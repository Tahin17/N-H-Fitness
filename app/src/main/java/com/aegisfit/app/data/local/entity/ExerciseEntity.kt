package com.aegisfit.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "exercises",
    foreignKeys = [ForeignKey(
        entity = WorkoutDayEntity::class,
        parentColumns = ["id"],
        childColumns = ["workout_day_id"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["workout_day_id"])]
)
data class ExerciseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "workout_day_id") val workoutDayId: Long,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "target_muscle") val targetMuscle: String,
    @ColumnInfo(name = "order_in_day") val orderInDay: Int,
    @ColumnInfo(name = "is_unilateral") val isUnilateral: Boolean = false,
    @ColumnInfo(name = "notes") val notes: String? = null
)
