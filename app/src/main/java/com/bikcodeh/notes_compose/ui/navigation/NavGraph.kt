package com.bikcodeh.notes_compose.ui.navigation

import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.bikcodeh.notes_compose.R
import com.bikcodeh.notes_compose.data.repository.MongoDB
import com.bikcodeh.notes_compose.domain.model.Diary
import com.bikcodeh.notes_compose.presentation.screens.auth.AuthenticationScreen
import com.bikcodeh.notes_compose.presentation.screens.auth.AuthenticationViewModel
import com.bikcodeh.notes_compose.presentation.screens.home.HomeScreen
import com.bikcodeh.notes_compose.presentation.screens.home.HomeViewModel
import com.bikcodeh.notes_compose.presentation.screens.write.WriteScreen
import com.bikcodeh.notes_compose.presentation.screens.write.WriteViewModel
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
            onTokenIdReceived = {
                viewModel.signInWithMongoAtlas(it,
                    onSuccess = {
                        messageBarState.addSuccess(context.getString(R.string.login_success))
                        viewModel.setLoading(false)
                    }, onError = {
                        messageBarState.addError(Exception(context.getString(R.string.error_login)))
                        viewModel.setLoading(false)
                    })
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

        LaunchedEffect(key1 = diaries) {
            if (diaries !is com.bikcodeh.notes_compose.domain.commons.Result.Loading) {
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
            onLogOut = { authViewModel.logOut() }
        )
        LaunchedEffect(key1 = Unit) {
            MongoDB.configureRealm()
        }
    }
}

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
        WriteScreen(
            selectedDiary = Diary().apply {
                title = "Title"
                description = "Some random text"
            },
            onBack = onBack,
            onDeleteConfirmed = {}
        )
    }
}