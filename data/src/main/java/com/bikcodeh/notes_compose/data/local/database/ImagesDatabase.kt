package com.bikcodeh.notes_compose.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.bikcodeh.notes_compose.data.local.database.dao.ImageToDeleteDao
import com.bikcodeh.notes_compose.data.local.database.dao.ImagesToUploadDao
import com.bikcodeh.notes_compose.data.local.database.entity.ImageToDelete
import com.bikcodeh.notes_compose.data.local.database.entity.ImageToUpload

@Database(
    entities = [ImageToUpload::class, ImageToDelete::class],
    version = 1,
    exportSchema = false
)
abstract class ImagesDatabase: RoomDatabase() {
    abstract fun imageToUploadDao(): ImagesToUploadDao
    abstract fun imageToDeleteDao(): ImageToDeleteDao
    companion object {
        const val DB_NAME = "images_db"
    }
}