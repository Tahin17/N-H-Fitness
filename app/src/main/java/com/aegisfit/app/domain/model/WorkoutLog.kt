package com.aegisfit.app.domain.model

data class WorkoutLog(
    val id: Long = 0,
    val userId: String = "",
    val date: Long,
    val exerciseId: Long,
    val setNumber: Int,
    val reps: Int,
    val weightKg: Double,
    val completed: Boolean,
    val notes: String? = null
)
