package com.bikcodeh.notes_compose.domain.model

data class ImageToUpload(
    val id: Int,
    val remoteImagePath: String,
    val imageUri: String,
    val sessionUri: String
)
