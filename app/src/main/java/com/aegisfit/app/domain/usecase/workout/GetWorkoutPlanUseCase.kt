package com.aegisfit.app.domain.usecase.workout

import com.aegisfit.app.domain.model.Exercise
import com.aegisfit.app.domain.model.WorkoutDay
import com.aegisfit.app.domain.repository.WorkoutRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetWorkoutPlanUseCase @Inject constructor(
    private val repo: WorkoutRepository
) {
    operator fun invoke(): Flow<List<WorkoutDay>> = repo.getWorkoutDays()
    
    fun getExercises(dayId: Long): Flow<List<Exercise>> = repo.getExercisesForDay(dayId)
}
