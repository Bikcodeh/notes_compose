package com.bikcodeh.notes_compose.di

import android.content.Context
import androidx.room.Room
import com.bikcodeh.notes_compose.data.local.database.ImagesDatabase
import com.bikcodeh.notes_compose.data.local.database.dao.ImageToDeleteDao
import com.bikcodeh.notes_compose.data.local.database.dao.ImagesToUploadDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun providesDatabase(
        @ApplicationContext context: Context
    ): ImagesDatabase = Room.databaseBuilder(
        context = context,
        klass = ImagesDatabase::class.java,
        name = ImagesDatabase.DB_NAME
    ).build()

    @Provides
    @Singleton
    fun providesImageDao(database: ImagesDatabase): ImagesToUploadDao = database.imageToUploadDao()

    @Provides
    @Singleton
    fun providesDeleteImageDao(database: ImagesDatabase): ImageToDeleteDao = database.imageToDeleteDao()
}