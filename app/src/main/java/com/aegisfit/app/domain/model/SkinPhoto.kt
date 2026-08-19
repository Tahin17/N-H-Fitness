package com.aegisfit.app.domain.model

data class SkinPhoto(
    val id: Long = 0,
    val userId: String = "",
    val date: Long,
    val photoPath: String,
    val angleType: String,
    val notes: String?
)
