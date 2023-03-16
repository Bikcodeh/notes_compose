package com.bikcodeh.notes_compose

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class NotesComposeApp: Application() {
    override fun onCreate() {
        super.onCreate()
    }
}