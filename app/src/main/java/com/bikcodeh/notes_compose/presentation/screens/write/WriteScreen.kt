@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class)

package com.bikcodeh.notes_compose.presentation.screens.write

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.bikcodeh.notes_compose.domain.model.Diary
import com.bikcodeh.notes_compose.domain.model.GalleryImage
import com.bikcodeh.notes_compose.domain.model.GalleryState
import com.bikcodeh.notes_compose.domain.model.Mood
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import java.time.ZonedDateTime

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun WriteScreen(
    galleryState: GalleryState,
    uiState: UiState,
    pagerState: PagerState,
    moodName: () -> String,
    onDeleteConfirmed: () -> Unit,
    onBack: () -> Unit,
    getData: () -> Unit,
    onTitleChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onSaveClicked: (Diary) -> Unit,
    onDateTimeUpdated: (ZonedDateTime) -> Unit,
    onImageSelect: (Uri) -> Unit
) {
    LaunchedEffect(key1 = uiState.selectedDiaryId) {
        getData()
    }
    LaunchedEffect(key1 = uiState.mood) {
        pagerState.scrollToPage(Mood.valueOf(uiState.mood.name).ordinal)
    }
    var selectedGalleryImage by remember { mutableStateOf<GalleryImage?>(null) }
    Scaffold(
        topBar = {
            WriteTopBar(
                onBack = onBack,
                onDeleteConfirmed = onDeleteConfirmed,
                selectedDiary = uiState.selectedDiary,
                moodName = moodName,
                onDateTimeUpdated = onDateTimeUpdated
            )
        },
        content = {
            WriteContent(
                galleryState = galleryState,
                paddingValues = it,
                pagerState = pagerState,
                title = uiState.title,
                onTitleChanged = onTitleChanged,
                description = uiState.description,
                onDescriptionChanged = onDescriptionChanged,
                uiState = uiState,
                onSaveClicked = onSaveClicked,
                onImageSelect = onImageSelect,
                onImageClicked = {  galleryImage ->
                    selectedGalleryImage = galleryImage
                }
            )
        }
    )
}