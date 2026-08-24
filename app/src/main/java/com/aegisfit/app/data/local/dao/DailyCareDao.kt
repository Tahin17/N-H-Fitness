package com.aegisfit.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.aegisfit.app.data.local.entity.DailyCareItemEntity
import com.aegisfit.app.data.local.entity.DailyCareLogEntity
import kotlinx.coroutines.flow.Flow
import kotlin.jvm.JvmSuppressWildcards

@Dao
@JvmSuppressWildcards
interface DailyCareDao {

    // Care Items CRUD
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCareItem(item: DailyCareItemEntity): Long

    @Update
    suspend fun updateCareItem(item: DailyCareItemEntity)

    @Query("DELETE FROM daily_care_items WHERE id = :id")
    suspend fun deleteCareItemById(id: Long): Int

    @Query("SELECT * FROM daily_care_items WHERE user_id = :userId ORDER BY created_at_ms ASC")
    fun getAllCareItems(userId: String): Flow<List<DailyCareItemEntity>>

    @Query("SELECT * FROM daily_care_items WHERE user_id = :userId AND time_slot = :timeSlot ORDER BY created_at_ms ASC")
    fun getCareItemsByTimeSlot(userId: String, timeSlot: String): Flow<List<DailyCareItemEntity>>

    @Query("SELECT COUNT(*) FROM daily_care_items WHERE user_id = :userId")
    suspend fun getCareItemCount(userId: String): Int

    // Daily Logs
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateLog(log: DailyCareLogEntity): Long

    @Query("SELECT * FROM daily_care_logs WHERE user_id = :userId AND date = :date")
    fun getLogsForDate(userId: String, date: Long): Flow<List<DailyCareLogEntity>>

    @Query("DELETE FROM daily_care_logs WHERE user_id = :userId AND date = :date AND care_item_id = :careItemId")
    suspend fun deleteLogForDate(userId: String, date: Long, careItemId: Long): Int
}
