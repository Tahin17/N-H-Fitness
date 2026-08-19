package com.aegisfit.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "workout_logs",
    foreignKeys = [ForeignKey(
        entity = ExerciseEntity::class,
        parentColumns = ["id"],
        childColumns = ["exercise_id"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["exercise_id"]), Index(value = ["date"]), Index(value = ["user_id"])]
)
data class WorkoutLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "user_id") val userId: String = "",
    @ColumnInfo(name = "exercise_id") val exerciseId: Long,
    @ColumnInfo(name = "date") val date: Long,
    @ColumnInfo(name = "set_number") val setNumber: Int,
    @ColumnInfo(name = "reps") val reps: Int,
    @ColumnInfo(name = "weight_kg") val weightKg: Double,
    @ColumnInfo(name = "completed") val completed: Boolean = false,
    @ColumnInfo(name = "notes") val notes: String? = null
)
