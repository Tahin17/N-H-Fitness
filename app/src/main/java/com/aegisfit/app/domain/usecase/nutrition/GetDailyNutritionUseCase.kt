package com.aegisfit.app.domain.usecase.nutrition

import com.aegisfit.app.domain.model.NutritionSummary
import com.aegisfit.app.domain.repository.NutritionRepository
import com.aegisfit.app.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.aegisfit.app.util.NutritionMath
import javax.inject.Inject

class GetDailyNutritionUseCase @Inject constructor(
    private val nutritionRepository: NutritionRepository,
    private val userRepository: UserRepository
) {
    operator fun invoke(userId: String, date: Long): Flow<NutritionSummary> {
        return nutritionRepository.getFoodLogsByDate(userId, date).map { logs ->
            var totalCalories = 0
            var totalProtein = 0.0
            var totalCarbs = 0.0
            var totalFat = 0.0

            for (log in logs) {
                val servingWeightG = NutritionMath.servingWeightG(
                    log.foodItem.defaultServingSizeG,
                    log.servings
                )
                totalCalories += NutritionMath.nutrientAmount(
                    log.foodItem.caloriesPer100g,
                    servingWeightG
                ).toInt()
                totalProtein += NutritionMath.nutrientAmount(log.foodItem.proteinPer100g, servingWeightG)
                totalCarbs += NutritionMath.nutrientAmount(log.foodItem.carbsPer100g, servingWeightG)
                totalFat += NutritionMath.nutrientAmount(log.foodItem.fatPer100g, servingWeightG)
            }

            val targetCalories = userRepository.getUserProfileOnce(userId)?.dailyCalorieTarget ?: 1700

            NutritionSummary(
                date = date,
                totalCalories = totalCalories,
                targetCalories = targetCalories,
                totalProtein = totalProtein,
                totalCarbs = totalCarbs,
                totalFat = totalFat
            )
        }
    }
}
