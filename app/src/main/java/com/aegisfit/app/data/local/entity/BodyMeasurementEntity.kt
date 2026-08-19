package com.aegisfit.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "body_measurements")
data class BodyMeasurementEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "user_id") val userId: String = "",
    @ColumnInfo(name = "date") val date: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "chest_cm") val chestCm: Double? = null,
    @ColumnInfo(name = "waist_cm") val waistCm: Double? = null,
    @ColumnInfo(name = "hips_cm") val hipsCm: Double? = null,
    @ColumnInfo(name = "neck_cm") val neckCm: Double? = null,
    @ColumnInfo(name = "left_bicep_cm") val leftBicepCm: Double? = null,
    @ColumnInfo(name = "right_bicep_cm") val rightBicepCm: Double? = null,
    @ColumnInfo(name = "left_forearm_cm") val leftForearmCm: Double? = null,
    @ColumnInfo(name = "right_forearm_cm") val rightForearmCm: Double? = null,
    @ColumnInfo(name = "left_quad_cm") val leftQuadCm: Double? = null,
    @ColumnInfo(name = "right_quad_cm") val rightQuadCm: Double? = null,
    @ColumnInfo(name = "left_calf_cm") val leftCalfCm: Double? = null,
    @ColumnInfo(name = "right_calf_cm") val rightCalfCm: Double? = null
)
