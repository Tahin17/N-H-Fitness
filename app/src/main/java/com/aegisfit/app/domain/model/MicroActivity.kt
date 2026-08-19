package com.aegisfit.app.domain.model

data class MicroActivity(
    val id: Long = 0,
    val date: Long,
    val activityType: String,
    val reps: Int,
    val completedFromNotification: Boolean
)
