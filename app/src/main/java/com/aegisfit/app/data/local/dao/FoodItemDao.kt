package com.aegisfit.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.aegisfit.app.data.local.entity.FoodItemEntity
import kotlinx.coroutines.flow.Flow
import kotlin.jvm.JvmSuppressWildcards

@Dao
@JvmSuppressWildcards
interface FoodItemDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: FoodItemEntity): Long

    @Update
    suspend fun update(item: FoodItemEntity): Int

    @Query("SELECT * FROM food_items WHERE name LIKE '%' || :query || '%' OR brand LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%' ORDER BY is_local_bd DESC, name ASC LIMIT 80")
    fun searchByName(query: String): Flow<List<FoodItemEntity>>

    @Query("SELECT * FROM food_items ORDER BY CASE category WHEN 'Fitness' THEN 0 WHEN 'Protein' THEN 1 WHEN 'Fruit' THEN 2 WHEN 'Rice' THEN 3 ELSE 4 END, is_local_bd DESC, name ASC LIMIT 30")
    fun getSuggestedFoods(): Flow<List<FoodItemEntity>>

    @Query("SELECT * FROM food_items ORDER BY is_local_bd DESC, name ASC")
    fun observeSearchCandidates(): Flow<List<FoodItemEntity>>

    @Query("SELECT * FROM food_items WHERE id = :id")
    suspend fun getById(id: Long): FoodItemEntity?

    @Query("SELECT * FROM food_items WHERE barcode = :barcode LIMIT 1")
    suspend fun getByBarcode(barcode: String): FoodItemEntity?

    @Query("SELECT id FROM food_items WHERE external_id = :externalId LIMIT 1")
    suspend fun getIdByExternalId(externalId: String): Long?

    @Query("SELECT id FROM food_items WHERE barcode = :barcode LIMIT 1")
    suspend fun getIdByBarcode(barcode: String): Long?

    @Transaction
    suspend fun upsertRemote(items: List<FoodItemEntity>): Int {
        var changed = 0
        items.forEach { item ->
            val externalMatch = item.externalId?.let { getIdByExternalId(it) }
            val barcodeMatch = item.barcode?.takeIf(String::isNotBlank)?.let { getIdByBarcode(it) }
            // Conflicting unique identifiers should never trigger SQLite REPLACE, which could
            // cascade-delete logs that reference the displaced food row.
            if (externalMatch != null && barcodeMatch != null && externalMatch != barcodeMatch) {
                return@forEach
            }
            val existingId = externalMatch ?: barcodeMatch
            val rows = if (existingId != null) {
                update(item.copy(id = existingId))
            } else {
                if (insert(item.copy(id = 0L)) > 0L) 1 else 0
            }
            changed += rows
        }
        return changed
    }

    @Transaction
    suspend fun upsertOne(item: FoodItemEntity): Long {
        val externalMatch = item.externalId?.let { getIdByExternalId(it) }
        val barcodeMatch = item.barcode?.takeIf(String::isNotBlank)?.let { getIdByBarcode(it) }
        if (externalMatch != null && barcodeMatch != null && externalMatch != barcodeMatch) {
            return externalMatch
        }
        val existingId = externalMatch ?: barcodeMatch ?: item.id.takeIf { it > 0 }
        if (existingId != null) {
            update(item.copy(id = existingId))
            return existingId
        }
        val insertedId = insert(item.copy(id = 0L))
        check(insertedId > 0L) { "Food could not be saved because its identifiers conflict." }
        return insertedId
    }

    @Query("SELECT * FROM food_items WHERE is_local_bd = 1 ORDER BY category, name")
    fun getLocalBdFoods(): Flow<List<FoodItemEntity>>

    @Query("SELECT * FROM food_items ORDER BY is_local_bd DESC, name ASC")
    fun getAllFoods(): Flow<List<FoodItemEntity>>

    @Query("SELECT COUNT(*) FROM food_items")
    suspend fun getCount(): Int

    @Query("SELECT * FROM food_items WHERE category = :category ORDER BY name")
    fun getByCategory(category: String): Flow<List<FoodItemEntity>>
}
