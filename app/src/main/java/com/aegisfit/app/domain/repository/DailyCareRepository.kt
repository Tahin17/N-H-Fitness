package com.aegisfit.app.domain.repository

import com.aegisfit.app.domain.model.CareCategory
import com.aegisfit.app.domain.model.CareItem
import com.aegisfit.app.domain.model.CareTimeSlot
import com.aegisfit.app.domain.model.DailyCareSummary
import kotlinx.coroutines.flow.Flow

interface DailyCareRepository {
    fun getCareItems(userId: String): Flow<List<CareItem>>
    fun getCareItemsWithStatus(userId: String, date: Long): Flow<List<CareItem>>
    fun getDailyCareSummary(userId: String, date: Long): Flow<DailyCareSummary>
    suspend fun addCareItem(
        userId: String,
        title: String,
        timeSlot: CareTimeSlot,
        category: CareCategory,
        notes: String? = null,
        timeHint: String? = null
    ): Long
    suspend fun deleteCareItem(id: Long): Boolean
    suspend fun toggleCareItemCompletion(
        userId: String,
        date: Long,
        careItemId: Long,
        isCompleted: Boolean
    )
}
