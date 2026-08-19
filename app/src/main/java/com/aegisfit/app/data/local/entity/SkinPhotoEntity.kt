package com.aegisfit.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "skin_photos", indices = [Index(value = ["date"]), Index(value = ["user_id"])])
data class SkinPhotoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "user_id") val userId: String = "",
    @ColumnInfo(name = "date") val date: Long,
    @ColumnInfo(name = "photo_path") val photoPath: String,
    @ColumnInfo(name = "angle_type") val angleType: String, // Front, Left, Right
    @ColumnInfo(name = "notes") val notes: String? = null
)
