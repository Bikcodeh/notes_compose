package com.bikcodeh.notes_compose.presentation.screens.home

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.unit.dp
import com.bikcodeh.notes_compose.R
import com.bikcodeh.notes_compose.presentation.components.DisplayAlertDialog

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@ExperimentalMaterial3Api
@Composable
fun HomeScreen(
    drawerState: DrawerState,
    onMenuClicked: () -> Unit,
    navigateToWriteScreen: () -> Unit,
    navigateToAuth: () -> Unit,
    onLogOut: () -> Unit
) {
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