package com.aegisfit.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aegisfit.app.data.local.entity.MicroActivityLogEntity
import kotlinx.coroutines.flow.Flow
import kotlin.jvm.JvmSuppressWildcards

@Dao
@JvmSuppressWildcards
interface MicroActivityDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: MicroActivityLogEntity): Long

    @Query("SELECT * FROM micro_activity_logs WHERE user_id = :userId AND date = :date ORDER BY timestamp")
    fun getForDate(userId: String, date: Long): Flow<List<MicroActivityLogEntity>>

    @Query("SELECT * FROM micro_activity_logs WHERE user_id = :userId ORDER BY date DESC, timestamp DESC")
    fun getAll(userId: String): Flow<List<MicroActivityLogEntity>>

    @Query("SELECT SUM(reps) FROM micro_activity_logs WHERE user_id = :userId AND date = :date AND activity_type = :activityType")
    fun getTotalRepsForDate(userId: String, date: Long, activityType: String): Flow<Long?>

    @Query("SELECT COUNT(*) FROM micro_activity_logs WHERE user_id = :userId AND date = :date")
    fun getCountForDate(userId: String, date: Long): Flow<Int>
}
