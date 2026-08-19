package com.aegisfit.app.presentation.screen.nutrition

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.SavedStateHandle
import com.aegisfit.app.domain.model.FoodItem
import com.aegisfit.app.domain.model.FoodLog
import com.aegisfit.app.domain.model.MealType
import com.aegisfit.app.domain.repository.AuthRepository
import com.aegisfit.app.domain.usecase.nutrition.FoodSearchPolicy
import com.aegisfit.app.domain.usecase.nutrition.LogFoodUseCase
import com.aegisfit.app.domain.usecase.nutrition.SearchFoodUseCase
import com.aegisfit.app.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class FoodSearchViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val searchFoodUseCase: SearchFoodUseCase,
    private val logFoodUseCase: LogFoodUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val selectedMeal: MealType = MealType.fromString(
        savedStateHandle.get<String>("mealType").orEmpty()
    )

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<FoodItem>>(emptyList())
    val searchResults = _searchResults.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message = _message.asStateFlow()

    init {
        _searchQuery
            .debounce(250)
            .distinctUntilChanged()
            .flatMapLatest(searchFoodUseCase::invoke)
            .onEach { _searchResults.value = it }
            .launchIn(viewModelScope)
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query.take(MAX_QUERY_LENGTH)
    }

    fun searchOnline() {
        val query = FoodSearchPolicy.normalize(_searchQuery.value)
        if (query.length < FoodSearchPolicy.MIN_REMOTE_QUERY_LENGTH) {
            _message.value = "Enter at least ${FoodSearchPolicy.MIN_REMOTE_QUERY_LENGTH} characters."
            return
        }
        if (_isRefreshing.value) return

        viewModelScope.launch {
            _isRefreshing.value = true
            runCatching { searchFoodUseCase.refresh(query) }
                .onSuccess { result ->
                    _message.value = when {
                        result.usedFreshCache -> "Showing recently saved results."
                        result.itemsAddedOrUpdated == 0 -> "No additional foods were found."
                        else -> "Saved ${result.itemsAddedOrUpdated} results for offline use."
                    }
                }
                .onFailure { error ->
                    _message.value = error.message
                        ?: "Online food search is unavailable. Saved foods still work."
                }
            _isRefreshing.value = false
        }
    }

    fun logFood(foodItem: FoodItem, servings: Double) {
        val userId = authRepository.currentUserId
        if (userId.isNullOrBlank()) {
            _message.value = "Your session expired. Please sign in again."
            return
        }
        if (!servings.isFinite() || servings !in MIN_SERVINGS..MAX_SERVINGS) {
            _message.value = "Servings must be between $MIN_SERVINGS and $MAX_SERVINGS."
            return
        }

        viewModelScope.launch {
            runCatching {
                logFoodUseCase(
                    FoodLog(
                        userId = userId,
                        date = DateUtils.todayStartMillis(),
                        foodItem = foodItem,
                        servings = servings,
                        mealType = selectedMeal.name
                    )
                )
            }.onSuccess {
                _message.value = "${foodItem.name} added to ${selectedMeal.displayName.lowercase()}."
            }.onFailure { error ->
                _message.value = error.message ?: "The food could not be logged."
            }
        }
    }

    fun consumeMessage() {
        _message.value = null
    }

    private companion object {
        const val MAX_QUERY_LENGTH = 80
        const val MIN_SERVINGS = 0.1
        const val MAX_SERVINGS = 20.0
    }
}
