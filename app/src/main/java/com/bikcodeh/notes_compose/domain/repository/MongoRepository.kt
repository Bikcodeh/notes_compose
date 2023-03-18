package com.bikcodeh.notes_compose.domain.repository

import com.bikcodeh.notes_compose.domain.commons.Result
import com.bikcodeh.notes_compose.domain.model.Diary
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

typealias Diaries = Result<Map<LocalDate, List<Diary>>>
interface MongoRepository {
    fun configureRealm()
    fun getAllDiaries(): Flow<Diaries>
}