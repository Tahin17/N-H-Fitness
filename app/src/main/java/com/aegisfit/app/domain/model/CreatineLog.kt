package com.aegisfit.app.domain.model

data class CreatineLog(
    val id: Long = 0,
    val userId: String = "",
    val date: Long,
    val taken: Boolean,
    val waterWithCreatineMl: Int
)
