package com.bikcodeh.notes_compose.data.repository

import com.bikcodeh.notes_compose.domain.BuildConfig
import com.bikcodeh.notes_compose.domain.repository.AuthRepository
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.Credentials
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor() : AuthRepository {

    override suspend fun signInMongoAtlas(
        tokenId: String,
        onSuccess: (Boolean) -> Unit,
        onError: (Exception) -> Unit
    ) {
        try {
            val result = withContext(Dispatchers.IO) {
                App.create(BuildConfig.APP_ID).login(
                    //Working with custom JWT to retrieve some fields in mongo
                     Credentials.jwt(tokenId)
                    //Normal google
                    //Credentials.google(tokenId, GoogleAuthType.ID_TOKEN)
                ).loggedIn
            }
            withContext(Dispatchers.Main) {
                onSuccess(result)
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                onError(e)
            }
        }
    }

    override suspend fun logOut() {
        App.create(BuildConfig.APP_ID).currentUser?.logOut()
    }
}