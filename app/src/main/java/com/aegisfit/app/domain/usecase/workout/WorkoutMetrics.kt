package com.aegisfit.app.domain.usecase.workout

import kotlin.math.roundToInt

/**
 * Transparent estimates used by the workout and dashboard screens.
 * These are planning aids, not measurements from a heart-rate sensor.
 */
object WorkoutMetrics {
    private const val STRENGTH_MET = 6.0
    private const val ESTIMATED_MINUTES_PER_SET = 1.5

    fun estimateStrengthCalories(completedSets: Int, bodyWeightKg: Double): Double {
        if (completedSets <= 0 || !bodyWeightKg.isFinite() || bodyWeightKg <= 0.0) return 0.0
        val durationHours = completedSets * ESTIMATED_MINUTES_PER_SET / 60.0
        return (STRENGTH_MET * bodyWeightKg * durationHours).coerceIn(0.0, 20_000.0)
    }

    fun recoveryEstimate(
        waterMl: Long,
        waterGoalMl: Int,
        caloriesConsumed: Double,
        calorieTarget: Int,
        proteinG: Double,
        completedSets: Int,
        cardioCalories: Double
    ): RecoveryEstimate {
        val hasLoggedData = waterMl > 0L || caloriesConsumed > 0.0 || proteinG > 0.0 ||
            completedSets > 0 || cardioCalories > 0.0
        if (!hasLoggedData) return RecoveryEstimate(score = 0, hasEnoughData = false)

        val hydration = if (waterGoalMl > 0) {
            (waterMl.toDouble() / waterGoalMl).coerceIn(0.0, 1.0) * 100.0
        } else 0.0

        val calorieCoverage = if (calorieTarget > 0) {
            (caloriesConsumed / calorieTarget).coerceIn(0.0, 1.0)
        } else 0.0
        val proteinTargetG = if (calorieTarget > 0) calorieTarget * 0.30 / 4.0 else 0.0
        val proteinCoverage = if (proteinTargetG > 0.0) {
            (proteinG / proteinTargetG).coerceIn(0.0, 1.0)
        } else 0.0
        val nutrition = (calorieCoverage * 0.60 + proteinCoverage * 0.40) * 100.0

        // With no sleep or wearable data, this is an intentionally conservative proxy.
        // More completed work today reduces the remaining recovery/readiness component.
        val trainingPenalty = completedSets.coerceAtLeast(0) * 4.0 +
            cardioCalories.coerceAtLeast(0.0) / 5.0
        val trainingLoadRecovery = (100.0 - trainingPenalty).coerceIn(20.0, 100.0)

        return RecoveryEstimate(
            score = (hydration * 0.35 + nutrition * 0.35 + trainingLoadRecovery * 0.30)
                .roundToInt()
                .coerceIn(0, 100),
            hasEnoughData = true
        )
    }
}

data class RecoveryEstimate(
    val score: Int,
    val hasEnoughData: Boolean
)
