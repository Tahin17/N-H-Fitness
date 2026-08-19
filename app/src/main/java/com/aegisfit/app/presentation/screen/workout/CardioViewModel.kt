package com.aegisfit.app.presentation.screen.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aegisfit.app.domain.model.CardioLog
import com.aegisfit.app.domain.repository.AuthRepository
import com.aegisfit.app.domain.repository.UserRepository
import com.aegisfit.app.domain.repository.WorkoutRepository
import com.aegisfit.app.domain.usecase.workout.EstimateCalorieBurnUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.aegisfit.app.util.DateUtils

data class CardioState(
    val selectedType: String = "Treadmill",
    val durationInput: String = "15",
    val estimatedCalories: Int = 0,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class CardioViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val workoutRepository: WorkoutRepository,
    private val estimateCalorieBurnUseCase: EstimateCalorieBurnUseCase,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CardioState())
    val state: StateFlow<CardioState> = _state.asStateFlow()

    private val userId: String
        get() = authRepository.currentUserId ?: ""

    init {
        updateEstimatedCalories()
    }

    fun selectType(type: String) {
        if (type !in CARDIO_TYPES) return
        _state.update { it.copy(selectedType = type) }
        updateEstimatedCalories()
    }

    fun updateDuration(duration: String) {
        if (duration.length <= 3 && (duration.isEmpty() || duration.all(Char::isDigit))) {
            _state.update { it.copy(durationInput = duration, errorMessage = null) }
            updateEstimatedCalories()
        }
    }

    private fun updateEstimatedCalories() {
        viewModelScope.launch {
            try {
                val weight = userRepository.getUserProfileOnce(userId)?.weightKg ?: 70.0
                val duration = (_state.value.durationInput.toIntOrNull() ?: 0).coerceIn(0, 600)
                val calories = estimateCalorieBurnUseCase(
                    _state.value.selectedType,
                    duration,
                    weight
                ).toInt()
                _state.update { it.copy(estimatedCalories = calories) }
            } catch (e: Exception) {
                _state.update {
                    it.copy(estimatedCalories = 0, errorMessage = "Calorie estimate is unavailable.")
                }
            }
        }
    }

    fun logCardio() {
        val duration = _state.value.durationInput.toIntOrNull()
        val userId = authRepository.currentUserId
        val error = when {
            userId.isNullOrBlank() -> "Your session expired. Please sign in again."
            duration == null || duration !in 1..600 -> "Duration must be between 1 and 600 minutes."
            _state.value.selectedType !in CARDIO_TYPES -> "Choose a supported activity."
            else -> null
        }
        if (error != null) {
            _state.update { it.copy(errorMessage = error) }
            return
        }
        if (_state.value.isSaving) return

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, errorMessage = null) }
            runCatching {
                val log = CardioLog(
                    userId = requireNotNull(userId),
                    date = DateUtils.todayStartMillis(),
                    type = _state.value.selectedType,
                    durationMin = requireNotNull(duration),
                    caloriesBurned = _state.value.estimatedCalories.toDouble(),
                    completed = true
                )
                workoutRepository.saveCardioLog(log)
            }.onSuccess {
                _state.update { it.copy(isSaving = false, isSaved = true) }
            }.onFailure { throwable ->
                _state.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = throwable.message ?: "Cardio could not be logged."
                    )
                }
            }
        }
    }

    private companion object {
        val CARDIO_TYPES = setOf("Treadmill", "Stairmaster", "Cycling", "Outdoor Stairs")
    }
}
