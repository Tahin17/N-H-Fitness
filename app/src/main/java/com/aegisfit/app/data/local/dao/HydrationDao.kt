package com.aegisfit.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aegisfit.app.data.local.entity.HydrationLogEntity
import kotlinx.coroutines.flow.Flow
import kotlin.jvm.JvmSuppressWildcards

@Dao
@JvmSuppressWildcards
interface HydrationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: HydrationLogEntity): Long

    @Query("SELECT * FROM hydration_logs WHERE user_id = :userId AND date = :date ORDER BY timestamp")
    fun getForDate(userId: String, date: Long): Flow<List<HydrationLogEntity>>

    @Query("SELECT SUM(amount_ml) FROM hydration_logs WHERE user_id = :userId AND date = :date")
    fun getTotalForDate(userId: String, date: Long): Flow<Long?>

    @Query("DELETE FROM hydration_logs WHERE user_id = :userId AND id = :id")
    suspend fun delete(userId: String, id: Long): Int

    @Query("SELECT * FROM hydration_logs WHERE user_id = :userId ORDER BY date DESC, timestamp DESC")
    fun getAll(userId: String): Flow<List<HydrationLogEntity>>
}
