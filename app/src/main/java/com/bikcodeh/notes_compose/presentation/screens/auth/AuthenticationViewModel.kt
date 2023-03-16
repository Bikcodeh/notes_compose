package com.bikcodeh.notes_compose.presentation.screens.auth

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bikcodeh.notes_compose.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthenticationViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    var authenticated = mutableStateOf(false)
        private set

    var loadingState = mutableStateOf(false)
        private set

    fun setLoading(loading: Boolean) {
        loadingState.value = loading
    }

    fun signInWithMongoAtlas(
        tokenId: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            authRepository.signInMongoAtlas(
                tokenId = tokenId,
                onSuccess = { isSuccess ->
                    if (isSuccess) {
                        authenticated.value = true
                        onSuccess()
                    } else {
                        onError(Exception("Error while login"))
                    }
                },
                onError = {
                    onError(it)
                },
            )
        }
    }

    fun logOut() {
        viewModelScope.launch { authRepository.logOut() }
    }
}