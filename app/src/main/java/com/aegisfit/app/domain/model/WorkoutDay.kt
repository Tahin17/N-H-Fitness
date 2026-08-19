package com.aegisfit.app.domain.model

data class WorkoutDay(
    val id: Long = 0,
    val dayNumber: Int,
    val name: String,
    val muscleGroup: String
)
