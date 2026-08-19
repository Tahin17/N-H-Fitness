package com.aegisfit.app.data.mapper

import com.aegisfit.app.data.local.entity.UserProfileEntity
import com.aegisfit.app.domain.model.ActivityLevel
import com.aegisfit.app.domain.model.Gender
import com.aegisfit.app.domain.model.UnitSystem
import com.aegisfit.app.domain.model.UserProfile

object UserMapper {
    fun UserProfileEntity.toDomain(): UserProfile {
        return UserProfile(
            userId = this.userId,
            name = this.name,
            age = this.age,
            gender = Gender.fromString(this.gender),
            weightKg = this.weightKg,
            goalWeightKg = this.goalWeightKg,
            heightCm = this.heightCm,
            bodyFatPercent = this.bodyFatPercent,
            activityLevel = ActivityLevel.fromString(this.activityLevel),
            dailyCalorieTarget = this.dailyCalorieTarget,
            unitSystem = UnitSystem.fromString(this.unitSystem),
            useStealthMode = this.useStealthMode,
            xp = this.xp,
            createdAt = this.createdAt,
            updatedAt = this.updatedAt
        )
    }

    fun UserProfile.toEntity(): UserProfileEntity {
        return UserProfileEntity(
            userId = this.userId,
            name = this.name,
            age = this.age,
            gender = this.gender.name,
            weightKg = this.weightKg,
            goalWeightKg = this.goalWeightKg,
            heightCm = this.heightCm,
            bodyFatPercent = this.bodyFatPercent,
            activityLevel = this.activityLevel.name,
            dailyCalorieTarget = this.dailyCalorieTarget,
            unitSystem = this.unitSystem.name,
            useStealthMode = this.useStealthMode,
            xp = this.xp,
            createdAt = this.createdAt,
            updatedAt = this.updatedAt
        )
    }
}
