package com.bikcodeh.notes_compose.presentation.screens.home

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bikcodeh.notes_compose.data.local.database.dao.ImageToDeleteDao
import com.bikcodeh.notes_compose.data.local.database.entity.ImageToDelete
import com.bikcodeh.notes_compose.data.repository.MongoDB
import com.bikcodeh.notes_compose.di.IoDispatcher
import com.bikcodeh.notes_compose.domain.commons.Failure
import com.bikcodeh.notes_compose.domain.commons.Result
import com.bikcodeh.notes_compose.domain.commons.fold
import com.bikcodeh.notes_compose.domain.repository.Diaries
import com.bikcodeh.notes_compose.presentation.util.connectivity.ConnectivityObserver
import com.bikcodeh.notes_compose.presentation.util.handleError
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val connectivity: ConnectivityObserver,
    private val deleteDao: ImageToDeleteDao,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : ViewModel() {

    var diaries: MutableState<Diaries?> = mutableStateOf(null)
    private var network by mutableStateOf(ConnectivityObserver.Status.Unavailable)

    init {
        observeAllDiaries()
        viewModelScope.launch {
            connectivity.observe().collect { network = it }
        }
    }

    private fun observeAllDiaries() {
        diaries.value = Result.Loading
        viewModelScope.launch(Dispatchers.IO) {
            MongoDB.getAllDiaries().collect { result ->
                diaries.value = result
            }
        }
    }

    fun deleteAllDiaries(
        onSuccess: () -> Unit,
        onError: (resId: Int) -> Unit
    ) {
        if (network == ConnectivityObserver.Status.Available) {
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            val imagesDirectory = "images/${userId}"
            val storage = FirebaseStorage.getInstance().reference
            storage.child(imagesDirectory)
                .listAll()
                .addOnSuccessListener {
                    it.items.forEach { ref ->
                        val imagePath = "images/${userId}/${ref.name}"
                        storage.child(imagePath).delete()
                            .addOnFailureListener {
                                viewModelScope.launch(dispatcher) {
                                    deleteDao.addImageToDelete(
                                        ImageToDelete(
                                            remoteImagePath = imagePath
                                        )
                                    )
                                }
                            }
                    }
                    viewModelScope.launch(dispatcher) {
                        MongoDB.deleteAllDiaries()
                            .fold(
                                onSuccess = { execute(onSuccess) },
                                onError = { failure -> execute { onError(handleError(failure)) } },
                                onLoading = {}
                            )
                    }
                }
                .addOnFailureListener { exception ->
                    onError(
                        handleError(
                            Failure.analyzeException(
                                exception
                            )
                        )
                    )
                }
        } else {
            onError(handleError(Failure.NetworkConnection()))
        }
    }

    private suspend fun execute(action: () -> Unit) {
        withContext(Dispatchers.Main) {
            action()
        }
    }
}