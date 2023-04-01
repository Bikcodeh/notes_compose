package com.bikcodeh.notes_compose.data.di

import com.bikcodeh.notes_compose.data.repository.DispatcherProviderImpl
import com.bikcodeh.notes_compose.domain.commons.DispatcherProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DispatcherModule {

    @Provides
    @Singleton
    fun provideDispatcherProvider(): DispatcherProvider = DispatcherProviderImpl()
}