package com.aegisfit.app.presentation.screen.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aegisfit.app.domain.model.Exercise
import com.aegisfit.app.domain.model.WorkoutDay
import com.aegisfit.app.domain.repository.AuthRepository
import com.aegisfit.app.domain.repository.WorkoutRepository
import com.aegisfit.app.domain.usecase.workout.GenerateRandomWorkoutUseCase
import com.aegisfit.app.domain.usecase.workout.GetWorkoutPlanUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WorkoutState(
    val selectedDay: WorkoutDay? = null,
    val days: List<WorkoutDay> = emptyList(),
    val exercises: List<Exercise> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class WorkoutViewModel @Inject constructor(
    private val getWorkoutPlanUseCase: GetWorkoutPlanUseCase,
    private val generateRandomWorkoutUseCase: GenerateRandomWorkoutUseCase,
    private val workoutRepository: WorkoutRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val userId: String
        get() = authRepository.currentUserId ?: ""

    private val _state = MutableStateFlow(WorkoutState())
    val state: StateFlow<WorkoutState> = _state.asStateFlow()

    init {
        loadDays()
    }

    private fun loadDays() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                getWorkoutPlanUseCase().collect { days ->
                    _state.update { it.copy(days = days, isLoading = false) }
                    if (_state.value.selectedDay == null && days.isNotEmpty()) {
                        selectDay(days.first())
                    }
                }
            } catch (e: Exception) {
                _state.update { 
                    it.copy(
                        isLoading = false,
                        error = e.localizedMessage ?: "Failed to load workout days."
                    ) 
                }
            }
        }
    }

    fun generateRandomWorkout() {
        _state.update { it.copy(isLoading = true, error = null, selectedDay = null) }
        viewModelScope.launch {
            try {
                val exercises = generateRandomWorkoutUseCase(userId)
                _state.update { it.copy(exercises = exercises, isLoading = false) }
            } catch (e: Exception) {
                _state.update { 
                    it.copy(
                        isLoading = false,
                        error = e.localizedMessage ?: "Failed to generate random workout."
                    ) 
                }
            }
        }
    }

    fun selectDay(day: WorkoutDay) {
        _state.update { it.copy(selectedDay = day, isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                getWorkoutPlanUseCase.getExercises(day.id).collect { exercises ->
                    _state.update { it.copy(exercises = exercises, isLoading = false) }
                }
            } catch (e: Exception) {
                _state.update { 
                    it.copy(
                        exercises = emptyList(),
                        isLoading = false,
                        error = e.localizedMessage ?: "Failed to load exercises for the selected day."
                    ) 
                }
            }
        }
    }
}
