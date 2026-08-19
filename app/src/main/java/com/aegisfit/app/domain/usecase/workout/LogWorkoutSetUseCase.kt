package com.aegisfit.app.domain.usecase.workout

import com.aegisfit.app.domain.model.WorkoutLog
import com.aegisfit.app.domain.repository.AuthRepository
import com.aegisfit.app.domain.repository.UserRepository
import com.aegisfit.app.domain.repository.WorkoutRepository
import javax.inject.Inject

class LogWorkoutSetUseCase @Inject constructor(
    private val repo: WorkoutRepository
) {
    suspend operator fun invoke(log: WorkoutLog): Long {
        return repo.saveWorkoutLog(log)
    }
}
