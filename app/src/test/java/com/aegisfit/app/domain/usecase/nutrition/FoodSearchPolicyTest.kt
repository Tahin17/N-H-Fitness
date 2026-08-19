package com.aegisfit.app.domain.usecase.nutrition

import com.aegisfit.app.domain.model.FoodItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FoodSearchPolicyTest {
    @Test
    fun rank_prioritizesExactAndLocalMatches() {
        val items = listOf(
            food("Chicken Biryani", local = true),
            food("Spicy Chicken Biryani"),
            food("Chicken Soup")
        )

        val ranked = FoodSearchPolicy.rank(items, "chicken biryani")

        assertEquals("Chicken Biryani", ranked.first().name)
        assertEquals(2, ranked.size)
    }

    @Test
    fun cacheTtl_isLongerForSuccessfulSearches() {
        val now = 10L * 24 * 60 * 60 * 1_000
        val twoDaysAgo = now - 2L * 24 * 60 * 60 * 1_000

        assertTrue(FoodSearchPolicy.isFresh(twoDaysAgo, resultCount = 5, nowEpochMs = now))
        assertFalse(FoodSearchPolicy.isFresh(twoDaysAgo, resultCount = 0, nowEpochMs = now))
    }

    private fun food(name: String, local: Boolean = false) = FoodItem(
        name = name,
        caloriesPer100g = 100.0,
        proteinPer100g = 10.0,
        carbsPer100g = 10.0,
        fatPer100g = 2.0,
        isLocalBd = local
    )
}
