package com.aegisfit.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "skincare_routines")
data class SkincareRoutineEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "routine_type") val routineType: String, // AM, PM
    @ColumnInfo(name = "step_order") val stepOrder: Int,
    @ColumnInfo(name = "product_name") val productName: String,
    @ColumnInfo(name = "product_category") val productCategory: String, // Cleanser, Serum, Moisturizer, Sunscreen, Treatment, BarrierCream
    @ColumnInfo(name = "active_ingredient") val activeIngredient: String? = null,
    @ColumnInfo(name = "notes") val notes: String? = null,
    @ColumnInfo(name = "instructions") val instructions: String? = null,
    @ColumnInfo(name = "dosage") val dosage: String? = null,
    @ColumnInfo(name = "warning") val warning: String? = null,
    @ColumnInfo(name = "alternate_group") val alternateGroup: String? = null
)
