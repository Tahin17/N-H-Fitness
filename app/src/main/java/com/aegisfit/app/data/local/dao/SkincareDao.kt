package com.aegisfit.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aegisfit.app.data.local.entity.SkincareLogEntity
import com.aegisfit.app.data.local.entity.SkincareRoutineEntity
import kotlinx.coroutines.flow.Flow
import kotlin.jvm.JvmSuppressWildcards

@Dao
@JvmSuppressWildcards
interface SkincareDao {
    // Routine Steps
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutineStep(step: SkincareRoutineEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllRoutineSteps(steps: List<SkincareRoutineEntity>): List<Long>

    @Query("SELECT * FROM skincare_routines WHERE routine_type = :routineType ORDER BY step_order")
    fun getRoutineSteps(routineType: String): Flow<List<SkincareRoutineEntity>>

    @Query("SELECT * FROM skincare_routines ORDER BY routine_type, step_order")
    fun getAllRoutineSteps(): Flow<List<SkincareRoutineEntity>>

    @Query("SELECT COUNT(*) FROM skincare_routines")
    suspend fun getRoutineStepCount(): Int

    // Logs
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: SkincareLogEntity): Long

    @Query("SELECT * FROM skincare_logs WHERE user_id = :userId AND date = :date AND routine_type = :routineType ORDER BY routine_step_id")
    fun getLogsForDate(userId: String, date: Long, routineType: String): Flow<List<SkincareLogEntity>>

    @Query("UPDATE skincare_logs SET completed = :completed WHERE user_id = :userId AND id = :id")
    suspend fun updateLogCompletion(userId: String, id: Long, completed: Boolean): Int

    @Query("SELECT * FROM skincare_logs WHERE user_id = :userId AND date = :date")
    fun getAllLogsForDate(userId: String, date: Long): Flow<List<SkincareLogEntity>>
}
