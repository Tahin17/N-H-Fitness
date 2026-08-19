package com.aegisfit.app.domain.usecase.workout

import com.aegisfit.app.domain.model.Exercise
import com.aegisfit.app.domain.repository.WorkoutRepository
import javax.inject.Inject

class GenerateRandomWorkoutUseCase @Inject constructor(
    private val workoutRepository: WorkoutRepository
) {
    suspend operator fun invoke(userId: String): List<Exercise> {
        val allExercises = workoutRepository.getAllExercises()
        // Last 14 days
        val recentlyLoggedIds = workoutRepository.getRecentlyLoggedExerciseIds(userId, System.currentTimeMillis() - 14 * 24 * 60 * 60 * 1000L)
        
        val filtered = allExercises.filter { it.id !in recentlyLoggedIds }
        
        return if (filtered.size >= 5) {
            filtered.shuffled().take(5)
        } else {
            val remaining = (allExercises - filtered.toSet()).shuffled()
            (filtered + remaining).take(minOf(allExercises.size, 5))
        }
    }
}
