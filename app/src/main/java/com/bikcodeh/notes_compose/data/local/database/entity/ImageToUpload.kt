package com.bikcodeh.notes_compose.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "images_to_upload")
data class ImageToUpload(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val remoteImagePath: String,
    val imageUri: String,
    val sessionUri: String
)
