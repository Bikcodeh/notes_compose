package com.bikcodeh.notes_compose.presentation.screens.home

import android.annotation.SuppressLint
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.bikcodeh.notes_compose.R

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@ExperimentalMaterial3Api
@Composable
fun HomeScreen(
    onMenuClicked: () -> Unit,
    navigateToWriteScreen: () -> Unit
) {
    Scaffold(
        topBar = {
            HomeTopBar(onMenuClicked = onMenuClicked)
        },
        floatingActionButton = {
            FloatingActionButton(onClick = navigateToWriteScreen) {
                Icon(
                    imageVector = Icons.Default.Edit, contentDescription = stringResource(
                        id = R.string.edit_description
                    )
                )
            }
        },
        content = {

        }
    )
}