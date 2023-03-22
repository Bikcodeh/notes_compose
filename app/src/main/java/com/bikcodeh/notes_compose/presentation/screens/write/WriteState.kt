package com.bikcodeh.notes_compose.presentation.screens.write

import com.bikcodeh.notes_compose.domain.model.Diary
import com.bikcodeh.notes_compose.domain.model.Mood
import io.realm.kotlin.types.RealmInstant

data class UiState(
    val selectedDiary: Diary? = null,
    val selectedDiaryId: String? = null,
    val title: String = "",
    val description: String = "",
    val mood: Mood = Mood.Neutral,
    val isLoading: Boolean = false,
    val error: Boolean = false,
    val updatedDateTime: RealmInstant? = null
)