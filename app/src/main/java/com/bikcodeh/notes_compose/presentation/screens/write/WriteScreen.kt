@file:OptIn(ExperimentalMaterial3Api::class)

package com.bikcodeh.notes_compose.presentation.screens.write

import android.annotation.SuppressLint
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import com.bikcodeh.notes_compose.domain.model.Diary


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun WriteScreen(
    selectedDiary: Diary?,
    onDeleteConfirmed: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            WriteTopBar(
                onBack = onBack,
                onDeleteConfirmed = onDeleteConfirmed,
                selectedDiary = selectedDiary
            )
        },
        content = {

        }
    )
}