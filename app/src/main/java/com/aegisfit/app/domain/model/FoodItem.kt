package com.aegisfit.app.domain.model

data class FoodItem(
    val id: Long = 0,
    val name: String,
    val brand: String? = null,
    val caloriesPer100g: Double,
    val proteinPer100g: Double,
    val carbsPer100g: Double,
    val fatPer100g: Double,
    val fiberPer100g: Double = 0.0,
    val defaultServingSizeG: Double = 100.0,
    val servingDescription: String? = null,
    val barcode: String? = null,
    val isLocalBd: Boolean = false,
    val category: String? = null,
    val imageUrl: String? = null,
    val externalId: String? = null,
    val source: String = "local",
    val lastUpdatedEpochMs: Long = 0L
)
