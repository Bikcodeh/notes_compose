package com.bikcodeh.notes_compose.domain.repository

import android.net.Uri
import com.bikcodeh.notes_compose.domain.model.ImageToDelete
import com.bikcodeh.notes_compose.domain.model.ImageToUpload

interface FirebaseUtility {

    fun retryUploadingImageToFirebase(
        imageToUpload: ImageToUpload,
        onSuccess: () -> Unit
    )

    fun retryDeletingImageFromFirebase(
        imageToDelete: ImageToDelete,
        onSuccess: () -> Unit
    )

    fun fetchImagesFromFirebase(
        remoteImagePaths: List<String>,
        onImageDownload: (Uri) -> Unit,
        onImageDownloadFailed: (Exception) -> Unit = {},
        onReadyToDisplay: () -> Unit = {}
    )
}