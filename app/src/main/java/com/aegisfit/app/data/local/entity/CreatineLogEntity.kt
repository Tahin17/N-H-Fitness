package com.aegisfit.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "creatine_logs", indices = [Index(value = ["date", "user_id"], unique = true)])
data class CreatineLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "user_id") val userId: String = "",
    @ColumnInfo(name = "date") val date: Long,
    @ColumnInfo(name = "taken") val taken: Boolean = false,
    @ColumnInfo(name = "water_amount_ml") val waterAmountMl: Int = 0
)
