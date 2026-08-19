package com.aegisfit.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aegisfit.app.data.local.entity.FoodLogEntity
import kotlinx.coroutines.flow.Flow
import kotlin.jvm.JvmSuppressWildcards

@Dao
@JvmSuppressWildcards
interface FoodLogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: FoodLogEntity): Long

    @Query("SELECT * FROM food_logs WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): FoodLogEntity?

    @Query("SELECT id FROM food_logs WHERE user_id = :userId AND date = :date AND timestamp = :timestamp AND meal_type = :mealType LIMIT 1")
    suspend fun findId(userId: String, date: Long, timestamp: Long, mealType: String): Long?

    @Query("SELECT * FROM food_logs WHERE user_id = :userId AND date = :date ORDER BY meal_type, timestamp")
    fun getForDate(userId: String, date: Long): Flow<List<FoodLogEntity>>

    @Query("SELECT SUM(calories) FROM food_logs WHERE user_id = :userId AND date = :date")
    fun getTotalCaloriesForDate(userId: String, date: Long): Flow<Double?>

    @Query("SELECT SUM(protein) FROM food_logs WHERE user_id = :userId AND date = :date")
    fun getTotalProteinForDate(userId: String, date: Long): Flow<Double?>

    @Query("SELECT SUM(carbs) FROM food_logs WHERE user_id = :userId AND date = :date")
    fun getTotalCarbsForDate(userId: String, date: Long): Flow<Double?>

    @Query("SELECT SUM(fat) FROM food_logs WHERE user_id = :userId AND date = :date")
    fun getTotalFatForDate(userId: String, date: Long): Flow<Double?>

    @Query("SELECT * FROM food_logs WHERE user_id = :userId AND date = :date AND meal_type = :mealType ORDER BY timestamp")
    fun getForDateAndMeal(userId: String, date: Long, mealType: String): Flow<List<FoodLogEntity>>

    @Query("DELETE FROM food_logs WHERE user_id = :userId AND id = :id")
    suspend fun delete(userId: String, id: Long): Int

    @Query("SELECT * FROM food_logs WHERE user_id = :userId ORDER BY date DESC, timestamp DESC")
    fun getAll(userId: String): Flow<List<FoodLogEntity>>

    @Query("SELECT SUM(calories) FROM food_logs WHERE user_id = :userId AND date >= :startDate AND date <= :endDate")
    fun getTotalCaloriesInRange(userId: String, startDate: Long, endDate: Long): Flow<Double?>

    @Query("SELECT * FROM food_logs WHERE user_id = :userId AND date >= :since")
    suspend fun getLogsSince(userId: String, since: Long): List<FoodLogEntity>

    @Query("SELECT * FROM food_logs WHERE user_id = :userId")
    suspend fun getAllLogsSync(userId: String): List<FoodLogEntity>
}
