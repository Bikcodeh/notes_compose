package com.bikcodeh.notes_compode.ui.global

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.bikcodeh.notes_compose.domain.repository.FirebaseUtility
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FirebaseUtilityViewModel @Inject constructor(
    private val firebaseUtility: FirebaseUtility
) : ViewModel() {

    fun fetchImageFromFirebase(
        remoteImagePaths: List<String>,
        onImageDownload: (Uri) -> Unit,
        onImageDownloadFailed: (Exception) -> Unit = {},
        onReadyToDisplay: () -> Unit = {}
    ) {
        firebaseUtility.fetchImagesFromFirebase(
            remoteImagePaths,
            onImageDownload,
            onImageDownloadFailed,
            onReadyToDisplay
        )
    }
}