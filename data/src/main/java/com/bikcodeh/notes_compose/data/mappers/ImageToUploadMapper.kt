package com.bikcodeh.notes_compose.data.mappers

import com.bikcodeh.notes_compose.domain.commons.Mapper
import com.bikcodeh.notes_compose.domain.model.ImageToUpload
import javax.inject.Inject
import com.bikcodeh.notes_compose.data.local.database.entity.ImageToUpload as ImageToUploadEntity

class ImageToUploadMapper @Inject constructor(): Mapper<ImageToUpload, ImageToUploadEntity> {

    override fun map(input: ImageToUpload): ImageToUploadEntity {
        return with(input) {
            ImageToUploadEntity(id, remoteImagePath, imageUri, sessionUri)
        }
    }

    override fun mapInverse(input: ImageToUploadEntity): ImageToUpload {
        return with(input) {
            ImageToUpload(id, remoteImagePath, imageUri, sessionUri)
        }
    }
}