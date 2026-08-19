package com.aegisfit.app.domain.model

data class SkincareRoutine(
    val id: Long = 0,
    val stepOrder: Int,
    val routineType: String,
    val productName: String,
    val productCategory: String = "",
    val activeIngredient: String? = null,
    val instructions: String? = null,
    val dosage: String? = null,
    val warning: String? = null,
    val alternateGroup: String? = null
)
