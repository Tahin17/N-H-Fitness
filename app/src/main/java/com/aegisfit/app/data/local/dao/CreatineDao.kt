package com.aegisfit.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aegisfit.app.data.local.entity.CreatineLogEntity
import kotlinx.coroutines.flow.Flow
import kotlin.jvm.JvmSuppressWildcards

@Dao
@JvmSuppressWildcards
interface CreatineDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(log: CreatineLogEntity): Long

    @Query("SELECT * FROM creatine_logs WHERE user_id = :userId AND date = :date LIMIT 1")
    fun getForDate(userId: String, date: Long): Flow<CreatineLogEntity?>

    @Query("SELECT * FROM creatine_logs WHERE user_id = :userId ORDER BY date DESC")
    fun getAll(userId: String): Flow<List<CreatineLogEntity>>

    @Query("SELECT COUNT(*) FROM creatine_logs WHERE user_id = :userId AND taken = 1")
    fun getTotalDaysTaken(userId: String): Flow<Int>

    @Query("SELECT * FROM creatine_logs WHERE user_id = :userId AND date >= :since")
    suspend fun getLogsSince(userId: String, since: Long): List<CreatineLogEntity>

    @Query("SELECT * FROM creatine_logs WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): CreatineLogEntity?

    @Query("SELECT id FROM creatine_logs WHERE user_id = :userId AND date = :date LIMIT 1")
    suspend fun findId(userId: String, date: Long): Long?
}
