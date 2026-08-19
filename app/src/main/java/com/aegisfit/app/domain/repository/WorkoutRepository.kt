package com.aegisfit.app.domain.repository

import com.aegisfit.app.domain.model.CardioLog
import com.aegisfit.app.domain.model.CreatineLog
import com.aegisfit.app.domain.model.Exercise
import com.aegisfit.app.domain.model.WorkoutDay
import com.aegisfit.app.domain.model.WorkoutLog
import kotlinx.coroutines.flow.Flow

interface WorkoutRepository {
    fun getWorkoutDays(): Flow<List<WorkoutDay>>
    fun getExercisesForDay(dayId: Long): Flow<List<Exercise>>
    fun getWorkoutLogsByDateAndExercise(userId: String, date: Long, exerciseId: Long): Flow<List<WorkoutLog>>
    suspend fun saveWorkoutLog(log: WorkoutLog): Long
    
    fun getCardioLogsByDate(userId: String, date: Long): Flow<List<CardioLog>>
    fun getCardioCaloriesForDate(userId: String, date: Long): Flow<Double>
    suspend fun saveCardioLog(log: CardioLog): Long
    
    fun getWeightliftingCaloriesForDate(userId: String, date: Long, bodyWeight: Double): Flow<Double>
    
    fun getCreatineLogByDate(userId: String, date: Long): Flow<CreatineLog?>
    suspend fun saveCreatineLog(log: CreatineLog): Long
    
    fun getCompletedSetsCountForDate(userId: String, date: Long): Flow<Int>
    fun getWorkoutDatesInRange(userId: String, startDate: Long, endDate: Long): Flow<List<Long>>
    suspend fun getExerciseById(id: Long): Exercise?
    
    suspend fun getLastSessionLogs(userId: String, exerciseId: Long, date: Long): List<WorkoutLog>
    suspend fun getAllExercises(): List<Exercise>
    suspend fun getRecentlyLoggedExerciseIds(userId: String, since: Long): List<Long>
}
