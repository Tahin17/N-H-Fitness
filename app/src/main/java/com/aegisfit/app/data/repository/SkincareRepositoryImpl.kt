package com.aegisfit.app.data.repository

import com.aegisfit.app.data.local.dao.SkinPhotoDao
import com.aegisfit.app.data.local.dao.SkincareDao
import com.aegisfit.app.data.mapper.toDomainModel
import com.aegisfit.app.data.mapper.toEntity
import com.aegisfit.app.domain.model.SkinPhoto
import com.aegisfit.app.domain.model.SkincareLog
import com.aegisfit.app.domain.model.SkincareRoutine
import com.aegisfit.app.domain.repository.SkincareRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SkincareRepositoryImpl @Inject constructor(
    private val skincareDao: SkincareDao,
    private val skinPhotoDao: SkinPhotoDao
) : SkincareRepository {

    override fun getRoutinesByType(type: String): Flow<List<SkincareRoutine>> {
        return skincareDao.getRoutineSteps(type).map { list ->
            list.map { it.toDomainModel() }
        }
    }

    override fun getLogsByDate(userId: String, date: Long): Flow<List<SkincareLog>> {
        return skincareDao.getAllLogsForDate(userId, date).map { list ->
            list.map { it.toDomainModel() }
        }
    }

    override suspend fun saveLog(log: SkincareLog) {
        require(log.userId.isNotBlank() && log.routineType in setOf("AM", "PM") && log.stepId > 0) {
            "Invalid care routine entry."
        }
        skincareDao.insertLog(log.toEntity())
    }

    override fun getPhotos(userId: String): Flow<List<SkinPhoto>> {
        return skinPhotoDao.getAll(userId).map { list ->
            list.map { it.toDomainModel() }
        }
    }

    override suspend fun savePhoto(photo: SkinPhoto) {
        skinPhotoDao.insert(photo.toEntity())
    }

    override fun getAllLogsForDate(userId: String, date: Long): Flow<List<SkincareLog>> {
        return skincareDao.getAllLogsForDate(userId, date).map { list ->
            list.map { it.toDomainModel() }
        }
    }
}
