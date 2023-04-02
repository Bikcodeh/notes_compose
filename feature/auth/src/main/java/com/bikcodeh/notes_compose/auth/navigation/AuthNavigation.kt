package com.bikcodeh.notes_compose.auth.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.bikcodeh.notes_compode.ui.navigation.Screen
import com.bikcodeh.notes_compose.auth.R
import com.bikcodeh.notes_compose.auth.screen.AuthenticationScreen
import com.bikcodeh.notes_compose.auth.screen.AuthenticationViewModel
import com.stevdzasan.messagebar.rememberMessageBarState
import com.stevdzasan.onetap.rememberOneTapSignInState

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

        LaunchedEffect(key1 = kotlin.Unit) {
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