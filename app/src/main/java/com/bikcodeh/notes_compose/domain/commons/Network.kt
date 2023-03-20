package com.bikcodeh.notes_compose.domain.commons

import com.bikcodeh.notes_compose.BuildConfig
import com.bikcodeh.notes_compose.domain.exception.UserNotAuthenticatedException
import io.realm.kotlin.mongodb.App
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow

@OptIn(ExperimentalCoroutinesApi::class)
fun<T> makeSafeRequest(
    action: () -> Flow<T>
): Flow<Result<T>> {
    App.Companion.create(BuildConfig.APP_ID).currentUser?.let {
        try {
            return action().flatMapLatest { value ->
                flow { emit(Result.Success(value)) }
            }
        }catch (e: Exception) {
            return flow { emit(Result.Error(Failure.analyzeException(e))) }
        }
    }
    return flow { emit(Result.Error(Failure.analyzeException(UserNotAuthenticatedException()))) }
}