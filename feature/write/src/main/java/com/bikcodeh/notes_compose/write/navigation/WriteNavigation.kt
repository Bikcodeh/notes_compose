package com.bikcodeh.notes_compose.write.navigation

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.bikcodeh.notes_compode.ui.model.Mood
import com.bikcodeh.notes_compode.ui.navigation.Screen
import com.bikcodeh.notes_compose.util.extension.toast
import com.bikcodeh.notes_compose.write.screen.WriteScreen
import com.bikcodeh.notes_compose.write.screen.WriteViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState
import com.bikcodeh.notes_compose.ui.R as CoreR

@OptIn(ExperimentalPagerApi::class)
fun NavGraphBuilder.writeRoute(
    onBack: () -> Unit
) {
    composable(
        route = Screen.Write.route,
        arguments = listOf(navArgument(name = Screen.Write.WRITE_ARG_KEY) {
            type = NavType.StringType
            nullable = true
            defaultValue = null
        })
    ) {
        val viewModel: WriteViewModel = hiltViewModel()
        val uiState = viewModel.uiState
        val galleryState = viewModel.galleryState
        val pagerState = rememberPagerState()
        val pageNumber = remember { derivedStateOf { pagerState.currentPage } }
        val context = LocalContext.current

        WriteScreen(
            galleryState = galleryState,
            onBack = onBack,
            onDeleteConfirmed = {
                viewModel.deleteDiary(
                    onError = {
                        context.toast(CoreR.string.delete_error)
                    },
                    onSuccess = {
                        context.toast(CoreR.string.delete_success)
                        onBack()
                    }
                )
            },
            uiState = uiState,
            getData = {
                viewModel.fetchSelectedDiary()
            },
            onTitleChanged = {
                viewModel.setTitle(it)
            },
            onDescriptionChanged = {
                viewModel.setDescription(it)
            },
            onSaveClicked = {
                viewModel.upsertDiary(diary = it.apply {
                    mood = Mood.values()[pageNumber.value].name
                },
                    onSuccess = {
                        onBack()
                    },
                    onError = {
                        context.toast(CoreR.string.error_message)
                    })
            },
            moodName = { Mood.values()[pageNumber.value].name },
            pagerState = pagerState,
            onDateTimeUpdated = { time -> viewModel.updateDateTime(time) },
            onImageSelect = { uri ->
                val type = context.contentResolver.getType(uri)?.split("/")?.last() ?: "jpg"
                viewModel.addImage(image = uri, imageType = type)
            },
            onImageDeleteClicked = {
                galleryState.removeImage(it)
            }
        )
    }
}