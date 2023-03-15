package com.bikcodeh.notes_compose.di

import com.bikcodeh.notes_compose.data.repository.AuthRepositoryImpl
import com.bikcodeh.notes_compose.domain.repository.AuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
class RepositoryModule {

    @Provides
    @ViewModelScoped
    fun provideAuthRepository(): AuthRepository = AuthRepositoryImpl()
}