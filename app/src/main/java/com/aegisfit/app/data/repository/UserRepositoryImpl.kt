package com.aegisfit.app.data.repository

import com.aegisfit.app.data.local.dao.UserProfileDao
import com.aegisfit.app.data.local.dao.WeightLogDao
import com.aegisfit.app.data.local.entity.WeightLogEntity
import com.aegisfit.app.data.mapper.UserMapper.toDomain
import com.aegisfit.app.data.mapper.UserMapper.toEntity
import com.aegisfit.app.domain.model.UserProfile
import com.aegisfit.app.domain.model.WeightLog
import com.aegisfit.app.domain.repository.DataSyncRepository
import com.aegisfit.app.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userProfileDao: UserProfileDao,
    private val weightLogDao: WeightLogDao,
    private val dataSyncRepository: DataSyncRepository
) : UserRepository {

    override fun getUserProfile(userId: String): Flow<UserProfile?> {
        return userProfileDao.getProfile(userId).map { it?.toDomain() }
    }

    override suspend fun getUserProfileOnce(userId: String): UserProfile? {
        return userProfileDao.getProfileOnce(userId)?.toDomain()
    }

    override suspend fun saveUserProfile(profile: UserProfile) {
        require(profile.userId.isNotBlank()) { "A signed-in user is required." }
        require(profile.name.trim().length in 2..80 && profile.age in 13..100) {
            "Name or age is outside the supported range."
        }
        require(
            profile.weightKg.isFinite() && profile.weightKg in 30.0..350.0 &&
                profile.goalWeightKg.isFinite() && profile.goalWeightKg in 30.0..350.0 &&
                profile.heightCm.isFinite() && profile.heightCm in 100.0..250.0 &&
                profile.dailyCalorieTarget in 1_000..6_000
        ) { "Profile measurements are outside the supported range." }
        userProfileDao.insertOrUpdate(profile.toEntity())
        dataSyncRepository.syncDirtyData(profile.userId)
    }

    override suspend fun deleteUserProfile(userId: String) {
        userProfileDao.deleteProfile(userId)
    }

    override fun getWeightLogForDate(userId: String, date: Long): Flow<WeightLog?> {
        return weightLogDao.getForDate(userId, date).map { entity ->
            entity?.let { WeightLog(it.id, it.userId, it.date, it.weightKg, it.timestamp) }
        }
    }

    override fun getRecentWeightLogs(userId: String, limit: Int): Flow<List<WeightLog>> {
        return weightLogDao.getRecent(userId, limit).map { list ->
            list.map { WeightLog(it.id, it.userId, it.date, it.weightKg, it.timestamp) }
        }
    }

    override suspend fun saveWeightLog(log: WeightLog): Long {
        require(log.userId.isNotBlank() && log.weightKg.isFinite() && log.weightKg in 30.0..350.0) {
            "Weight must be between 30 and 350 kg."
        }
        val entity = WeightLogEntity(
            id = log.id,
            userId = log.userId,
            date = log.date,
            weightKg = log.weightKg,
            timestamp = log.timestamp
        )
        val id = weightLogDao.insert(entity)
        
        // Sync to cloud
        dataSyncRepository.uploadLog(
            userId = log.userId,
            logType = "weight",
            logData = mapOf(
                "localId" to id,
                "userId" to log.userId,
                "date" to log.date,
                "weightKg" to log.weightKg,
                "timestamp" to log.timestamp
            ),
            date = log.date,
            id = id.toString()
        )
        
        return id
    }

    override suspend fun addXp(userId: String, amount: Long) {
        if (userId.isBlank() || amount <= 0) return
        userProfileDao.getProfileOnce(userId)?.let { entity ->
            val updatedProfile = entity.copy(
                xp = entity.xp + amount,
                updatedAt = System.currentTimeMillis()
            )
            userProfileDao.insertOrUpdate(updatedProfile)
        }
    }
}
