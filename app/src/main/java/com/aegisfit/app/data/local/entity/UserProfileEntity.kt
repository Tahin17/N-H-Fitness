package com.aegisfit.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val userId: String = "",
    @ColumnInfo(name = "name") val name: String = "",
    @ColumnInfo(name = "age") val age: Int = 25,
    @ColumnInfo(name = "gender") val gender: String = "Male", // Male, Female, Other
    @ColumnInfo(name = "weight_kg") val weightKg: Double = 86.0,
    @ColumnInfo(name = "goal_weight_kg") val goalWeightKg: Double = 76.0,
    @ColumnInfo(name = "height_cm") val heightCm: Double = 175.0,
    @ColumnInfo(name = "body_fat_percent") val bodyFatPercent: Double? = null,
    @ColumnInfo(name = "activity_level") val activityLevel: String = "Moderate", // Sedentary, Light, Moderate, Active, VeryActive
    @ColumnInfo(name = "daily_calorie_target") val dailyCalorieTarget: Int = 1700,
    @ColumnInfo(name = "unit_system") val unitSystem: String = "Metric", // Metric, Imperial
    @ColumnInfo(name = "use_stealth_mode") val useStealthMode: Boolean = false,
    @ColumnInfo(name = "xp") val xp: Long = 0,
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "updated_at") val updatedAt: Long = System.currentTimeMillis()
)
