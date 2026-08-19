package com.aegisfit.app.util

import org.junit.Assert.assertEquals
import org.junit.Test

class NutritionMathTest {
    @Test
    fun servingWeight_usesDefaultServingInsteadOfAssuming100Grams() {
        assertEquals(75.0, NutritionMath.servingWeightG(50.0, 1.5), 0.001)
    }

    @Test
    fun nutrientAmount_scalesPer100GramValue() {
        assertEquals(123.75, NutritionMath.nutrientAmount(165.0, 75.0), 0.001)
    }

    @Test
    fun nonFiniteInputs_returnZero() {
        assertEquals(0.0, NutritionMath.servingWeightG(Double.NaN, 1.0), 0.0)
        assertEquals(0.0, NutritionMath.nutrientAmount(20.0, Double.POSITIVE_INFINITY), 0.0)
    }
}
