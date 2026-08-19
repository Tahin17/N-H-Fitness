package com.aegisfit.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "food_logs",
    foreignKeys = [ForeignKey(
        entity = FoodItemEntity::class,
        parentColumns = ["id"],
        childColumns = ["food_item_id"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["food_item_id"]), Index(value = ["date"]), Index(value = ["user_id"])]
)
data class FoodLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "user_id") val userId: String = "",
    @ColumnInfo(name = "date") val date: Long,
    @ColumnInfo(name = "food_item_id") val foodItemId: Long,
    @ColumnInfo(name = "serving_size_g") val servingSizeG: Double,
    @ColumnInfo(name = "meal_type") val mealType: String, // Breakfast, Lunch, Dinner, Snack
    @ColumnInfo(name = "calories") val calories: Double,
    @ColumnInfo(name = "protein") val protein: Double,
    @ColumnInfo(name = "carbs") val carbs: Double,
    @ColumnInfo(name = "fat") val fat: Double,
    @ColumnInfo(name = "timestamp") val timestamp: Long = System.currentTimeMillis()
)
