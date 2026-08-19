package com.aegisfit.app.data.repository

import com.aegisfit.app.data.local.dao.HydrationDao
import com.aegisfit.app.data.mapper.toDomain
import com.aegisfit.app.data.mapper.toEntity
import com.aegisfit.app.domain.model.HydrationLog
import com.aegisfit.app.domain.repository.HydrationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class HydrationRepositoryImpl @Inject constructor(
    private val hydrationDao: HydrationDao
) : HydrationRepository {

    override fun getHydrationByDate(userId: String, date: Long): Flow<List<HydrationLog>> {
        return hydrationDao.getForDate(userId, date).map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun saveHydrationLog(log: HydrationLog) {
        require(log.userId.isNotBlank() && log.amountMl in 50..2_000) {
            "Water amount must be between 50 and 2,000 ml."
        }
        hydrationDao.insert(log.toEntity())
    }

    override fun getTotalForDate(userId: String, date: Long): Flow<Long?> {
        return hydrationDao.getTotalForDate(userId, date)
    }
}
