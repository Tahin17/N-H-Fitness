package com.aegisfit.app.domain.model

data class NutritionSummary(
    val date: Long,
    val totalCalories: Int,
    val targetCalories: Int,
    val totalProtein: Double,
    val totalCarbs: Double,
    val totalFat: Double
)
