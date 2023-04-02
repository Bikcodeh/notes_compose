@file:OptIn(ExperimentalPagerApi::class)

package com.bikcodeh.notes_compose.ui.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.bikcodeh.notes_compode.ui.model.Mood
import com.bikcodeh.notes_compode.ui.navigation.Screen
import com.bikcodeh.notes_compose.R
import com.bikcodeh.notes_compose.auth.navigation.authenticationRoute
import com.bikcodeh.notes_compose.home.navigation.homeRoute
import com.bikcodeh.notes_compose.presentation.screens.write.WriteScreen
import com.bikcodeh.notes_compose.presentation.screens.write.WriteViewModel
import com.bikcodeh.notes_compose.util.extension.toast
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState

@ExperimentalMaterial3Api
@Composable
fun SetupNavGraph(
    startDestination: String,
    navController: NavHostController,
    onDataLoaded: () -> Unit
) {
    NavHost(
        startDestination = startDestination,
        navController = navController
    ) {
        authenticationRoute(
            navigateToHome = {
                navController.popBackStack()
                navController.navigate(Screen.Home.route)
            },
            onDataLoaded = onDataLoaded
        )
        homeRoute(
            navigateToWrite = {
                navController.navigate(Screen.Write.route)
            },
            navigateToWriteWithArgs = {
                navController.navigate(Screen.Write.passDiaryId(it))
            },
            navigateToAuth = {
                navController.popBackStack()
                navController.navigate(Screen.Authentication.route)
            },
            onDataLoaded = onDataLoaded
        )
        writeRoute(
            onBack = {
                navController.popBackStack()
            }
        )
    }
}

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
                        context.toast(R.string.delete_error)
                    },
                    onSuccess = {
                        context.toast(R.string.delete_success)
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
                        context.toast(R.string.error_message)
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