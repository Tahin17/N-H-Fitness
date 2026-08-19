package com.aegisfit.app.domain.model

enum class AsymmetrySeverity { Normal, Warning, Alert }

data class AsymmetryResult(
    val bodyPart: String,
    val leftCm: Double,
    val rightCm: Double,
    val differenceCm: Double,
    val differencePercent: Double,
    val severity: AsymmetrySeverity
)
