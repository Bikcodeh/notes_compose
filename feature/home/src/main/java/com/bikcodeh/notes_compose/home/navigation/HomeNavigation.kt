@file:OptIn(ExperimentalFoundationApi::class)

package com.bikcodeh.notes_compose.home.navigation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.bikcodeh.notes_compode.ui.navigation.Screen
import com.bikcodeh.notes_compose.auth.screen.AuthenticationViewModel
import com.bikcodeh.notes_compose.data.repository.MongoDB
import com.bikcodeh.notes_compose.domain.commons.Result
import com.bikcodeh.notes_compose.home.R
import com.bikcodeh.notes_compose.home.screen.HomeScreen
import com.bikcodeh.notes_compose.home.screen.HomeViewModel
import com.bikcodeh.notes_compose.util.extension.toast
import kotlinx.coroutines.launch

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
            if (diaries !is Result.Loading) {
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