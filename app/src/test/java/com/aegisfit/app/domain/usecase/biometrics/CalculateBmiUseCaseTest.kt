package com.aegisfit.app.domain.usecase.biometrics

import org.junit.Assert.assertEquals
import org.junit.Test

class CalculateBmiUseCaseTest {
    private val calculateBmi = CalculateBmiUseCase()

    @Test
    fun calculatesAndRoundsBmi() {
        assertEquals(22.9, calculateBmi(weightKg = 70.0, heightCm = 175.0), 0.0)
    }

    @Test
    fun zeroOrInvalidHeight_doesNotProduceInfinity() {
        assertEquals(0.0, calculateBmi(weightKg = 70.0, heightCm = 0.0), 0.0)
        assertEquals("Unavailable", CalculateBmiUseCase.getCategory(0.0))
    }
}
