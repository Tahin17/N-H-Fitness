package com.aegisfit.app.domain.usecase.biometrics

import com.aegisfit.app.domain.model.ActivityLevel
import com.aegisfit.app.domain.model.Gender
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.roundToInt

class CalculateCalorieTargetUseCase @Inject constructor(
    private val calculateTdeeUseCase: CalculateTdeeUseCase
) {
    operator fun invoke(
        currentWeightKg: Double,
        goalWeightKg: Double,
        heightCm: Double,
        age: Int,
        gender: Gender,
        activityLevel: ActivityLevel
    ): Int {
        val tdee = calculateTdeeUseCase(currentWeightKg, heightCm, age, gender, activityLevel)
        if (tdee <= 0.0 || !goalWeightKg.isFinite()) return 2_000
        
        val weightDiff = goalWeightKg - currentWeightKg
        
        val baseTarget = when {
            abs(weightDiff) < 0.5 -> tdee // Maintenance
            weightDiff < 0 -> tdee - 500.0 // Deficit for weight loss
            else -> tdee + 300.0 // Surplus for weight gain
        }
        
        return baseTarget.roundToInt().coerceIn(1_200, 6_000)
    }
}
