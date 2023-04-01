package com.bikcodeh.notes_compose.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bikcodeh.notes_compose.data.local.database.entity.ImageToUpload

@Dao
interface ImagesToUploadDao {

    @Query("SELECT * FROM image_to_upload ORDER BY id ASC")
    suspend fun getAllImages(): List<ImageToUpload>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addImageToUpload(imageToUpload: ImageToUpload)

    @Query("DELETE FROM image_to_upload WHERE id=:imageId")
    suspend fun cleanupImage(imageId: Int)
}