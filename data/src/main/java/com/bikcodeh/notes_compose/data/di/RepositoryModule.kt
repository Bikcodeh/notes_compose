package com.bikcodeh.notes_compose.data.di

import com.bikcodeh.notes_compose.data.remote.FirebaseUtilityImpl
import com.bikcodeh.notes_compose.data.repository.AuthRepositoryImpl
import com.bikcodeh.notes_compose.domain.repository.AuthRepository
import com.bikcodeh.notes_compose.domain.repository.FirebaseUtility
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object RepositoryModule {

    @Provides
    @ViewModelScoped
    fun provideAuthRepository(): AuthRepository = AuthRepositoryImpl()

    @Provides
    @ViewModelScoped
    fun provideFirebaseRepository(): FirebaseUtility = FirebaseUtilityImpl()
}