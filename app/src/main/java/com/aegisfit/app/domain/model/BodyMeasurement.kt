package com.aegisfit.app.domain.model

data class BodyMeasurement(
    val id: Long = 0,
    val userId: String = "",
    val date: Long = System.currentTimeMillis(),
    val chestCm: Double? = null,
    val waistCm: Double? = null,
    val hipsCm: Double? = null,
    val neckCm: Double? = null,
    val leftBicepCm: Double? = null,
    val rightBicepCm: Double? = null,
    val leftForearmCm: Double? = null,
    val rightForearmCm: Double? = null,
    val leftQuadCm: Double? = null,
    val rightQuadCm: Double? = null,
    val leftCalfCm: Double? = null,
    val rightCalfCm: Double? = null
)
