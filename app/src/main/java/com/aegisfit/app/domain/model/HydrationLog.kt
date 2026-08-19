package com.aegisfit.app.domain.model

data class HydrationLog(
    val id: Long = 0,
    val userId: String = "",
    val date: Long,
    val amountMl: Int,
    val withCreatine: Boolean,
    val timestamp: Long
)
