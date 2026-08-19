package com.aegisfit.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aegisfit.app.data.local.entity.ExerciseEntity
import com.aegisfit.app.data.local.entity.WorkoutDayEntity
import kotlinx.coroutines.flow.Flow
import kotlin.jvm.JvmSuppressWildcards

@Dao
@JvmSuppressWildcards
interface WorkoutDao {
    // Workout Days
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDay(day: WorkoutDayEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllDays(days: List<WorkoutDayEntity>): List<Long>

    @Query("SELECT * FROM workout_days ORDER BY day_number ASC")
    fun getAllDays(): Flow<List<WorkoutDayEntity>>

    @Query("SELECT * FROM workout_days WHERE id = :dayId")
    suspend fun getDayById(dayId: Long): WorkoutDayEntity?

    @Query("SELECT COUNT(*) FROM workout_days")
    suspend fun getDayCount(): Int

    // Exercises
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercise(exercise: ExerciseEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllExercises(exercises: List<ExerciseEntity>): List<Long>

    @Query("SELECT * FROM exercises WHERE workout_day_id = :dayId ORDER BY order_in_day ASC")
    fun getExercisesForDay(dayId: Long): Flow<List<ExerciseEntity>>

    @Query("SELECT * FROM exercises ORDER BY workout_day_id, order_in_day")
    fun getAllExercises(): Flow<List<ExerciseEntity>>

    @Query("SELECT * FROM exercises")
    suspend fun getAllExercisesSync(): List<ExerciseEntity>

    @Query("SELECT * FROM exercises WHERE id = :exerciseId")
    suspend fun getExerciseById(exerciseId: Long): ExerciseEntity?
}
