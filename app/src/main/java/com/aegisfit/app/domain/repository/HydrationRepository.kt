package com.aegisfit.app.domain.repository

import com.aegisfit.app.domain.model.HydrationLog
import kotlinx.coroutines.flow.Flow

interface HydrationRepository {
    fun getHydrationByDate(userId: String, date: Long): Flow<List<HydrationLog>>
    suspend fun saveHydrationLog(log: HydrationLog)
    fun getTotalForDate(userId: String, date: Long): Flow<Long?>
}
