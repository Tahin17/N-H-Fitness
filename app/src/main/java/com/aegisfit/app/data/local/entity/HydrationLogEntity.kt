package com.aegisfit.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "hydration_logs", indices = [Index(value = ["date"]), Index(value = ["user_id"])])
data class HydrationLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "user_id") val userId: String = "",
    @ColumnInfo(name = "date") val date: Long,
    @ColumnInfo(name = "amount_ml") val amountMl: Int,
    @ColumnInfo(name = "timestamp") val timestamp: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "with_creatine") val withCreatine: Boolean = false
)
