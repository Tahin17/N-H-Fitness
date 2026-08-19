package com.aegisfit.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aegisfit.app.data.local.entity.FoodSearchCacheEntity
import kotlin.jvm.JvmSuppressWildcards

@Dao
@JvmSuppressWildcards
interface FoodSearchCacheDao {
    @Query("SELECT * FROM food_search_cache WHERE query = :query LIMIT 1")
    suspend fun get(query: String): FoodSearchCacheEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entry: FoodSearchCacheEntity): Long

    @Query("DELETE FROM food_search_cache WHERE fetched_at_epoch_ms < :cutoffEpochMs")
    suspend fun deleteOlderThan(cutoffEpochMs: Long): Int
}
