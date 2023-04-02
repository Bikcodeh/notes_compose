package com.bikcodeh.notes_compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.bikcodeh.notes_compode.ui.navigation.Screen
import com.bikcodeh.notes_compode.ui.theme.Notes_ComposeTheme
import com.bikcodeh.notes_compose.presentation.screens.main.MainViewModel
import com.bikcodeh.notes_compose.ui.navigation.SetupNavGraph
import dagger.hilt.android.AndroidEntryPoint
import io.realm.kotlin.mongodb.App

@ExperimentalMaterial3Api
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    private var keepSplashOpened = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().setKeepOnScreenCondition { keepSplashOpened }
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            Notes_ComposeTheme {
                val navController = rememberNavController()
                SetupNavGraph(
                    startDestination = getStartDestination(),
                    navController = navController,
                    onDataLoaded = {
                        keepSplashOpened = false
                    }
                )
            }
        }
        mainViewModel.cleanUpImages()
    }
}

private fun getStartDestination(): String {
    val user = App.Companion.create(com.bikcodeh.notes_compose.domain.BuildConfig.APP_ID).currentUser
    return if (user != null && user.loggedIn) Screen.Home.route else Screen.Authentication.route
}