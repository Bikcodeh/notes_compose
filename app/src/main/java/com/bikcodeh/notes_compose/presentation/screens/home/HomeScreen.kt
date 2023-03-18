@file:OptIn(ExperimentalFoundationApi::class)

package com.bikcodeh.notes_compose.presentation.screens.home

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.bikcodeh.notes_compose.R
import com.bikcodeh.notes_compose.domain.commons.fold
import com.bikcodeh.notes_compose.domain.repository.Diaries
import com.bikcodeh.notes_compose.presentation.components.DisplayAlertDialog

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@ExperimentalMaterial3Api
@Composable
fun HomeScreen(
    diaries: Diaries?,
    drawerState: DrawerState,
    onMenuClicked: () -> Unit,
    navigateToWriteScreen: () -> Unit,
    navigateToAuth: () -> Unit,
    onLogOut: () -> Unit
) {
    var paddingValues by remember { mutableStateOf(PaddingValues()) }
    var dialogOpened by remember { mutableStateOf(false) }

    NavigationDrawerNotes(
        drawerState = drawerState,
        onSignOutClicked = { dialogOpened = true }
    ) {
        Scaffold(
            topBar = {
                HomeTopBar(onMenuClicked = onMenuClicked)
            },
            floatingActionButton = {
                FloatingActionButton(
                    modifier = Modifier.padding(
                        end = paddingValues.calculateEndPadding(
                            LayoutDirection.Ltr
                        )
                    ),
                    onClick = navigateToWriteScreen
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit, contentDescription = stringResource(
                            id = R.string.edit_description
                        )
                    )
                }
            },
            content = {
                paddingValues = it
                diaries?.fold(
                    onSuccess = { response ->
                        HomeContent(paddingValues = it, diaries = response, onClick = {})
                    },
                    onError = {
                        EmptyPage(
                            title = stringResource(id = R.string.error_fetching_data),
                            subtitle = stringResource(id = R.string.try_again)
                        )
                    },
                    onLoading = {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                )
            }
        )
    }

    DisplayAlertDialog(
        title = stringResource(id = R.string.sign_out),
        message = stringResource(id = R.string.sign_out_message),
        dialogOpened = dialogOpened,
        onDialogClosed = { dialogOpened = false },
        onYesClicked = {
            onLogOut()
            navigateToAuth()
            dialogOpened = false
        }
    )
}

@Composable
fun NavigationDrawerNotes(
    drawerState: DrawerState,
    onSignOutClicked: () -> Unit,
    content: @Composable () -> Unit
) {
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(content = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        modifier = Modifier.size(250.dp),
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = stringResource(id = R.string.logo_description)
                    )
                }
                NavigationDrawerItem(
                    label = {
                        Row(modifier = Modifier.padding(horizontal = 12.dp)) {
                            Image(
                                painter = painterResource(id = R.drawable.google_logo),
                                contentDescription = stringResource(
                                    id = R.string.logo_description
                                )
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = stringResource(id = R.string.sign_out),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    },
                    selected = false,
                    onClick = onSignOutClicked
                )
            })
        },
        content = content
    )
}