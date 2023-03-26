package com.bikcodeh.notes_compose.presentation.screens.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bikcodeh.notes_compose.data.local.database.dao.ImageToDeleteDao
import com.bikcodeh.notes_compose.data.local.database.dao.ImagesToUploadDao
import com.bikcodeh.notes_compose.di.IoDispatcher
import com.bikcodeh.notes_compose.presentation.util.retryDeletingImageFromFirebase
import com.bikcodeh.notes_compose.presentation.util.retryUploadingImageToFirebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val uploadDao: ImagesToUploadDao,
    private val deleteDao: ImageToDeleteDao,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : ViewModel() {

    fun cleanUpImages() {
        viewModelScope.launch(dispatcher) {
            val result = uploadDao.getAllImages()
            result.forEach { imageToUpload ->
                retryUploadingImageToFirebase(
                    imageToUpload = imageToUpload,
                    onSuccess = { execute { uploadDao.cleanupImage(imageId = imageToUpload.id) } }
                )
            }
            val result2 = deleteDao.getAllImages()
            result2.forEach { imageToDelete ->
                retryDeletingImageFromFirebase(
                    imageToDelete = imageToDelete,
                    onSuccess = { execute { deleteDao.cleanupImage(imageId = imageToDelete.id) } }
                )
            }
        }
    }

    private fun execute(block: suspend () -> Unit) {
        viewModelScope.launch(dispatcher) {
            block()
        }
    }
}