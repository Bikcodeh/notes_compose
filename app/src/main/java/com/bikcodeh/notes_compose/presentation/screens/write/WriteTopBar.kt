@file:OptIn(ExperimentalMaterial3Api::class)

package com.bikcodeh.notes_compose.presentation.screens.write

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.bikcodeh.notes_compose.R
import com.bikcodeh.notes_compose.domain.model.Diary
import com.bikcodeh.notes_compose.presentation.components.DisplayAlertDialog

@Composable
fun WriteTopBar(
    selectedDiary: Diary?,
    onBack: () -> Unit,
    onDeleteConfirmed: () -> Unit
) {
    CenterAlignedTopAppBar(
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(id = R.string.back_description)
                )
            }
        },
        title = {
            Column() {
                Text(
                    text = "Happy",
                    modifier = Modifier.fillMaxWidth(),
                    style = TextStyle(
                        fontSize = MaterialTheme.typography.titleLarge.fontSize,
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "10 jan 2023, 10:00 AM",
                    modifier = Modifier.fillMaxWidth(),
                    style = TextStyle(fontSize = MaterialTheme.typography.bodySmall.fontSize),
                    textAlign = TextAlign.Center
                )
            }
        },
        actions = {
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = stringResource(id = R.string.date_description),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            if (selectedDiary != null) {
                DeleteDiaryAction(
                    onDeleteConfirmed = onDeleteConfirmed,
                    selectedDiary = selectedDiary
                )
            }
        }
    )
}

@Composable
fun DeleteDiaryAction(
    selectedDiary: Diary?,
    onDeleteConfirmed: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var openDialog by remember { mutableStateOf(false) }
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {
        DropdownMenuItem(
            text = {
                Text(text = "Delete")
            }, onClick = {
                openDialog = true
                expanded = false
            }
        )
    }
    DisplayAlertDialog(
        title = stringResource(id = R.string.delete),
        message = stringResource(id = R.string.delete_confirmation, selectedDiary?.title ?: ""),
        dialogOpened = openDialog,
        onDialogClosed = { openDialog = false },
        onYesClicked = onDeleteConfirmed
    )
    IconButton(onClick = { expanded = !expanded }) {
        Icon(
            imageVector = Icons.Default.MoreVert,
            contentDescription = stringResource(id = R.string.menu_description),
            tint = MaterialTheme.colorScheme.onSurface
        )
    }
}