package com.bikcodeh.notes_compose.write.screen

import com.bikcodeh.notes_compode.ui.model.Mood
import com.bikcodeh.notes_compose.domain.model.Diary
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