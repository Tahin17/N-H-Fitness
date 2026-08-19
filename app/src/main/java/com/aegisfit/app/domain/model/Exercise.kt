package com.aegisfit.app.domain.model

data class Exercise(
    val id: Long = 0,
    val name: String,
    val targetMuscle: String,
    val workoutDayId: Long,
    val orderInDay: Int
)
