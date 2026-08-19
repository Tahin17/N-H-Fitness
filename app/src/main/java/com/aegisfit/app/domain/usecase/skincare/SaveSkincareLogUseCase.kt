package com.aegisfit.app.domain.usecase.skincare

import com.aegisfit.app.domain.model.SkincareLog
import com.aegisfit.app.domain.repository.AuthRepository
import com.aegisfit.app.domain.repository.SkincareRepository
import com.aegisfit.app.domain.repository.UserRepository
import javax.inject.Inject

class SaveSkincareLogUseCase @Inject constructor(
    private val skincareRepository: SkincareRepository,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(log: SkincareLog) {
        skincareRepository.saveLog(log)

        // Reward 15 XP if completed
        if (log.completed) {
            authRepository.currentUserId?.let { userId ->
                userRepository.getUserProfileOnce(userId)?.let { profile ->
                    userRepository.saveUserProfile(profile.copy(xp = profile.xp + 15))
                }
            }
        }
    }
}
