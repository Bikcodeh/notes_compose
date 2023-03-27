package com.bikcodeh.notes_compose.domain.repository

import com.bikcodeh.notes_compose.domain.commons.Result
import com.bikcodeh.notes_compose.domain.model.Diary
import kotlinx.coroutines.flow.Flow
import org.mongodb.kbson.ObjectId
import java.time.LocalDate
import java.time.ZonedDateTime

typealias Diaries = Result<Map<LocalDate, List<Diary>>>
interface MongoRepository {
    fun configureRealm()
    fun getAllDiaries(): Flow<Diaries>
    fun getFilteredDiaries(zonedDateTime: ZonedDateTime): Flow<Diaries>
    suspend fun getSelectedDiary(diaryId: ObjectId): Result<Diary>
    suspend fun addNewDiary(diary: Diary): Result<Diary>
    suspend fun updateDiary(diary: Diary): Result<Diary>
    suspend fun deleteDiary(id: ObjectId): Result<Boolean>
    suspend fun deleteAllDiaries(): Result<Boolean>

}