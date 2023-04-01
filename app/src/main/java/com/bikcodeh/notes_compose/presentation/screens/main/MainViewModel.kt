package com.bikcodeh.notes_compose.presentation.screens.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bikcodeh.notes_compose.data.local.database.dao.ImageToDeleteDao
import com.bikcodeh.notes_compose.data.local.database.dao.ImagesToUploadDao
import com.bikcodeh.notes_compose.data.mappers.ImageToDeleteMapper
import com.bikcodeh.notes_compose.data.mappers.ImageToUploadMapper
import com.example.domain.commons.DispatcherProvider
import com.bikcodeh.notes_compose.domain.repository.FirebaseUtility
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val uploadDao: ImagesToUploadDao,
    private val deleteDao: ImageToDeleteDao,
    private val firebaseUtility: FirebaseUtility,
    private val imageToUploadMapper: ImageToUploadMapper,
    private val imageToDeleteMapper: ImageToDeleteMapper,
    private val dispatcher: DispatcherProvider
) : ViewModel() {

    fun cleanUpImages() {
        viewModelScope.launch(dispatcher.io) {
            val result = uploadDao.getAllImages()
            result.forEach { imageToUploadEntity ->
                firebaseUtility.retryUploadingImageToFirebase(
                    imageToUpload = imageToUploadMapper.mapInverse(imageToUploadEntity),
                    onSuccess = { execute { uploadDao.cleanupImage(imageId = imageToUploadEntity.id) } }
                )
            }
            val result2 = deleteDao.getAllImages()
            result2.forEach { imageToDeleteEntity ->
                firebaseUtility.retryDeletingImageFromFirebase(
                    imageToDelete = imageToDeleteMapper.mapInverse(imageToDeleteEntity),
                    onSuccess = { execute { deleteDao.cleanupImage(imageId = imageToDeleteEntity.id) } }
                )
            }
        }
    }

    private fun execute(block: suspend () -> Unit) {
        viewModelScope.launch(dispatcher.main) {
            block()
        }
    }
}