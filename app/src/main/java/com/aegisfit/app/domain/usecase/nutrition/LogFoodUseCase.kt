package com.aegisfit.app.domain.usecase.nutrition

import com.aegisfit.app.domain.model.FoodLog
import com.aegisfit.app.domain.repository.NutritionRepository
import javax.inject.Inject
import com.aegisfit.app.util.DateUtils

class LogFoodUseCase @Inject constructor(
    private val nutritionRepository: NutritionRepository
) {
    suspend operator fun invoke(log: FoodLog): Long {
        val normalizedLog = log.copy(date = DateUtils.startOfDay(log.date))
        val savedId = nutritionRepository.saveFoodItem(normalizedLog.foodItem)
        val updatedLog = normalizedLog.copy(foodItem = normalizedLog.foodItem.copy(id = savedId))
        return nutritionRepository.saveFoodLog(updatedLog)
    }
}
