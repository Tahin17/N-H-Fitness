package com.aegisfit.app.domain.model

data class WeightLog(
    val id: Long = 0,
    val userId: String = "",
    val date: Long,
    val weightKg: Double,
    val timestamp: Long = System.currentTimeMillis()
)
