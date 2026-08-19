package com.aegisfit.app.presentation.screen.skincare

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aegisfit.app.domain.model.SkincareLog
import com.aegisfit.app.domain.repository.AuthRepository
import com.aegisfit.app.domain.repository.SkincareRepository
import com.aegisfit.app.domain.usecase.skincare.GetSkincareRoutineUseCase
import com.aegisfit.app.domain.usecase.skincare.SaveSkincareLogUseCase
import com.aegisfit.app.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RoutineItem(
    val id: Long,
    val name: String,
    val category: String = "",
    val instructions: String? = null,
    val dosage: String? = null,
    val warning: String? = null,
    val isCompleted: Boolean = false
)

data class SkincareState(
    val amRoutine: List<RoutineItem> = emptyList(),
    val pmRoutine: List<RoutineItem> = emptyList(),
    val completionStatus: Float = 0f,
    val isNightA: Boolean = false
)

@HiltViewModel
class SkincareViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val getSkincareRoutineUseCase: GetSkincareRoutineUseCase,
    private val saveSkincareLogUseCase: SaveSkincareLogUseCase,
    private val skincareRepository: SkincareRepository
) : ViewModel() {

    private val _isNightA = MutableStateFlow(DateUtils.daysSinceEpoch() % 2 == 0L)
    private val _state = MutableStateFlow(SkincareState(isNightA = _isNightA.value))
    val state: StateFlow<SkincareState> = _state.asStateFlow()

    private val userId: String
        get() = authRepository.currentUserId ?: ""

    init {
        loadRoutines()
    }

    private fun loadRoutines() {
        val today = DateUtils.todayStartMillis()
        viewModelScope.launch {
            combine(
                getSkincareRoutineUseCase("AM"),
                getSkincareRoutineUseCase("PM"),
                skincareRepository.getLogsByDate(userId, today),
                _isNightA
            ) { am, pm, logs, isNightA ->
                val currentNightGroup = if (isNightA) "NightA" else "NightB"

                val amItems = am.map { routine ->
                    RoutineItem(
                        id = routine.id,
                        name = routine.productName,
                        category = routine.productCategory,
                        instructions = routine.instructions,
                        dosage = routine.dosage,
                        warning = routine.warning,
                        isCompleted = logs.any { it.stepId == routine.id && it.completed }
                    )
                }
                val pmItems = pm.filter { it.alternateGroup == null || it.alternateGroup == currentNightGroup }
                    .map { routine ->
                        RoutineItem(
                            id = routine.id,
                            name = routine.productName,
                            category = routine.productCategory,
                            instructions = routine.instructions,
                            dosage = routine.dosage,
                            warning = routine.warning,
                            isCompleted = logs.any { it.stepId == routine.id && it.completed }
                        )
                    }
                SkincareState(
                    amRoutine = amItems,
                    pmRoutine = pmItems,
                    completionStatus = calculateCompletion(amItems, pmItems),
                    isNightA = isNightA
                )
            }.collect { newState ->
                _state.value = newState
            }
        }
    }

    fun toggleNight() {
        _isNightA.value = !_isNightA.value
    }

    fun toggleAmRoutine(itemId: Long, isCompleted: Boolean) {
        val today = DateUtils.todayStartMillis()
        viewModelScope.launch {
            saveSkincareLogUseCase(SkincareLog(
                userId = userId,
                date = today,
                routineType = "AM",
                stepId = itemId,
                completed = isCompleted
            ))
        }
    }

    fun togglePmRoutine(itemId: Long, isCompleted: Boolean) {
        val today = DateUtils.todayStartMillis()
        viewModelScope.launch {
            saveSkincareLogUseCase(SkincareLog(
                userId = userId,
                date = today,
                routineType = "PM",
                stepId = itemId,
                completed = isCompleted
            ))
        }
    }

    private fun calculateCompletion(am: List<RoutineItem>, pm: List<RoutineItem>): Float {
        val total = am.size + pm.size
        if (total == 0) return 0f
        val completed = am.count { it.isCompleted } + pm.count { it.isCompleted }
        return completed.toFloat() / total.toFloat()
    }
}
