package com.aegisfit.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "food_search_cache")
data class FoodSearchCacheEntity(
    @PrimaryKey val query: String,
    @ColumnInfo(name = "fetched_at_epoch_ms") val fetchedAtEpochMs: Long,
    @ColumnInfo(name = "result_count") val resultCount: Int
)
