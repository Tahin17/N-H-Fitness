package com.aegisfit.app.domain.usecase.biometrics

import com.aegisfit.app.domain.model.ActivityLevel
import com.aegisfit.app.domain.model.Gender
import javax.inject.Inject
import kotlin.math.roundToInt

class CalculateTdeeUseCase @Inject constructor(
    private val calculateBmrUseCase: CalculateBmrUseCase
) {
    operator fun invoke(weightKg: Double, heightCm: Double, age: Int, gender: Gender, activityLevel: ActivityLevel): Double {
        val bmr = calculateBmrUseCase(weightKg, heightCm, age, gender)
        if (bmr <= 0.0 || !bmr.isFinite()) return 0.0
        return (bmr * activityLevel.multiplier).roundToInt().toDouble()
    }
}
