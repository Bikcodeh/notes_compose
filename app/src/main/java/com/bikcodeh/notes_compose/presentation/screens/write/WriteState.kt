package com.bikcodeh.notes_compose.presentation.screens.write

import com.bikcodeh.notes_compose.domain.model.Mood

data class UiState(
    val selectedDiary: String? = null,
    val title: String = "",
    val description: String = "",
    val mood: Mood = Mood.Neutral
)