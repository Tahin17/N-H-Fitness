package com.aegisfit.app.presentation.screen.nutrition

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aegisfit.app.domain.model.HydrationLog
import com.aegisfit.app.domain.model.FoodLog
import com.aegisfit.app.domain.model.NutritionSummary
import com.aegisfit.app.domain.repository.AuthRepository
import com.aegisfit.app.domain.repository.HydrationRepository
import com.aegisfit.app.domain.repository.NutritionRepository
import com.aegisfit.app.domain.usecase.nutrition.GetDailyNutritionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.aegisfit.app.util.DateUtils

data class NutritionState(
    val summary: NutritionSummary? = null,
    val foodLogs: List<FoodLog> = emptyList(),
    val waterTotalMl: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class NutritionViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val getDailyNutritionUseCase: GetDailyNutritionUseCase,
    private val hydrationRepository: HydrationRepository,
    private val nutritionRepository: NutritionRepository
) : ViewModel() {

    private val _state = MutableStateFlow(NutritionState())
    val state: StateFlow<NutritionState> = _state.asStateFlow()

    init {
        loadData(DateUtils.todayStartMillis())
    }

    fun loadData(dateTimestamp: Long) {
        val userId = authRepository.currentUserId
        if (userId.isNullOrBlank()) {
            _state.update { it.copy(isLoading = false, error = "Your session expired. Please sign in again.") }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                getDailyNutritionUseCase(userId, dateTimestamp).collect { summary ->
                    _state.update { it.copy(summary = summary, isLoading = false) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message ?: "Failed to load nutrition", isLoading = false) }
            }
        }

        viewModelScope.launch {
            nutritionRepository.getFoodLogsByDate(userId, dateTimestamp).collect { logs ->
                _state.update { it.copy(foodLogs = logs) }
            }
        }

        viewModelScope.launch {
            try {
                hydrationRepository.getHydrationByDate(userId, dateTimestamp).collect { logs ->
                    val total = logs.sumOf { it.amountMl }
                    _state.update { it.copy(waterTotalMl = total) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = "Hydration data could not be loaded.") }
            }
        }
    }

    fun addWater(amountMl: Int) {
        if (amountMl !in 50..2_000) {
            _state.update { it.copy(error = "Water amount must be between 50 ml and 2,000 ml.") }
            return
        }
        val userId = authRepository.currentUserId
        if (userId.isNullOrBlank()) {
            _state.update { it.copy(error = "Your session expired. Please sign in again.") }
            return
        }
        viewModelScope.launch {
            try {
                val now = DateUtils.todayStartMillis()
                val log = HydrationLog(
                    userId = userId,
                    date = now,
                    amountMl = amountMl,
                    withCreatine = false,
                    timestamp = System.currentTimeMillis()
                )
                hydrationRepository.saveHydrationLog(log)
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message ?: "Failed to add water") }
            }
        }
    }
}
