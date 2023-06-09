@file:OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class)

package com.bikcodeh.notes_compose.write.screen

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.bikcodeh.notes_compode.ui.components.gallery.GalleryImage
import com.bikcodeh.notes_compode.ui.components.gallery.GalleryState
import com.bikcodeh.notes_compode.ui.components.gallery.GalleryUploader
import com.bikcodeh.notes_compode.ui.model.Mood
import com.bikcodeh.notes_compose.domain.model.Diary
import com.bikcodeh.notes_compose.util.extension.toast
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import io.realm.kotlin.ext.toRealmList
import kotlinx.coroutines.launch
import com.bikcodeh.notes_compose.ui.R as CoreR

@ExperimentalMaterial3Api
@Composable
fun WriteContent(
    galleryState: GalleryState,
    uiState: UiState,
    paddingValues: PaddingValues,
    pagerState: PagerState,
    title: String,
    onTitleChanged: (String) -> Unit,
    description: String,
    onDescriptionChanged: (String) -> Unit,
    onSaveClicked: (Diary) -> Unit,
    onImageSelect: (Uri) -> Unit,
    onImageClicked: (GalleryImage) -> Unit
) {
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    LaunchedEffect(key1 = scrollState.maxValue) {
        scrollState.scrollTo(scrollState.maxValue)
    }
    Column(
        modifier = Modifier
            .imePadding()
            .navigationBarsPadding()
            .fillMaxSize()
            .padding(
                top = paddingValues.calculateTopPadding(),
                bottom = paddingValues.calculateBottomPadding()
            )
            .padding(horizontal = 24.dp)
            .padding(bottom = 24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(30.dp))
            HorizontalPager(
                state = pagerState,
                count = Mood.values().size
            ) { page ->
                AsyncImage(
                    modifier = Modifier.size(120.dp),
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(Mood.values()[page].icon)
                        .crossfade(true)
                        .build(),
                    contentDescription = stringResource(
                        id = CoreR.string.feel_description
                    )
                )
            }
            Spacer(modifier = Modifier.height(30.dp))
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = title,
                onValueChange = onTitleChanged,
                placeholder = { Text(text = stringResource(id = CoreR.string.title_hint)) },
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Unspecified,
                    disabledIndicatorColor = Color.Unspecified,
                    unfocusedIndicatorColor = Color.Unspecified,
                    unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                ),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        coroutineScope.launch { scrollState.scrollTo(Int.MAX_VALUE) }
                        focusManager.moveFocus(FocusDirection.Next)
                    },
                ),
                maxLines = 1,
                singleLine = true
            )
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = description,
                onValueChange = onDescriptionChanged,
                placeholder = { Text(text = stringResource(id = CoreR.string.tell_me_about_it_hint)) },
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Unspecified,
                    disabledIndicatorColor = Color.Unspecified,
                    unfocusedIndicatorColor = Color.Unspecified,
                    unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                ),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.clearFocus() },
                )
            )
        }
        Column(
            modifier = Modifier,
            verticalArrangement = Arrangement.Bottom
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            GalleryUploader(
                galleryState = galleryState,
                onAddClicked = { focusManager.clearFocus() },
                onImageSelect = onImageSelect,
                onImageClicked = onImageClicked
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = {
                    if (uiState.title.isNotEmpty() && uiState.description.isNotEmpty()) {
                        onSaveClicked(
                            Diary().apply {
                                this.title = uiState.title
                                this.description = uiState.description
                                this.images = galleryState.images.map { it.remoteImagePath }.toRealmList()
                            }
                        )
                    } else {
                        context.toast(CoreR.string.required_fields)
                    }
                }, modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = Shapes().small
            ) {
                Text(text = stringResource(id = CoreR.string.save))
            }
        }
    }
}