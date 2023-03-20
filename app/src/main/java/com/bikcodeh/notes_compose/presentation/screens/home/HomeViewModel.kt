package com.bikcodeh.notes_compose.presentation.screens.home

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bikcodeh.notes_compose.data.repository.MongoDB
import com.bikcodeh.notes_compose.domain.commons.Result
import com.bikcodeh.notes_compose.domain.repository.Diaries
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(): ViewModel() {

    var diaries: MutableState<Diaries?> = mutableStateOf(null)

    init {
        observeAllDiaries()
    }

    private fun observeAllDiaries() {
        diaries.value = Result.Loading
        viewModelScope.launch(Dispatchers.IO) {
            MongoDB.getAllDiaries().collect { result ->
                diaries.value = result
            }
        }
    }
}