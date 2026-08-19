package com.aegisfit.app.domain.usecase.biometrics

import javax.inject.Inject
import kotlin.math.roundToInt

class CalculateBmiUseCase @Inject constructor() {
    operator fun invoke(weightKg: Double, heightCm: Double): Double {
        if (!weightKg.isFinite() || !heightCm.isFinite() || weightKg <= 0.0 || heightCm <= 0.0) {
            return 0.0
        }
        val heightM = heightCm / 100.0
        val bmi = weightKg / (heightM * heightM)
        return (bmi * 10.0).roundToInt() / 10.0
    }

    companion object {
        fun getCategory(bmi: Double): String {
            return when {
                !bmi.isFinite() || bmi <= 0.0 -> "Unavailable"
                bmi < 18.5 -> "Underweight"
                bmi < 25.0 -> "Normal"
                bmi < 30.0 -> "Overweight"
                else -> "Obese"
            }
        }
    }
}
