package com.aegisfit.app.data.local.dao

import androidx.room.*
import com.aegisfit.app.data.local.entity.WeightLogEntity
import kotlinx.coroutines.flow.Flow
import kotlin.jvm.JvmSuppressWildcards

@Dao
@JvmSuppressWildcards
interface WeightLogDao {
    @Query("SELECT * FROM weight_logs WHERE user_id = :userId AND date = :date LIMIT 1")
    fun getForDate(userId: String, date: Long): Flow<WeightLogEntity?>

    @Query("SELECT * FROM weight_logs WHERE user_id = :userId ORDER BY date DESC LIMIT :limit")
    fun getRecent(userId: String, limit: Int): Flow<List<WeightLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: WeightLogEntity): Long

    @Query("SELECT * FROM weight_logs WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): WeightLogEntity?

    @Query("SELECT id FROM weight_logs WHERE user_id = :userId AND date = :date LIMIT 1")
    suspend fun findId(userId: String, date: Long): Long?

    @Delete
    suspend fun delete(log: WeightLogEntity): Int

    @Query("SELECT * FROM weight_logs WHERE user_id = :userId AND date >= :since")
    suspend fun getLogsSince(userId: String, since: Long): List<WeightLogEntity>

    @Query("SELECT * FROM weight_logs WHERE user_id = :userId")
    suspend fun getAllLogsSync(userId: String): List<WeightLogEntity>
}
