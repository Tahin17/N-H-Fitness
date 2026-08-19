package com.aegisfit.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout_days")
data class WorkoutDayEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "day_number") val dayNumber: Int,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "muscle_groups") val muscleGroups: String // comma-separated
)
