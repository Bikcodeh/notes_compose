package com.bikcodeh.notes_compose.data.di

import com.bikcodeh.notes_compose.data.mappers.ImageToDeleteMapper
import com.bikcodeh.notes_compose.data.mappers.ImageToUploadMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object MappersModule {

    @Provides
    @ViewModelScoped
    fun provideImageToUploadMapper() = ImageToUploadMapper()


    @Provides
    @ViewModelScoped
    fun provideImageToDeleteMapper() = ImageToDeleteMapper()
}