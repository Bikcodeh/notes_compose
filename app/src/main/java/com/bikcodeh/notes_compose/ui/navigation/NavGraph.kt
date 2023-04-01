@file:OptIn(ExperimentalPagerApi::class)

package com.bikcodeh.notes_compose.ui.navigation

import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.bikcodeh.notes_compode.ui.model.Mood
import com.bikcodeh.notes_compose.R
import com.bikcodeh.notes_compose.data.repository.MongoDB
import com.bikcodeh.notes_compose.presentation.screens.auth.AuthenticationScreen
import com.bikcodeh.notes_compose.presentation.screens.auth.AuthenticationViewModel
import com.bikcodeh.notes_compose.presentation.screens.home.HomeScreen
import com.bikcodeh.notes_compose.presentation.screens.home.HomeViewModel
import com.bikcodeh.notes_compose.presentation.screens.write.WriteScreen
import com.bikcodeh.notes_compose.presentation.screens.write.WriteViewModel
import com.bikcodeh.notes_compose.util.extension.toast
import com.example.domain.commons.Result.Loading
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState
import com.stevdzasan.messagebar.rememberMessageBarState
import com.stevdzasan.onetap.rememberOneTapSignInState
import kotlinx.coroutines.launch

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

@ExperimentalMaterial3Api
fun NavGraphBuilder.authenticationRoute(
    navigateToHome: () -> Unit,
    onDataLoaded: () -> Unit
) {
    composable(route = Screen.Authentication.route) {
        val viewModel: AuthenticationViewModel = hiltViewModel()
        val loadingState by viewModel.loadingState
        val authenticated by viewModel.authenticated
        val oneTapState = rememberOneTapSignInState()
        val messageBarState = rememberMessageBarState()
        val context = LocalContext.current

        LaunchedEffect(key1 = Unit) {
            onDataLoaded()
        }
        AuthenticationScreen(
            authenticated = authenticated,
            loadingState = loadingState,
            oneTapState = oneTapState,
            messageBarState = messageBarState,
            onButtonClicked = {
                oneTapState.open()
                viewModel.setLoading(true)
            },
            onSuccessfulFirebaseSignIn = {
                viewModel.signInWithMongoAtlas(it,
                    onSuccess = {
                        messageBarState.addSuccess(context.getString(R.string.login_success))
                        viewModel.setLoading(false)
                    }, onError = {
                        messageBarState.addError(Exception(context.getString(R.string.error_login)))
                        viewModel.setLoading(false)
                    })
            },
            onFailedFirebaseSignIn = {
                messageBarState.addError(Exception(context.getString(R.string.error_login)))
                viewModel.setLoading(false)
            },
            onDialogDismissed = {
                messageBarState.addError(Exception(it))
                viewModel.setLoading(false)
            },
            navigateToHome = navigateToHome
        )
    }
}

@ExperimentalMaterial3Api
fun NavGraphBuilder.homeRoute(
    navigateToWrite: () -> Unit,
    navigateToWriteWithArgs: (String) -> Unit,
    navigateToAuth: () -> Unit,
    onDataLoaded: () -> Unit
) {
    composable(route = Screen.Home.route) {
        val viewModel: HomeViewModel = hiltViewModel()
        val diaries by viewModel.diaries
        val authViewModel: AuthenticationViewModel = hiltViewModel()
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val scope = rememberCoroutineScope()
        val context = LocalContext.current

        LaunchedEffect(key1 = diaries) {
            if (diaries !is Loading) {
                onDataLoaded()
            }
        }
        HomeScreen(
            diaries = diaries,
            onMenuClicked = {
                scope.launch { drawerState.open() }
            },
            navigateToWriteScreen = navigateToWrite,
            navigateToWriteWithArgs = navigateToWriteWithArgs,
            drawerState = drawerState,
            navigateToAuth = navigateToAuth,
            onLogOut = { authViewModel.logOut() },
            deleteAlliDiaries = {
                viewModel.deleteAllDiaries(
                    onSuccess = {
                        context.toast(R.string.all_diaries_deleted)
                        scope.launch { drawerState.close() }
                    },
                    onError = { messageResId ->
                        context.toast(messageResId)
                        scope.launch { drawerState.close() }
                    }
                )
            },
            onDateReset = {
                viewModel.getDiaries()
            },
            onDateSelected = {
                viewModel.getDiaries(it)
            },
            dateIsSelected = viewModel.dateIsSelected
        )
        LaunchedEffect(key1 = Unit) {
            MongoDB.configureRealm()
        }
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