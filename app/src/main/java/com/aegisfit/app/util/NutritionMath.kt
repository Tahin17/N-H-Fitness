package com.aegisfit.app.util

object NutritionMath {
    fun servingWeightG(defaultServingSizeG: Double, servings: Double): Double {
        if (!defaultServingSizeG.isFinite() || !servings.isFinite()) return 0.0
        return defaultServingSizeG.coerceIn(1.0, 2_000.0) * servings.coerceIn(0.0, 100.0)
    }

    fun nutrientAmount(per100g: Double, servingWeightG: Double): Double {
        if (!per100g.isFinite() || !servingWeightG.isFinite()) return 0.0
        return per100g.coerceAtLeast(0.0) * servingWeightG.coerceAtLeast(0.0) / 100.0
    }
}
