package com.aegisfit.app.presentation.screen.workout

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aegisfit.app.domain.model.WorkoutLog
import com.aegisfit.app.domain.repository.AuthRepository
import com.aegisfit.app.domain.repository.WorkoutRepository
import com.aegisfit.app.domain.usecase.workout.LogWorkoutSetUseCase
import com.aegisfit.app.domain.usecase.workout.WorkoutMetrics
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.flow.collectLatest
import com.aegisfit.app.domain.repository.UserRepository
import com.aegisfit.app.util.DateUtils

data class ExerciseSetState(
    val id: Long = 0L,
    val setNumber: Int = 1,
    val reps: String = "",
    val weight: String = "",
    val isCompleted: Boolean = false
)

data class WorkoutDetailState(
    val isLoading: Boolean = false,
    val sets: List<ExerciseSetState> = emptyList(),
    val ghostSets: List<ExerciseSetState> = emptyList(),
    val error: String? = null,
    val exerciseName: String = "",
    val totalCaloriesBurned: Double = 0.0,
    val isFinishing: Boolean = false,
    val hasCommittedSets: Boolean = false
)

@HiltViewModel
class WorkoutDetailViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val workoutRepository: WorkoutRepository,
    private val userRepository: UserRepository,
    private val logWorkoutSetUseCase: LogWorkoutSetUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val exerciseId: Long = savedStateHandle.get<String>("exerciseId")?.toLongOrNull() ?: 0L
    private val date: Long = DateUtils.startOfDay(savedStateHandle.get<String>("date")?.toLongOrNull() ?: System.currentTimeMillis())

    private val _uiState = MutableStateFlow(WorkoutDetailState())
    val uiState: StateFlow<WorkoutDetailState> = _uiState.asStateFlow()

    private var bodyWeightKg: Double = 70.0
    private val userId: String
        get() = authRepository.currentUserId ?: ""

    init {
        loadInitialData()
        loadLogs()
        loadGhostLogs()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            try {
                bodyWeightKg = userRepository.getUserProfileOnce(userId)?.weightKg ?: 70.0
                val exercise = workoutRepository.getExerciseById(exerciseId)
                _uiState.update {
                    it.copy(
                        exerciseName = exercise?.name ?: it.exerciseName,
                        totalCaloriesBurned = calculateCalories(it.sets)
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Exercise details could not be loaded.") }
            }
        }
    }

    private fun loadGhostLogs() {
        viewModelScope.launch {
            try {
                val ghostLogs = workoutRepository.getLastSessionLogs(userId, exerciseId, date)
                val mappedGhosts = ghostLogs.map { log ->
                    ExerciseSetState(
                        id = log.id,
                        setNumber = log.setNumber,
                        reps = log.reps.toString(),
                        weight = log.weightKg.toString(),
                        isCompleted = log.completed
                    )
                }
                _uiState.update { it.copy(ghostSets = mappedGhosts) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Previous-session sets could not be loaded.") }
            }
        }
    }

    private fun loadLogs() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                workoutRepository.getWorkoutLogsByDateAndExercise(userId, date, exerciseId).collectLatest { logs ->
                    val savedLogsMap: Map<Int, WorkoutLog> = logs.associateBy { it.setNumber }
                    
                    val currentSets = _uiState.value.sets
                    val savedMax = logs.maxOfOrNull { it.setNumber } ?: 0
                    val currentMax = currentSets.maxOfOrNull { it.setNumber } ?: 0
                    val maxSetNumber = maxOf(3, maxOf(savedMax, currentMax))
                    
                    val mergedSets = (1..maxSetNumber).map { setNumber ->
                        val savedLog = savedLogsMap[setNumber]
                        val currentSet = currentSets.find { it.setNumber == setNumber }
                        
                        if (savedLog != null) {
                            val savedReps = if (savedLog.reps > 0) savedLog.reps.toString() else ""
                            val savedWeight = if (savedLog.completed || savedLog.weightKg > 0.0) {
                                savedLog.weightKg.toString()
                            } else ""
                            
                            val finalReps = if (currentSet != null && currentSet.reps.toIntOrNull() == savedLog.reps) {
                                currentSet.reps
                            } else {
                                savedReps
                            }
                            
                            val finalWeight = if (currentSet != null && currentSet.weight.toDoubleOrNull() == savedLog.weightKg) {
                                currentSet.weight
                            } else {
                                savedWeight
                            }

                            ExerciseSetState(
                                id = savedLog.id,
                                setNumber = savedLog.setNumber,
                                reps = finalReps,
                                weight = finalWeight,
                                isCompleted = savedLog.completed
                            )
                        } else if (currentSet != null) {
                            currentSet
                        } else {
                            ExerciseSetState(setNumber = setNumber)
                        }
                    }
                    
                    val totalCalories = calculateCalories(mergedSets)
                    
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            sets = mergedSets,
                            totalCaloriesBurned = totalCalories,
                            hasCommittedSets = logs.any(WorkoutLog::completed)
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    private fun calculateCalories(sets: List<ExerciseSetState>): Double {
        return WorkoutMetrics.estimateStrengthCalories(
            completedSets = sets.count { it.isCompleted },
            bodyWeightKg = bodyWeightKg
        )
    }

    fun onSetAdded() {
        _uiState.update { state ->
            if (state.sets.size >= MAX_SETS) {
                return@update state.copy(error = "A maximum of $MAX_SETS sets is supported per exercise.")
            }
            val newSetNumber = (state.sets.maxOfOrNull { it.setNumber } ?: 0) + 1
            val newSet = ExerciseSetState(setNumber = newSetNumber)
            state.copy(sets = state.sets + newSet)
        }
    }

    fun onSetChanged(setId: Long, setNumber: Int, reps: String, weight: String, isCompleted: Boolean) {
        if (setNumber !in 1..MAX_SETS || reps.length > 4 ||
            (reps.isNotEmpty() && !reps.all(Char::isDigit)) ||
            weight.length > 7 || weight.count { it == '.' } > 1 ||
            weight.any { !it.isDigit() && it != '.' }
        ) return

        val repsValue = reps.toIntOrNull()
        val weightValue = weight.toDoubleOrNull()
        if (isCompleted && (repsValue == null || repsValue !in 1..1_000 ||
                weightValue == null || !weightValue.isFinite() || weightValue !in 0.0..1_000.0)
        ) {
            _uiState.update { it.copy(error = "Enter valid reps and weight before completing a set.") }
            return
        }

        // Set checks are a local draft. The dashboard is updated only after Finish Exercise.
        _uiState.update { state ->
            val updatedSets = state.sets.map {
                if (it.setNumber == setNumber) {
                    it.copy(reps = reps, weight = weight, isCompleted = isCompleted)
                } else {
                    it
                }
            }
            state.copy(
                sets = updatedSets,
                totalCaloriesBurned = calculateCalories(updatedSets),
                error = null
            )
        }

    }

    fun finishExercise(onFinished: () -> Unit) {
        val current = _uiState.value
        if (current.isFinishing) return
        val setsToSave = current.sets.filter {
            it.id > 0L || it.isCompleted || it.reps.isNotBlank() || it.weight.isNotBlank()
        }
        if (setsToSave.none { it.isCompleted } && !current.hasCommittedSets) {
            _uiState.update { it.copy(error = "Complete at least one valid set before finishing.") }
            return
        }

        val invalidCompletedSet = setsToSave.firstOrNull { set ->
            val reps = set.reps.toIntOrNull()
            val weight = set.weight.toDoubleOrNull()
            set.isCompleted && (reps == null || reps !in 1..1_000 ||
                weight == null || !weight.isFinite() || weight !in 0.0..1_000.0)
        }
        if (invalidCompletedSet != null) {
            _uiState.update {
                it.copy(error = "Set ${invalidCompletedSet.setNumber} needs valid reps and weight.")
            }
            return
        }
        if (userId.isBlank() || exerciseId <= 0L) {
            _uiState.update { it.copy(error = "Your session expired. Sign in again and retry.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isFinishing = true, error = null) }
            try {
                setsToSave.forEach { set ->
                    logWorkoutSetUseCase(
                        WorkoutLog(
                            id = set.id,
                            userId = userId,
                            date = date,
                            exerciseId = exerciseId,
                            setNumber = set.setNumber,
                            reps = set.reps.toIntOrNull() ?: 0,
                            weightKg = set.weight.toDoubleOrNull() ?: 0.0,
                            completed = set.isCompleted
                        )
                    )
                }
                _uiState.update {
                    it.copy(
                        isFinishing = false,
                        hasCommittedSets = setsToSave.any(ExerciseSetState::isCompleted)
                    )
                }
                onFinished()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isFinishing = false,
                        error = e.localizedMessage ?: "Exercise could not be saved. Please retry."
                    )
                }
            }
        }
    }

    private companion object {
        const val MAX_SETS = 20
    }
}
