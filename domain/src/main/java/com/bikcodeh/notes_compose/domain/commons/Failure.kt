package com.bikcodeh.notes_compose.domain.commons

import com.bikcodeh.notes_compose.domain.R
import java.net.ConnectException
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException

sealed class Failure(val error: Exception?, val code: Int = 0) {
    class UnknownException(error: Exception? = null) : Failure(error)
    class NetworkConnection(error: Exception? = null) : Failure(error)
    class ServerError(code: Int) : Failure(null, code)
    class ParsingException(error: Exception? = null) : Failure(error)

    companion object {
        fun analyzeException(exception: Exception?): Failure {
            return when (exception) {
                is UnknownHostException,
                is ConnectException,
                is TimeoutException -> NetworkConnection(exception)
                is NullPointerException -> ParsingException(exception)
                else -> UnknownException(exception)
            }
        }

        fun getMessageResId(failure: Failure): Int {
            return when(failure) {
                is NetworkConnection -> R.string.error_connection
                is ParsingException -> R.string.error_parsing
                is ServerError -> R.string.error_server
                is UnknownException -> R.string.error_unknown
            }
        }
    }
}