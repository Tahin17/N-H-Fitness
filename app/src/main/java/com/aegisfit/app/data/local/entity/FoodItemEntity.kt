package com.aegisfit.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "food_items",
    indices = [
        Index(value = ["barcode"], unique = true),
        Index(value = ["external_id"], unique = true)
    ]
)
data class FoodItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "brand") val brand: String? = null,
    @ColumnInfo(name = "calories_per_100g") val caloriesPer100g: Double,
    @ColumnInfo(name = "protein_per_100g") val proteinPer100g: Double,
    @ColumnInfo(name = "carbs_per_100g") val carbsPer100g: Double,
    @ColumnInfo(name = "fat_per_100g") val fatPer100g: Double,
    @ColumnInfo(name = "fiber_per_100g") val fiberPer100g: Double = 0.0,
    @ColumnInfo(name = "default_serving_size_g") val defaultServingSizeG: Double = 100.0,
    @ColumnInfo(name = "serving_description") val servingDescription: String? = null,
    @ColumnInfo(name = "barcode") val barcode: String? = null,
    @ColumnInfo(name = "is_local_bd") val isLocalBd: Boolean = false,
    @ColumnInfo(name = "category") val category: String? = null,
    @ColumnInfo(name = "image_url") val imageUrl: String? = null,
    @ColumnInfo(name = "external_id") val externalId: String? = null,
    @ColumnInfo(name = "source") val source: String = "local",
    @ColumnInfo(name = "last_updated_epoch_ms") val lastUpdatedEpochMs: Long = 0L
)
