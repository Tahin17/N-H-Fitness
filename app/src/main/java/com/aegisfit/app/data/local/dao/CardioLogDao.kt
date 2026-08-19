package com.aegisfit.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.aegisfit.app.data.local.entity.CardioLogEntity
import kotlinx.coroutines.flow.Flow
import kotlin.jvm.JvmSuppressWildcards

@Dao
@JvmSuppressWildcards
interface CardioLogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: CardioLogEntity): Long

    @Update
    suspend fun update(log: CardioLogEntity): Int

    @Query("SELECT * FROM cardio_logs WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): CardioLogEntity?

    @Query("SELECT id FROM cardio_logs WHERE user_id = :userId AND date = :date AND type = :type AND duration_min = :durationMin LIMIT 1")
    suspend fun findId(userId: String, date: Long, type: String, durationMin: Int): Long?

    @Query("SELECT * FROM cardio_logs WHERE user_id = :userId AND date = :date")
    fun getForDate(userId: String, date: Long): Flow<List<CardioLogEntity>>

    @Query("SELECT * FROM cardio_logs WHERE user_id = :userId ORDER BY date DESC")
    fun getAll(userId: String): Flow<List<CardioLogEntity>>

    @Query("SELECT * FROM cardio_logs WHERE user_id = :userId AND completed = 1 ORDER BY date DESC")
    fun getCompletedLogs(userId: String): Flow<List<CardioLogEntity>>

    @Query("SELECT SUM(calories_burned) FROM cardio_logs WHERE user_id = :userId AND date = :date AND completed = 1")
    fun getTotalCaloriesBurnedForDate(userId: String, date: Long): Flow<Long?>

    @Query("DELETE FROM cardio_logs WHERE id = :id")
    suspend fun delete(id: Long): Int

    @Query("SELECT * FROM cardio_logs WHERE user_id = :userId AND date >= :since")
    suspend fun getLogsSince(userId: String, since: Long): List<CardioLogEntity>

    @Query("SELECT * FROM cardio_logs WHERE user_id = :userId")
    suspend fun getAllLogsSync(userId: String): List<CardioLogEntity>
}
