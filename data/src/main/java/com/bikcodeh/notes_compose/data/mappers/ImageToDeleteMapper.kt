package com.bikcodeh.notes_compose.data.mappers

import com.bikcodeh.notes_compose.domain.commons.Mapper
import com.bikcodeh.notes_compose.domain.model.ImageToDelete
import javax.inject.Inject
import com.bikcodeh.notes_compose.data.local.database.entity.ImageToDelete as ImageToDeleteEntity

class ImageToDeleteMapper @Inject constructor(): Mapper<ImageToDelete, ImageToDeleteEntity> {

    override fun map(input: ImageToDelete): ImageToDeleteEntity {
        return with(input) {
            ImageToDeleteEntity(id, remoteImagePath)
        }
    }

    override fun mapInverse(input: ImageToDeleteEntity): ImageToDelete {
        return with(input) {
            ImageToDelete(id, remoteImagePath)
        }
    }
}