package com.aegisfit.app.domain.usecase.skincare

import com.aegisfit.app.domain.model.SkincareRoutine
import com.aegisfit.app.domain.repository.SkincareRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSkincareRoutineUseCase @Inject constructor(
    private val repo: SkincareRepository
) {
    operator fun invoke(type: String): Flow<List<SkincareRoutine>> {
        return repo.getRoutinesByType(type)
    }
}
