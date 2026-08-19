package com.aegisfit.app.domain.repository

import com.aegisfit.app.domain.model.SkinPhoto
import com.aegisfit.app.domain.model.SkincareLog
import com.aegisfit.app.domain.model.SkincareRoutine
import kotlinx.coroutines.flow.Flow

interface SkincareRepository {
    fun getRoutinesByType(type: String): Flow<List<SkincareRoutine>>
    fun getLogsByDate(userId: String, date: Long): Flow<List<SkincareLog>>
    suspend fun saveLog(log: SkincareLog)
    fun getPhotos(userId: String): Flow<List<SkinPhoto>>
    suspend fun savePhoto(photo: SkinPhoto)
    fun getAllLogsForDate(userId: String, date: Long): Flow<List<SkincareLog>>
}
