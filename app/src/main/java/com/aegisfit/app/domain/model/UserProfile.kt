package com.aegisfit.app.domain.model

data class UserProfile(
    val userId: String = "",
    val name: String = "",
    val age: Int = 25,
    val gender: Gender = Gender.Male,
    val weightKg: Double = 86.0,
    val goalWeightKg: Double = 76.0,
    val heightCm: Double = 175.0,
    val bodyFatPercent: Double? = null,
    val activityLevel: ActivityLevel = ActivityLevel.Moderate,
    val dailyCalorieTarget: Int = 1700,
    val unitSystem: UnitSystem = UnitSystem.Metric,
    val useStealthMode: Boolean = false,
    val xp: Long = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    val rank: String
        get() = when {
            xp <= 500 -> "Starter"
            xp <= 2000 -> "Consistent"
            xp <= 5000 -> "Committed"
            else -> "Peak"
        }
}
