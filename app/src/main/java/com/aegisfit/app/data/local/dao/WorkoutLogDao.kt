package com.aegisfit.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.aegisfit.app.data.local.entity.WorkoutLogEntity
import kotlinx.coroutines.flow.Flow
import kotlin.jvm.JvmSuppressWildcards

@Dao
@JvmSuppressWildcards
interface WorkoutLogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: WorkoutLogEntity): Long

    @Update
    suspend fun update(log: WorkoutLogEntity): Int

    @Query("SELECT * FROM workout_logs WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): WorkoutLogEntity?

    @Query("SELECT id FROM workout_logs WHERE user_id = :userId AND exercise_id = :exerciseId AND date = :date AND set_number = :setNumber LIMIT 1")
    suspend fun findId(userId: String, exerciseId: Long, date: Long, setNumber: Int): Long?

    @Query("SELECT * FROM workout_logs WHERE user_id = :userId AND date = :date ORDER BY exercise_id, set_number")
    fun getLogsForDate(userId: String, date: Long): Flow<List<WorkoutLogEntity>>

    @Query("SELECT * FROM workout_logs WHERE user_id = :userId AND exercise_id = :exerciseId AND date = :date ORDER BY set_number")
    fun getLogsForExerciseOnDate(userId: String, exerciseId: Long, date: Long): Flow<List<WorkoutLogEntity>>

    @Query("SELECT * FROM workout_logs WHERE user_id = :userId AND exercise_id = :exerciseId ORDER BY date DESC, set_number")
    fun getLogsForExercise(userId: String, exerciseId: Long): Flow<List<WorkoutLogEntity>>

    @Query("UPDATE workout_logs SET completed = :completed WHERE user_id = :userId AND id = :id")
    suspend fun updateCompletion(userId: String, id: Long, completed: Boolean): Int

    @Query("DELETE FROM workout_logs WHERE user_id = :userId AND id = :id")
    suspend fun deleteLog(userId: String, id: Long): Int

    @Query("SELECT DISTINCT date FROM workout_logs WHERE user_id = :userId ORDER BY date DESC")
    fun getWorkoutDates(userId: String): Flow<List<Long>>

    @Query("SELECT COUNT(*) FROM workout_logs WHERE user_id = :userId AND date = :date AND completed = 1")
    fun getCompletedSetsCountForDate(userId: String, date: Long): Flow<Int>

    @Query("SELECT DISTINCT date FROM workout_logs WHERE user_id = :userId AND date >= :startDate AND date <= :endDate AND completed = 1 ORDER BY date")
    fun getWorkoutDatesInRange(userId: String, startDate: Long, endDate: Long): Flow<List<Long>>

    @Query("SELECT * FROM workout_logs WHERE user_id = :userId AND exercise_id = :exerciseId AND date = (SELECT MAX(date) FROM workout_logs WHERE user_id = :userId AND exercise_id = :exerciseId AND date < :date) ORDER BY set_number")
    suspend fun getLastSessionLogs(userId: String, exerciseId: Long, date: Long): List<WorkoutLogEntity>

    @Query("SELECT DISTINCT exercise_id FROM workout_logs WHERE user_id = :userId AND date >= :since ORDER BY date DESC")
    suspend fun getRecentlyLoggedExerciseIds(userId: String, since: Long): List<Long>

    @Query("SELECT * FROM workout_logs WHERE user_id = :userId AND date >= :since")
    suspend fun getLogsSince(userId: String, since: Long): List<WorkoutLogEntity>

    @Query("SELECT * FROM workout_logs WHERE user_id = :userId")
    suspend fun getAllLogsSync(userId: String): List<WorkoutLogEntity>
}
