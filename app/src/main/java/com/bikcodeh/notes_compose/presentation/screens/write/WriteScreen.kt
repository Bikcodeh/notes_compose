@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class)

package com.bikcodeh.notes_compose.presentation.screens.write

import android.annotation.SuppressLint
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import com.bikcodeh.notes_compose.domain.model.Mood
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun WriteScreen(
    uiState: UiState,
    onDeleteConfirmed: () -> Unit,
    onBack: () -> Unit,
    getData: () -> Unit
) {
    val pagerState = rememberPagerState()
    val pageNumber = remember { derivedStateOf { pagerState.currentPage } }

    LaunchedEffect(key1 = uiState.selectedDiaryId) {
     getData()
    }
    LaunchedEffect(key1 = uiState.mood) {
        pagerState.scrollToPage(Mood.valueOf(uiState.mood.name).ordinal)
    }
    Scaffold(
        topBar = {
            WriteTopBar(
                onBack = onBack,
                onDeleteConfirmed = onDeleteConfirmed,
                selectedDiary = uiState.selectedDiary,
                moodName = { Mood.values()[pageNumber.value].name }
            )
        },
        content = {
            WriteContent(
                paddingValues = it, pagerState = pagerState,
                title = uiState.title,
                onTitleChanged = {},
                description = uiState.description,
                onDescriptionChanged = {},
                uiState = uiState
            )
        }
    )
}