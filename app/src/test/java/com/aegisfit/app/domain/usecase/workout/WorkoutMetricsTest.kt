package com.aegisfit.app.domain.usecase.workout

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WorkoutMetricsTest {
    @Test
    fun `strength calories are zero until a set is completed`() {
        assertEquals(0.0, WorkoutMetrics.estimateStrengthCalories(0, 85.0), 0.001)
    }

    @Test
    fun `three sets at 85 kg estimate 38 calories`() {
        assertEquals(38.25, WorkoutMetrics.estimateStrengthCalories(3, 85.0), 0.001)
    }

    @Test
    fun `recovery has no score before relevant data is logged`() {
        val result = WorkoutMetrics.recoveryEstimate(0, 3500, 0.0, 1738, 0.0, 0, 0.0)
        assertFalse(result.hasEnoughData)
        assertEquals(0, result.score)
    }

    @Test
    fun `recovery uses hydration nutrition and training load`() {
        val result = WorkoutMetrics.recoveryEstimate(750, 3500, 1341.0, 1738, 62.0, 0, 0.0)
        assertTrue(result.hasEnoughData)
        assertEquals(60, result.score)
    }
}
