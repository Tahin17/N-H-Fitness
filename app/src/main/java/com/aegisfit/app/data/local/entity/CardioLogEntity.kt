package com.aegisfit.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "cardio_logs", indices = [Index(value = ["date"]), Index(value = ["user_id"])])
data class CardioLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "user_id") val userId: String = "",
    @ColumnInfo(name = "date") val date: Long,
    @ColumnInfo(name = "type") val type: String, // Treadmill, Stairmaster, Cycling, OutdoorStairs
    @ColumnInfo(name = "duration_min") val durationMin: Int,
    @ColumnInfo(name = "calories_burned") val caloriesBurned: Int,
    @ColumnInfo(name = "completed") val completed: Boolean = false,
    @ColumnInfo(name = "distance_km") val distanceKm: Double? = null
)
