package com.aegisfit.app.domain.usecase.workout

import javax.inject.Inject

class EstimateCalorieBurnUseCase @Inject constructor() {
    operator fun invoke(type: String, durationMin: Int, bodyWeightKg: Double): Double {
        if (durationMin <= 0 || !bodyWeightKg.isFinite() || bodyWeightKg <= 0.0) return 0.0
        val caloriesPerMin = when(type.lowercase()) {
            "treadmill" -> 6.5
            "stairmaster" -> 9.0
            "cycling" -> 7.5
            "outdoor stairs" -> 10.0
            else -> 7.0
        } * (bodyWeightKg / 85.0)
        return (caloriesPerMin * durationMin).coerceIn(0.0, 20_000.0)
    }
}
