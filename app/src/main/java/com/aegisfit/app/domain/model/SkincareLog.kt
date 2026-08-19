package com.aegisfit.app.domain.model

data class SkincareLog(
    val id: Long = 0,
    val userId: String = "",
    val date: Long,
    val routineType: String,
    val stepId: Long,
    val completed: Boolean
)
