package com.aegisfit.app.domain.usecase.nutrition

import com.aegisfit.app.domain.model.FoodItem
import com.aegisfit.app.domain.model.FoodSearchRefreshResult
import com.aegisfit.app.domain.repository.NutritionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchFoodUseCase @Inject constructor(
    private val nutritionRepository: NutritionRepository
) {
    operator fun invoke(query: String): Flow<List<FoodItem>> =
        if (query.isBlank()) {
            nutritionRepository.getSuggestedFoods()
        } else {
            nutritionRepository.searchFoodLocally(query)
        }

    suspend fun refresh(query: String): FoodSearchRefreshResult =
        nutritionRepository.refreshFoodSearch(query)
}
