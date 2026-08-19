package com.aegisfit.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "weight_logs", indices = [Index(value = ["user_id"])])
data class WeightLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "user_id") val userId: String = "",
    val date: Long,
    val weightKg: Double,
    val timestamp: Long
)
