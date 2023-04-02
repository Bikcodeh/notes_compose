package com.bikcodeh.notes_compose.data.remote

import android.net.Uri
import androidx.core.net.toUri
import com.bikcodeh.notes_compose.domain.model.ImageToDelete
import com.bikcodeh.notes_compose.domain.model.ImageToUpload
import com.bikcodeh.notes_compose.domain.repository.FirebaseUtility
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storageMetadata
import javax.inject.Inject

class FirebaseUtilityImpl @Inject constructor(): FirebaseUtility {
    override fun retryUploadingImageToFirebase(
        imageToUpload: ImageToUpload,
        onSuccess: () -> Unit
    ) {
        val storage = FirebaseStorage.getInstance().reference
        storage.child(imageToUpload.remoteImagePath).putFile(
            imageToUpload.imageUri.toUri(),
            storageMetadata { },
            imageToUpload.sessionUri.toUri()
        ).addOnSuccessListener { onSuccess() }
    }

    override fun retryDeletingImageFromFirebase(
        imageToDelete: ImageToDelete,
        onSuccess: () -> Unit
    ) {
        val storage = FirebaseStorage.getInstance().reference
        storage.child(imageToDelete.remoteImagePath).delete()
            .addOnSuccessListener { onSuccess() }
    }

    /**
     * Download images from Firebase asynchronously.
     * This function returns imageUri after each successful download.
     * */
    override fun fetchImagesFromFirebase(
        remoteImagePaths: List<String>,
        onImageDownload: (Uri) -> Unit,
        onImageDownloadFailed: (Exception) -> Unit,
        onReadyToDisplay: () -> Unit
    ) {
        if (remoteImagePaths.isNotEmpty()) {
            remoteImagePaths.forEachIndexed { index, remoteImagePath ->
                if (remoteImagePath.trim().isNotEmpty()) {
                    FirebaseStorage.getInstance().reference.child(remoteImagePath.trim()).downloadUrl
                        .addOnSuccessListener {
                            onImageDownload(it)
                            if (remoteImagePaths.lastIndexOf(remoteImagePaths.last()) == index) {
                                onReadyToDisplay()
                            }
                        }.addOnFailureListener {
                            onImageDownloadFailed(it)
                        }
                }
            }
        }
    }
}