package com.aegisfit.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "daily_care_logs",
    foreignKeys = [
        ForeignKey(
            entity = DailyCareItemEntity::class,
            parentColumns = ["id"],
            childColumns = ["care_item_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["care_item_id"]),
        Index(value = ["user_id", "date"]),
        Index(value = ["user_id", "date", "care_item_id"], unique = true)
    ]
)
data class DailyCareLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "user_id") val userId: String = "",
    @ColumnInfo(name = "date") val date: Long, // Start-of-day epoch millis
    @ColumnInfo(name = "care_item_id") val careItemId: Long,
    @ColumnInfo(name = "completed") val completed: Boolean = false,
    @ColumnInfo(name = "completed_at_ms") val completedAtMs: Long? = null
)
