package com.aegisfit.app.domain.repository

import com.aegisfit.app.domain.model.UserProfile
import com.aegisfit.app.domain.model.WeightLog
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUserProfile(userId: String): Flow<UserProfile?>
    suspend fun getUserProfileOnce(userId: String): UserProfile?
    suspend fun saveUserProfile(profile: UserProfile)
    suspend fun deleteUserProfile(userId: String)
    
    // Daily weight tracking
    fun getWeightLogForDate(userId: String, date: Long): Flow<WeightLog?>
    fun getRecentWeightLogs(userId: String, limit: Int = 30): Flow<List<WeightLog>>
    suspend fun saveWeightLog(log: WeightLog): Long
    suspend fun addXp(userId: String, amount: Long)
}
