package com.bikcodeh.notes_compose.data.repository

import com.bikcodeh.notes_compose.BuildConfig
import com.bikcodeh.notes_compose.domain.commons.Result
import com.bikcodeh.notes_compose.domain.commons.makeSafeRequest
import com.bikcodeh.notes_compose.domain.commons.makeSafeRequestFlow
import com.bikcodeh.notes_compose.domain.exception.DiaryNotExistException
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
import io.realm.kotlin.types.RealmInstant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.mongodb.kbson.ObjectId
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

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
        return makeSafeRequestFlow {
            realm.query<Diary>(query = "ownerId == $0", user?.id)
                .sort(property = "date", sortOrder = Sort.DESCENDING)
                .asFlow()
                .map { result ->
                    result.list.groupBy {
                        it.date.toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                    }
                }
        }
    }

    override fun getFilteredDiaries(zonedDateTime: ZonedDateTime): Flow<Diaries> {
        return makeSafeRequestFlow {
            realm.query<Diary>(
                "ownerId == $0 AND date < $1 AND date > $2",
                user?.id,
                RealmInstant.from(
                    LocalDateTime.of(
                        zonedDateTime.toLocalDate().plusDays(1),
                        LocalTime.MIDNIGHT
                    ).toEpochSecond(zonedDateTime.offset), 0
                ),
                RealmInstant.from(
                    LocalDateTime.of(
                        zonedDateTime.toLocalDate(),
                        LocalTime.MIDNIGHT
                    ).toEpochSecond(zonedDateTime.offset), 0
                )
            ).asFlow()
                .map { result ->
                    result.list.groupBy {
                        it.date.toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                    }
                }
        }
    }

    override suspend fun getSelectedDiary(diaryId: ObjectId): Result<Diary> {
        return makeSafeRequest {
            realm.query<Diary>(query = "_id == $0", diaryId).first().find()
                ?: throw DiaryNotExistException()
        }
    }

    override suspend fun addNewDiary(diary: Diary): Result<Diary> {
        return makeSafeRequest {
            realm.write {
                val addedDiary = copyToRealm(diary.apply { ownerId = user!!.id })
                addedDiary
            }
        }
    }

    override suspend fun updateDiary(diary: Diary): Result<Diary> {
        return makeSafeRequest {
            realm.write {
                val queriedDiary = query<Diary>(query = "_id == $0", diary._id).first().find()
                if (queriedDiary != null) {
                    queriedDiary.title = diary.title
                    queriedDiary.description = diary.description
                    queriedDiary.mood = diary.mood
                    queriedDiary.images = diary.images
                    queriedDiary.date = diary.date
                    queriedDiary
                } else {
                    throw DiaryNotExistException()
                }
            }
        }
    }

    override suspend fun deleteDiary(id: ObjectId): Result<Boolean> {
        return makeSafeRequest {
            realm.write {
                val diary =
                    query<Diary>(query = "_id == $0 AND ownerId == $1", id, user?.id).first().find()
                if (diary != null) {
                    delete(diary)
                    true
                } else {
                    throw DiaryNotExistException()
                }
            }
        }
    }

    override suspend fun deleteAllDiaries(): Result<Boolean> {
        return makeSafeRequest {
            realm.write {
                val diaries = this.query<Diary>("ownerId == $0", user?.id).find()
                delete(diaries)
                true
            }
        }
    }
}