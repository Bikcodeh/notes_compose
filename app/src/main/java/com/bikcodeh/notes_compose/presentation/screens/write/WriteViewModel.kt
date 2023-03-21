package com.bikcodeh.notes_compose.presentation.screens.write

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bikcodeh.notes_compose.data.repository.MongoDB
import com.bikcodeh.notes_compose.di.IoDispatcher
import com.bikcodeh.notes_compose.domain.commons.fold
import com.bikcodeh.notes_compose.domain.model.Diary
import com.bikcodeh.notes_compose.domain.model.Mood
import com.bikcodeh.notes_compose.presentation.util.getBsonObjectId
import com.bikcodeh.notes_compose.ui.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class WriteViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    @IoDispatcher private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    var uiState by mutableStateOf(UiState())
        private set

    init {
        getDiaryIdArgument()
    }

    private fun getDiaryIdArgument() {
        uiState = uiState.copy(
            selectedDiaryId = savedStateHandle.get<String>(
                key = Screen.Write.WRITE_ARG_KEY
            )
        )
    }

    fun fetchSelectedDiary() {
        if (uiState.selectedDiaryId != null) {
            uiState = uiState.copy(isLoading = true, error = false)
            viewModelScope.launch {
                MongoDB.getSelectedDiary(
                    diaryId = org.mongodb.kbson.ObjectId(getBsonObjectId(uiState.selectedDiaryId))
                ).collect { result ->
                    result.fold(
                        onSuccess = {
                            uiState = uiState.copy(
                                title = it.title,
                                description = it.description,
                                mood = Mood.valueOf(it.mood),
                                isLoading = false,
                                selectedDiaryId = it._id.toString(),
                                error = false,
                                selectedDiary = it
                            )
                        },
                        onError = {
                            uiState = uiState.copy(
                                title = "",
                                description = "",
                                mood = Mood.Neutral,
                                isLoading = false,
                                error = true,
                                selectedDiaryId = null,
                                selectedDiary = null
                            )

                        },
                        onLoading = {}
                    )
                }
            }
        }
    }

    fun setTitle(title: String) {
        uiState = uiState.copy(title = title)
    }

    fun setDescription(description: String) {
        uiState = uiState.copy(description = description)
    }

    fun insertDiary(
        diary: Diary,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch(dispatcher) {
            MongoDB.addNewDiary(diary)
                .fold(
                    onSuccess = {
                        withContext(Dispatchers.Main){ onSuccess() }
                    },
                    onError = {

                    },
                    onLoading = {}
                )
        }
    }
}