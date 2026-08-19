package com.aegisfit.app.domain.model

data class FoodLog(
    val id: Long = 0,
    val userId: String = "",
    val date: Long,
    val foodItem: FoodItem,
    val servings: Double,
    val mealType: String
)
