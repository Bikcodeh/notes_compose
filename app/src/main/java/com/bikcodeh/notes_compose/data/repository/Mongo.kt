package com.bikcodeh.notes_compose.data.repository

import com.bikcodeh.notes_compose.BuildConfig
import com.bikcodeh.notes_compose.domain.commons.Failure
import com.bikcodeh.notes_compose.domain.commons.Result
import com.bikcodeh.notes_compose.domain.exception.UserNotAuthenticatedException
import com.bikcodeh.notes_compose.domain.model.Diary
import com.bikcodeh.notes_compose.domain.repository.Diaries
import com.bikcodeh.notes_compose.domain.repository.MongoRepository
import com.bikcodeh.notes_compose.presentation.util.toInstant
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.log.LogLevel
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.sync.SyncConfiguration
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.time.ZoneId

object MongoDB : MongoRepository {

    private val app = App.create(BuildConfig.APP_ID)
    private val user = app.currentUser
    private lateinit var realm: Realm

    init {
        configureRealm()
    }

    override fun configureRealm() {
        if (user != null) {
            val config = SyncConfiguration.Builder(user, setOf(Diary::class))
                .initialSubscriptions { sub ->
                    add(
                        query = sub.query<Diary>("ownerId == $0", user.id),
                        name = "diary"
                    )
                }
                .log(LogLevel.ALL)
                .build()
            realm = Realm.open(config)
        }
    }

    override fun getAllDiaries(): Flow<Diaries> {
        return if (user != null) {
            try {
                realm.query<Diary>(query = "ownerId == $0", user.id)
                    .sort(property = "date", sortOrder = Sort.DESCENDING)
                    .asFlow()
                    .map { result ->
                        Result.Success(
                            result.list.groupBy {
                                it.date.toInstant()
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate()
                            })
                    }

            } catch (e: Exception) {
                flow { emit(Result.Error(Failure.analyzeException(e))) }
            }
        } else {
            flow { emit(Result.Error(Failure.analyzeException(UserNotAuthenticatedException()))) }
        }
    }
}