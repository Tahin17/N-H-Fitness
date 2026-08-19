package com.aegisfit.app.data.mapper

import com.aegisfit.app.data.local.entity.CardioLogEntity
import com.aegisfit.app.data.local.entity.CreatineLogEntity
import com.aegisfit.app.data.local.entity.ExerciseEntity
import com.aegisfit.app.data.local.entity.WorkoutDayEntity
import com.aegisfit.app.data.local.entity.WorkoutLogEntity
import com.aegisfit.app.domain.model.CardioLog
import com.aegisfit.app.domain.model.CreatineLog
import com.aegisfit.app.domain.model.Exercise
import com.aegisfit.app.domain.model.WorkoutDay
import com.aegisfit.app.domain.model.WorkoutLog

fun WorkoutDayEntity.toDomain(): WorkoutDay = WorkoutDay(
    id = id,
    dayNumber = dayNumber,
    name = name,
    muscleGroup = muscleGroups
)

fun WorkoutDay.toEntity(): WorkoutDayEntity = WorkoutDayEntity(
    id = id,
    dayNumber = dayNumber,
    name = name,
    muscleGroups = muscleGroup
)

fun ExerciseEntity.toDomain(): Exercise = Exercise(
    id = id,
    name = name,
    targetMuscle = targetMuscle,
    workoutDayId = workoutDayId,
    orderInDay = orderInDay
)

fun Exercise.toEntity(): ExerciseEntity = ExerciseEntity(
    id = id,
    name = name,
    targetMuscle = targetMuscle,
    workoutDayId = workoutDayId,
    orderInDay = orderInDay
)

fun WorkoutLogEntity.toDomain(): WorkoutLog = WorkoutLog(
    id = id,
    userId = userId,
    date = date,
    exerciseId = exerciseId,
    setNumber = setNumber,
    reps = reps,
    weightKg = weightKg,
    completed = completed,
    notes = notes
)

fun WorkoutLog.toEntity(): WorkoutLogEntity = WorkoutLogEntity(
    id = id,
    userId = userId,
    date = date,
    exerciseId = exerciseId,
    setNumber = setNumber,
    reps = reps,
    weightKg = weightKg,
    completed = completed,
    notes = notes
)

fun CardioLogEntity.toDomain(): CardioLog = CardioLog(
    id = id,
    userId = userId,
    date = date,
    type = type,
    durationMin = durationMin,
    caloriesBurned = caloriesBurned.toDouble(),
    completed = completed,
    distanceKm = distanceKm
)

fun CardioLog.toEntity(): CardioLogEntity = CardioLogEntity(
    id = id,
    userId = userId,
    date = date,
    type = type,
    durationMin = durationMin,
    caloriesBurned = caloriesBurned.toInt(),
    completed = completed,
    distanceKm = distanceKm
)

fun CreatineLogEntity.toDomain(): CreatineLog = CreatineLog(
    id = id,
    userId = userId,
    date = date,
    taken = taken,
    waterWithCreatineMl = waterAmountMl
)

fun CreatineLog.toEntity(): CreatineLogEntity = CreatineLogEntity(
    id = id,
    userId = userId,
    date = date,
    taken = taken,
    waterAmountMl = waterWithCreatineMl
)
