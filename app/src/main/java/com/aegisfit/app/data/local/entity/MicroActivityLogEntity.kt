package com.aegisfit.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "micro_activity_logs", indices = [Index(value = ["date"]), Index(value = ["user_id"])])
data class MicroActivityLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "user_id") val userId: String = "",
    @ColumnInfo(name = "date") val date: Long,
    @ColumnInfo(name = "activity_type") val activityType: String, // Pushups, Plank, Squats, Stretching, Hydration
    @ColumnInfo(name = "reps") val reps: Int? = null,
    @ColumnInfo(name = "duration_seconds") val durationSeconds: Int? = null,
    @ColumnInfo(name = "completed_from_notification") val completedFromNotification: Boolean = false,
    @ColumnInfo(name = "timestamp") val timestamp: Long = System.currentTimeMillis()
)
