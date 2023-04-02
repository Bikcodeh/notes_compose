package com.bikcodeh.notes_compose.write.screen

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bikcodeh.notes_compode.ui.components.gallery.GalleryImage
import com.bikcodeh.notes_compode.ui.components.gallery.GalleryState
import com.bikcodeh.notes_compode.ui.model.Mood
import com.bikcodeh.notes_compode.ui.navigation.Screen
import com.bikcodeh.notes_compose.data.local.database.dao.ImageToDeleteDao
import com.bikcodeh.notes_compose.data.local.database.dao.ImagesToUploadDao
import com.bikcodeh.notes_compose.data.local.database.entity.ImageToDelete
import com.bikcodeh.notes_compose.data.local.database.entity.ImageToUpload
import com.bikcodeh.notes_compose.data.repository.MongoDB
import com.bikcodeh.notes_compose.domain.commons.DispatcherProvider
import com.bikcodeh.notes_compose.domain.commons.fold
import com.bikcodeh.notes_compose.domain.model.Diary
import com.bikcodeh.notes_compose.domain.repository.FirebaseUtility
import com.bikcodeh.notes_compose.util.extractImagePath
import com.bikcodeh.notes_compose.util.getBsonObjectId
import com.bikcodeh.notes_compose.util.toRealmInstant
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class WriteViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val uploadDao: ImagesToUploadDao,
    private val deleteDao: ImageToDeleteDao,
    private val dispatcher: DispatcherProvider,
    private val firebaseUtility: FirebaseUtility
) : ViewModel() {

    val galleryState = GalleryState()
    var uiState by mutableStateOf(UiState())
        private set

    init {
        getDiaryIdArgument()
    }

    private fun getDiaryIdArgument() {
        uiState = uiState.copy(
            selectedDiaryId = savedStateHandle.get<String>(
                key = Screen.Write.WRITE_ARG_KEY
            )
        )
    }

    fun fetchSelectedDiary() {
        if (uiState.selectedDiaryId != null) {
            uiState = uiState.copy(isLoading = true, error = false)
            viewModelScope.launch {
                MongoDB.getSelectedDiary(
                    diaryId = org.mongodb.kbson.ObjectId(getBsonObjectId(uiState.selectedDiaryId))
                ).fold(
                    onSuccess = {
                        uiState = uiState.copy(
                            title = it.title,
                            description = it.description,
                            mood = Mood.valueOf(it.mood),
                            isLoading = false,
                            selectedDiaryId = it._id.toString(),
                            error = false,
                            selectedDiary = it
                        )
                        firebaseUtility.fetchImagesFromFirebase(
                            remoteImagePaths = it.images,
                            onImageDownload = { uri ->
                                galleryState.addImage(
                                    GalleryImage(
                                        image = uri,
                                        remoteImagePath = extractImagePath(
                                            fullImageUrl = uri.toString()
                                        )
                                    )
                                )
                            }
                        )
                    },
                    onError = {
                        uiState = uiState.copy(
                            title = "",
                            description = "",
                            mood = Mood.Neutral,
                            isLoading = false,
                            error = true,
                            selectedDiaryId = null,
                            selectedDiary = null
                        )

                    },
                    onLoading = {}
                )
            }
        }
    }

    fun setTitle(title: String) {
        uiState = uiState.copy(title = title)
    }

    fun setDescription(description: String) {
        uiState = uiState.copy(description = description)
    }

    fun updateDateTime(zonedDateTime: ZonedDateTime) {
        uiState = uiState.copy(updatedDateTime = zonedDateTime.toInstant().toRealmInstant())
    }

    fun upsertDiary(
        diary: Diary,
        onSuccess: () -> Unit,
        onError: () -> Unit
    ) {
        viewModelScope.launch(dispatcher.io) {
            if (uiState.selectedDiaryId != null) {
                updateDiary(diary = diary, onSuccess = onSuccess, onError = onError)
            } else {
                insertDiary(diary = diary, onSuccess = onSuccess, onError = onError)
            } }
    }

    private fun insertDiary(
        diary: Diary,
        onSuccess: () -> Unit,
        onError: () -> Unit
    ) {
        viewModelScope.launch(dispatcher.io) {
            MongoDB.addNewDiary(diary = diary.apply {
                if (uiState.updatedDateTime != null) {
                    date = uiState.updatedDateTime!!
                }
            }).fold(
                onSuccess = {
                    uploadImagesToFirebase()
                    execute(onSuccess)
                },
                onError = { execute(onError) },
                onLoading = {}
            )
        }
    }

    private fun updateDiary(
        diary: Diary,
        onSuccess: () -> Unit,
        onError: () -> Unit
    ) {
        viewModelScope.launch(dispatcher.io) {
            MongoDB.updateDiary(diary.apply {
                _id = org.mongodb.kbson.ObjectId(getBsonObjectId(uiState.selectedDiaryId))
                date = if (uiState.updatedDateTime != null) {
                    uiState.updatedDateTime!!
                } else {
                    uiState.selectedDiary!!.date
                }
            })
                .fold(
                    onSuccess = {
                        uploadImagesToFirebase()
                        deleteImagesFromFirebase()
                        execute(onSuccess)
                    },
                    onError = { execute(onError) },
                    onLoading = {}
                )
        }
    }

    fun deleteDiary(
        onSuccess: () -> Unit,
        onError: () -> Unit
    ) {
        viewModelScope.launch(dispatcher.io) {
            if (uiState.selectedDiaryId != null) {
                MongoDB.deleteDiary(id = org.mongodb.kbson.ObjectId(getBsonObjectId(uiState.selectedDiaryId)))
                    .fold(
                        onSuccess = {
                            deleteImagesFromFirebase(uiState.selectedDiary?.images)
                            uiState = uiState.copy(selectedDiaryId = null, selectedDiary = null)
                            execute(onSuccess)
                        },
                        onError = { execute(onError) },
                        onLoading = {}
                    )
            }
        }
    }

    fun addImage(image: Uri, imageType: String) {
        val remoteImagePath = "images/${FirebaseAuth.getInstance().currentUser?.uid}/" +
                "${image.lastPathSegment}-${System.currentTimeMillis()}.$imageType"
        galleryState.addImage(
            GalleryImage(
                image = image,
                remoteImagePath = remoteImagePath
            )
        )
    }

    private fun uploadImagesToFirebase() {
        val storage = FirebaseStorage.getInstance().reference
        galleryState.images.forEach { galleryImage ->
            val imagePath = storage.child(galleryImage.remoteImagePath)
            imagePath.putFile(galleryImage.image)
                .addOnProgressListener {
                    it.uploadSessionUri?.let { sessionUri ->
                        viewModelScope.launch(dispatcher.io) {
                            uploadDao.addImageToUpload(
                                ImageToUpload(
                                    remoteImagePath = galleryImage.remoteImagePath,
                                    imageUri = galleryImage.image.toString(),
                                    sessionUri = sessionUri.toString()
                                )
                            )
                        }
                    }
                }
        }
    }

    private fun deleteImagesFromFirebase(images: List<String>? = null) {
        val storage = FirebaseStorage.getInstance().reference
        if (images != null) {
            images.forEach { remotePath ->
                storage.child(remotePath).delete()
                    .addOnFailureListener {
                        viewModelScope.launch(dispatcher.io) {
                            deleteDao.addImageToDelete(
                                ImageToDelete(remoteImagePath = remotePath)
                            )
                        }
                    }
            }
        } else {
            galleryState.imagesToBeDeleted.map { it.remoteImagePath }.forEach { remotePath ->
                storage.child(remotePath).delete()
                    .addOnFailureListener {
                        viewModelScope.launch(dispatcher.io) {
                            deleteDao.addImageToDelete(
                                ImageToDelete(remoteImagePath = remotePath)
                            )
                        }
                    }
            }
        }
    }

    private suspend fun execute(action: () -> Unit) {
        withContext(dispatcher.main) {
            action()
        }
    }
}