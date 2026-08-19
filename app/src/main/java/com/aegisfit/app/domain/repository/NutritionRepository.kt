package com.aegisfit.app.domain.repository

import com.aegisfit.app.domain.model.FoodItem
import com.aegisfit.app.domain.model.FoodLog
import com.aegisfit.app.domain.model.FoodSearchRefreshResult
import kotlinx.coroutines.flow.Flow

interface NutritionRepository {
    fun searchFoodLocally(query: String): Flow<List<FoodItem>>
    fun getSuggestedFoods(): Flow<List<FoodItem>>
    suspend fun refreshFoodSearch(query: String, forceRefresh: Boolean = false): FoodSearchRefreshResult
    fun getFoodLogsByDate(userId: String, date: Long): Flow<List<FoodLog>>
    suspend fun saveFoodLog(log: FoodLog): Long
    suspend fun saveFoodItem(item: FoodItem): Long
    
    fun getTotalCaloriesForDate(userId: String, date: Long): Flow<Double?>
    fun getTotalProteinForDate(userId: String, date: Long): Flow<Double?>
    fun getTotalCarbsForDate(userId: String, date: Long): Flow<Double?>
    fun getTotalFatForDate(userId: String, date: Long): Flow<Double?>
    fun getTotalCaloriesInRange(userId: String, startDate: Long, endDate: Long): Flow<Double?>
}
