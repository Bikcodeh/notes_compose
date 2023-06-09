package com.bikcodeh.notes_compose.domain.commons

/**
 * Class to wrap responses handling 2 possible states: Success, Error
 */
sealed class Result<out T> {
    class Success<T>(val data: T) : Result<T>()
    class Error<T>(val failure: Failure) : Result<T>()
    object Loading: Result<Nothing>()
}

/**
 * Extension function to handle in an easier way the possible states with lambdas
 */
@Suppress("TooGenericExceptionCaught")
inline fun <R, T> Result<T>.fold(
    onSuccess: (value: T) -> R,
    onError: (failure: Failure) -> R,
    onLoading: () -> R
): R {
    return when (this) {
        is Result.Success -> {
            try {
                onSuccess(data)
            } catch (e: Exception) {
                onError(Failure.analyzeException(e))
            }
        }
        is Result.Error -> onError(failure)
        Result.Loading -> onLoading()
    }
}

/**
 * Extension function to retrieve only the success value
 */
fun <T> Result<T>.getSuccess(): T? = when (this) {
    is Result.Success -> data
    else -> null
}

/**
 * Extension function to retrieve only the error value
 */
fun <T> Result<T>.getFailure(): Failure? = when (this) {
    is Result.Error -> failure
    else -> null
}
