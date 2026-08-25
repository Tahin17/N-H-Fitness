package com.aegisfit.app.data.repository

import com.aegisfit.app.data.local.dao.DailyCareDao
import com.aegisfit.app.data.local.entity.DailyCareItemEntity
import com.aegisfit.app.data.local.entity.DailyCareLogEntity
import com.aegisfit.app.domain.model.CareCategory
import com.aegisfit.app.domain.model.CareItem
import com.aegisfit.app.domain.model.CareTimeSlot
import com.aegisfit.app.domain.model.DailyCareSummary
import com.aegisfit.app.domain.repository.DailyCareRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DailyCareRepositoryImpl @Inject constructor(
    private val dailyCareDao: DailyCareDao
) : DailyCareRepository {

    override fun getCareItems(userId: String): Flow<List<CareItem>> {
        return dailyCareDao.getAllCareItems(userId).map { list ->
            list.map { entity -> entity.toDomainModel() }
        }
    }

    override fun getCareItemsWithStatus(userId: String, date: Long): Flow<List<CareItem>> {
        return combine(
            dailyCareDao.getAllCareItems(userId),
            dailyCareDao.getLogsForDate(userId, date)
        ) { items, logs ->
            val completedMap = logs.associate { it.careItemId to it.completed }
            items.map { entity ->
                entity.toDomainModel(isCompleted = completedMap[entity.id] == true)
            }
        }
    }

    override fun getDailyCareSummary(userId: String, date: Long): Flow<DailyCareSummary> {
        return getCareItemsWithStatus(userId, date).map { items ->
            val total = items.size
            val completed = items.count { it.isCompleted }
            DailyCareSummary(completedCount = completed, totalCount = total)
        }
    }

    override suspend fun addCareItem(
        userId: String,
        title: String,
        timeSlot: CareTimeSlot,
        category: CareCategory,
        notes: String?,
        timeHint: String?
    ): Long {
        if (title.isBlank()) return -1L
        val entity = DailyCareItemEntity(
            userId = userId,
            title = title.trim(),
            timeSlot = timeSlot.name,
            category = category.name,
            notes = notes?.trim()?.ifBlank { null },
            timeHint = timeHint?.trim()?.ifBlank { null },
            createdAtMs = System.currentTimeMillis()
        )
        return dailyCareDao.insertCareItem(entity)
    }

    override suspend fun deleteCareItem(id: Long): Boolean {
        return dailyCareDao.deleteCareItemById(id) > 0
    }

    override suspend fun toggleCareItemCompletion(
        userId: String,
        date: Long,
        careItemId: Long,
        isCompleted: Boolean
    ) {
        val log = DailyCareLogEntity(
            userId = userId,
            date = date,
            careItemId = careItemId,
            completed = isCompleted,
            completedAtMs = if (isCompleted) System.currentTimeMillis() else null
        )
        dailyCareDao.insertOrUpdateLog(log)
    }

    private fun DailyCareItemEntity.toDomainModel(isCompleted: Boolean = false): CareItem {
        return CareItem(
            id = id,
            userId = userId,
            title = title,
            timeSlot = CareTimeSlot.fromString(timeSlot),
            category = CareCategory.fromString(category),
            notes = notes,
            timeHint = timeHint,
            isCompleted = isCompleted,
            createdAtMs = createdAtMs
        )
    }
}
