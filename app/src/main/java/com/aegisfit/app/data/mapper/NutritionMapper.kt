package com.aegisfit.app.data.mapper

import com.aegisfit.app.data.local.entity.FoodItemEntity
import com.aegisfit.app.data.local.entity.FoodLogEntity
import com.aegisfit.app.data.local.entity.HydrationLogEntity
import com.aegisfit.app.data.remote.dto.OffProductDto
import com.aegisfit.app.data.remote.dto.UsdaFoodDto
import com.aegisfit.app.domain.model.FoodItem
import com.aegisfit.app.domain.model.FoodLog
import com.aegisfit.app.domain.model.HydrationLog
import com.aegisfit.app.util.NutritionMath

fun OffProductDto.toDomain(): FoodItem {
    return FoodItem(
        name = this.productName ?: "Unknown",
        brand = this.brands,
        caloriesPer100g = this.nutriments?.energyKcal100g ?: 0.0,
        proteinPer100g = this.nutriments?.proteins100g ?: 0.0,
        carbsPer100g = this.nutriments?.carbohydrates100g ?: 0.0,
        fatPer100g = this.nutriments?.fat100g ?: 0.0,
        fiberPer100g = this.nutriments?.fiber100g ?: 0.0,
        barcode = this.code,
        category = this.categories,
        imageUrl = this.imageUrl,
        isLocalBd = false,
        externalId = this.code?.takeIf(String::isNotBlank)?.let { "off:$it" },
        source = "open_food_facts"
    )
}

fun UsdaFoodDto.toDomain(): FoodItem {
    val nutrientsMap = this.foodNutrients.associate { it.nutrientName to it.value }
    return FoodItem(
        name = this.description,
        brand = this.brandOwner,
        caloriesPer100g = nutrientsMap["Energy"] ?: nutrientsMap["Energy (kcal)"] ?: 0.0,
        proteinPer100g = nutrientsMap["Protein"] ?: 0.0,
        carbsPer100g = nutrientsMap["Carbohydrate, by difference"] ?: 0.0,
        fatPer100g = nutrientsMap["Total lipid (fat)"] ?: 0.0,
        fiberPer100g = nutrientsMap["Fiber, total dietary"] ?: 0.0,
        barcode = this.gtinUpc,
        isLocalBd = false,
        externalId = "usda:${this.fdcId}",
        source = "usda"
    )
}

fun FoodItemEntity.toDomain(): FoodItem {
    return FoodItem(
        id = this.id,
        name = this.name,
        brand = this.brand,
        caloriesPer100g = this.caloriesPer100g,
        proteinPer100g = this.proteinPer100g,
        carbsPer100g = this.carbsPer100g,
        fatPer100g = this.fatPer100g,
        fiberPer100g = this.fiberPer100g,
        defaultServingSizeG = this.defaultServingSizeG,
        servingDescription = this.servingDescription,
        barcode = this.barcode,
        isLocalBd = this.isLocalBd,
        category = this.category,
        imageUrl = this.imageUrl,
        externalId = this.externalId,
        source = this.source,
        lastUpdatedEpochMs = this.lastUpdatedEpochMs
    )
}

fun FoodItem.toEntity(): FoodItemEntity {
    return FoodItemEntity(
        id = this.id,
        name = this.name,
        brand = this.brand,
        caloriesPer100g = this.caloriesPer100g,
        proteinPer100g = this.proteinPer100g,
        carbsPer100g = this.carbsPer100g,
        fatPer100g = this.fatPer100g,
        fiberPer100g = this.fiberPer100g,
        defaultServingSizeG = this.defaultServingSizeG,
        servingDescription = this.servingDescription,
        barcode = this.barcode,
        isLocalBd = this.isLocalBd,
        category = this.category,
        imageUrl = this.imageUrl,
        externalId = this.externalId,
        source = this.source,
        lastUpdatedEpochMs = this.lastUpdatedEpochMs
    )
}

fun FoodLog.toEntity(): FoodLogEntity {
    val servingSizeG = NutritionMath.servingWeightG(
        defaultServingSizeG = this.foodItem.defaultServingSizeG,
        servings = this.servings
    )
    return FoodLogEntity(
        id = this.id,
        userId = this.userId,
        date = this.date,
        foodItemId = this.foodItem.id,
        servingSizeG = servingSizeG,
        mealType = this.mealType,
        calories = NutritionMath.nutrientAmount(this.foodItem.caloriesPer100g, servingSizeG),
        protein = NutritionMath.nutrientAmount(this.foodItem.proteinPer100g, servingSizeG),
        carbs = NutritionMath.nutrientAmount(this.foodItem.carbsPer100g, servingSizeG),
        fat = NutritionMath.nutrientAmount(this.foodItem.fatPer100g, servingSizeG)
    )
}

fun FoodLogEntity.toDomain(foodItem: FoodItem): FoodLog {
    return FoodLog(
        id = this.id,
        userId = this.userId,
        date = this.date,
        foodItem = foodItem,
        servings = this.servingSizeG / foodItem.defaultServingSizeG.coerceAtLeast(1.0),
        mealType = this.mealType
    )
}

fun HydrationLogEntity.toDomain(): HydrationLog {
    return HydrationLog(
        id = this.id,
        userId = this.userId,
        date = this.date,
        amountMl = this.amountMl,
        withCreatine = this.withCreatine,
        timestamp = this.timestamp
    )
}

fun HydrationLog.toEntity(): HydrationLogEntity {
    return HydrationLogEntity(
        id = this.id,
        userId = this.userId,
        date = this.date,
        amountMl = this.amountMl,
        withCreatine = this.withCreatine,
        timestamp = this.timestamp
    )
}
