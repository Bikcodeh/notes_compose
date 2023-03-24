package com.bikcodeh.notes_compose.presentation.screens.write

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bikcodeh.notes_compose.data.repository.MongoDB
import com.bikcodeh.notes_compose.di.IoDispatcher
import com.bikcodeh.notes_compose.domain.commons.fold
import com.bikcodeh.notes_compose.domain.model.Diary
import com.bikcodeh.notes_compose.domain.model.GalleryImage
import com.bikcodeh.notes_compose.domain.model.GalleryState
import com.bikcodeh.notes_compose.domain.model.Mood
import com.bikcodeh.notes_compose.presentation.util.getBsonObjectId
import com.bikcodeh.notes_compose.presentation.util.toRealmInstant
import com.bikcodeh.notes_compose.ui.navigation.Screen
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class WriteViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    @IoDispatcher private val dispatcher: CoroutineDispatcher = Dispatchers.IO
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
        viewModelScope.launch(dispatcher) {
            if (uiState.selectedDiaryId != null) {
                updateDiary(diary = diary, onSuccess = onSuccess, onError = onError)
            } else {
                insertDiary(diary = diary, onSuccess = onSuccess, onError = onError)
            }
        }
    }

    private fun insertDiary(
        diary: Diary,
        onSuccess: () -> Unit,
        onError: () -> Unit
    ) {
        viewModelScope.launch(dispatcher) {
            MongoDB.addNewDiary(diary = diary.apply {
                if (uiState.updatedDateTime != null) {
                    date = uiState.updatedDateTime!!
                }
            }).fold(
                onSuccess = { execute(onSuccess) },
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
        viewModelScope.launch(dispatcher) {
            MongoDB.updateDiary(diary.apply {
                _id = org.mongodb.kbson.ObjectId(getBsonObjectId(uiState.selectedDiaryId))
                date = if (uiState.updatedDateTime != null) {
                    uiState.updatedDateTime!!
                } else {
                    uiState.selectedDiary!!.date
                }
            })
                .fold(
                    onSuccess = { execute(onSuccess) },
                    onError = { execute(onError) },
                    onLoading = {}
                )
        }
    }

    fun deleteDiary(
        onSuccess: () -> Unit,
        onError: () -> Unit
    ) {
        viewModelScope.launch(dispatcher) {
            if (uiState.selectedDiaryId != null) {
                MongoDB.deleteDiary(id = org.mongodb.kbson.ObjectId(getBsonObjectId(uiState.selectedDiaryId)))
                    .fold(
                        onSuccess = {
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

    private suspend fun execute(action: () -> Unit) {
        withContext(Dispatchers.Main) {
            action()
        }
    }
}