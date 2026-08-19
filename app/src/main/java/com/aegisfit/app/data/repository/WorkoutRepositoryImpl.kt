package com.aegisfit.app.data.repository

import com.aegisfit.app.data.local.dao.CardioLogDao
import com.aegisfit.app.data.local.dao.CreatineDao
import com.aegisfit.app.data.local.dao.WorkoutDao
import com.aegisfit.app.data.local.dao.WorkoutLogDao
import com.aegisfit.app.data.local.entity.CardioLogEntity
import com.aegisfit.app.data.local.entity.CreatineLogEntity
import com.aegisfit.app.data.local.entity.ExerciseEntity
import com.aegisfit.app.data.local.entity.WorkoutDayEntity
import com.aegisfit.app.data.local.entity.WorkoutLogEntity
import com.aegisfit.app.data.mapper.toDomain
import com.aegisfit.app.data.mapper.toEntity
import com.aegisfit.app.domain.model.CardioLog
import com.aegisfit.app.domain.model.CreatineLog
import com.aegisfit.app.domain.model.Exercise
import com.aegisfit.app.domain.model.WorkoutDay
import com.aegisfit.app.domain.model.WorkoutLog
import com.aegisfit.app.domain.repository.DataSyncRepository
import com.aegisfit.app.domain.repository.UserRepository
import com.aegisfit.app.domain.repository.WorkoutRepository
import com.aegisfit.app.domain.usecase.workout.WorkoutMetrics
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class WorkoutRepositoryImpl @Inject constructor(
    private val workoutDao: WorkoutDao,
    private val workoutLogDao: WorkoutLogDao,
    private val cardioLogDao: CardioLogDao,
    private val creatineDao: CreatineDao,
    private val dataSyncRepository: DataSyncRepository,
    private val userRepository: UserRepository
) : WorkoutRepository {

    override fun getWorkoutDays(): Flow<List<WorkoutDay>> {
        return workoutDao.getAllDays().map { list: List<WorkoutDayEntity> -> 
            list.map { entity -> entity.toDomain() } 
        }
    }

    override fun getExercisesForDay(dayId: Long): Flow<List<Exercise>> {
        return workoutDao.getExercisesForDay(dayId).map { list: List<ExerciseEntity> -> 
            list.map { entity -> entity.toDomain() } 
        }
    }

    override suspend fun getExerciseById(id: Long): Exercise? {
        return workoutDao.getExerciseById(id)?.toDomain()
    }

    override fun getWorkoutLogsByDateAndExercise(userId: String, date: Long, exerciseId: Long): Flow<List<WorkoutLog>> {
        return workoutLogDao.getLogsForExerciseOnDate(userId, exerciseId, date).map { list: List<WorkoutLogEntity> -> 
            list.map { entity -> entity.toDomain() } 
        }
    }

    override suspend fun saveWorkoutLog(log: WorkoutLog): Long {
        require(log.userId.isNotBlank() && log.exerciseId > 0 && log.setNumber in 1..100) {
            "Invalid workout set."
        }
        require(log.reps in 0..1_000 && log.weightKg.isFinite() && log.weightKg in 0.0..1_000.0) {
            "Reps or weight are outside the supported range."
        }
        val previous = log.id.takeIf { it > 0 }?.let { workoutLogDao.getById(it) }
        val entity = log.toEntity()
        val id = workoutLogDao.insert(entity)
        
        // Sync to cloud
        dataSyncRepository.uploadLog(
            userId = log.userId,
            logType = "workout",
            logData = mapOf(
                "localId" to id,
                "userId" to log.userId,
                "exerciseId" to log.exerciseId,
                "date" to log.date,
                "setNumber" to log.setNumber,
                "reps" to log.reps,
                "weightKg" to log.weightKg,
                "completed" to log.completed,
                "notes" to (log.notes ?: "")
            ),
            date = log.date,
            id = id.toString()
        )
        
        // Reward XP if completed
        if (log.completed && previous?.completed != true) {
            userRepository.addXp(log.userId, 10)
        }
        
        return id
    }

    override fun getCardioLogsByDate(userId: String, date: Long): Flow<List<CardioLog>> {
        return cardioLogDao.getForDate(userId, date).map { list: List<CardioLogEntity> -> 
            list.map { entity -> entity.toDomain() } 
        }
    }

    override fun getCardioCaloriesForDate(userId: String, date: Long): Flow<Double> {
        return cardioLogDao.getTotalCaloriesBurnedForDate(userId, date).map { it?.toDouble() ?: 0.0 }
    }

    override suspend fun saveCardioLog(log: CardioLog): Long {
        require(log.userId.isNotBlank() && log.durationMin in 1..600) { "Invalid cardio duration." }
        require(log.caloriesBurned.isFinite() && log.caloriesBurned in 0.0..20_000.0) {
            "Invalid calorie estimate."
        }
        val entity = log.toEntity()
        val id = cardioLogDao.insert(entity)
        
        // Sync to cloud
        dataSyncRepository.uploadLog(
            userId = log.userId,
            logType = "cardio",
            logData = mapOf(
                "localId" to id,
                "userId" to log.userId,
                "date" to log.date,
                "type" to log.type,
                "durationMin" to log.durationMin,
                "distanceKm" to (log.distanceKm ?: 0.0),
                "caloriesBurned" to log.caloriesBurned,
                "completed" to log.completed
            ),
            date = log.date,
            id = id.toString()
        )
        
        return id
    }

    override fun getWeightliftingCaloriesForDate(userId: String, date: Long, bodyWeight: Double): Flow<Double> {
        return workoutLogDao.getCompletedSetsCountForDate(userId, date).map { count ->
            WorkoutMetrics.estimateStrengthCalories(count, bodyWeight)
        }
    }

    override fun getCreatineLogByDate(userId: String, date: Long): Flow<CreatineLog?> {
        return creatineDao.getForDate(userId, date).map { it?.toDomain() }
    }

    override suspend fun saveCreatineLog(log: CreatineLog): Long {
        require(log.userId.isNotBlank() && log.waterWithCreatineMl in 0..5_000) {
            "Invalid creatine log."
        }
        val entity = log.toEntity()
        val id = creatineDao.insertOrUpdate(entity)
        
        // Sync to cloud
        dataSyncRepository.uploadLog(
            userId = log.userId,
            logType = "creatine",
            logData = mapOf(
                "localId" to id,
                "userId" to log.userId,
                "date" to log.date,
                "taken" to log.taken,
                "waterAmountMl" to log.waterWithCreatineMl
            ),
            date = log.date,
            id = id.toString()
        )
        
        return id
    }

    override fun getCompletedSetsCountForDate(userId: String, date: Long): Flow<Int> {
        return workoutLogDao.getCompletedSetsCountForDate(userId, date)
    }

    override fun getWorkoutDatesInRange(userId: String, startDate: Long, endDate: Long): Flow<List<Long>> {
        return workoutLogDao.getWorkoutDatesInRange(userId, startDate, endDate)
    }

    override suspend fun getLastSessionLogs(userId: String, exerciseId: Long, date: Long): List<WorkoutLog> {
        return workoutLogDao.getLastSessionLogs(userId, exerciseId, date).map { entity: WorkoutLogEntity -> 
            entity.toDomain() 
        }
    }

    override suspend fun getAllExercises(): List<Exercise> {
        return workoutDao.getAllExercisesSync().map { entity: ExerciseEntity -> 
            entity.toDomain() 
        }
    }

    override suspend fun getRecentlyLoggedExerciseIds(userId: String, since: Long): List<Long> {
        return workoutLogDao.getRecentlyLoggedExerciseIds(userId, since)
    }
}
