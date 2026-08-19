package com.aegisfit.app.domain.usecase.biometrics

import com.aegisfit.app.domain.model.Gender
import javax.inject.Inject
import kotlin.math.roundToInt

class CalculateBmrUseCase @Inject constructor() {
    operator fun invoke(weightKg: Double, heightCm: Double, age: Int, gender: Gender): Double {
        if (!weightKg.isFinite() || !heightCm.isFinite() || weightKg <= 0.0 ||
            heightCm <= 0.0 || age <= 0
        ) return 0.0
        val maleBmr = (10.0 * weightKg) + (6.25 * heightCm) - (5.0 * age) + 5.0
        val femaleBmr = (10.0 * weightKg) + (6.25 * heightCm) - (5.0 * age) - 161.0

        val bmr = when (gender) {
            Gender.Male -> maleBmr
            Gender.Female -> femaleBmr
            Gender.Other -> (maleBmr + femaleBmr) / 2.0
        }
        return bmr.roundToInt().toDouble()
    }
}
