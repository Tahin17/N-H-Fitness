package com.aegisfit.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "skincare_logs",
    foreignKeys = [ForeignKey(
        entity = SkincareRoutineEntity::class,
        parentColumns = ["id"],
        childColumns = ["routine_step_id"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["routine_step_id"]), Index(value = ["date"]), Index(value = ["user_id"])]
)
data class SkincareLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "user_id") val userId: String = "",
    @ColumnInfo(name = "date") val date: Long,
    @ColumnInfo(name = "routine_type") val routineType: String, // AM, PM
    @ColumnInfo(name = "routine_step_id") val routineStepId: Long,
    @ColumnInfo(name = "completed") val completed: Boolean = false
)
