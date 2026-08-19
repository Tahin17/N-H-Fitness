package com.aegisfit.app.domain.model

data class CardioLog(
    val id: Long = 0,
    val userId: String = "",
    val date: Long,
    val type: String,
    val durationMin: Int,
    val caloriesBurned: Double,
    val completed: Boolean,
    val distanceKm: Double? = null
)
