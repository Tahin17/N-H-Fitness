package com.aegisfit.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "daily_care_items",
    indices = [Index(value = ["user_id"]), Index(value = ["time_slot"])]
)
data class DailyCareItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "user_id") val userId: String = "",
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "time_slot") val timeSlot: String, // MORNING, AFTERNOON, EVENING, NIGHT
    @ColumnInfo(name = "category") val category: String, // MEDICINE, SKINCARE, HYGIENE, HABIT, TASK, OTHER
    @ColumnInfo(name = "notes") val notes: String? = null,
    @ColumnInfo(name = "time_hint") val timeHint: String? = null,
    @ColumnInfo(name = "created_at_ms") val createdAtMs: Long = System.currentTimeMillis()
)
