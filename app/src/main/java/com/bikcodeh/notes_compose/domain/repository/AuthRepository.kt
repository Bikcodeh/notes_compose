package com.bikcodeh.notes_compose.domain.repository

interface AuthRepository {
    suspend fun signInMongoAtlas(
        tokenId: String,
        onSuccess: (Boolean) -> Unit,
        onError: (Exception) -> Unit
    )
}