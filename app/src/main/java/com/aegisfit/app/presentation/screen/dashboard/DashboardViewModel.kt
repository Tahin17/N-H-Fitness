package com.aegisfit.app.presentation.screen.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aegisfit.app.domain.model.SkincareLog
import com.aegisfit.app.domain.model.UserProfile
import com.aegisfit.app.domain.repository.AuthRepository
import com.aegisfit.app.domain.repository.HydrationRepository
import com.aegisfit.app.domain.repository.NutritionRepository
import com.aegisfit.app.domain.repository.SkincareRepository
import com.aegisfit.app.domain.repository.UserRepository
import com.aegisfit.app.domain.repository.WorkoutRepository
import com.aegisfit.app.domain.usecase.biometrics.CalculateBmiUseCase
import com.aegisfit.app.domain.usecase.biometrics.CalculateTdeeUseCase
import com.aegisfit.app.domain.usecase.biometrics.CalculateCalorieTargetUseCase
import com.aegisfit.app.domain.usecase.workout.WorkoutMetrics
import com.aegisfit.app.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.firstOrNull
import com.aegisfit.app.domain.model.WeightLog
import java.util.Calendar
import javax.inject.Inject

data class DailyStats(
    val caloriesConsumed: Double = 0.0,
    val calorieTarget: Int = 0,
    val proteinG: Double = 0.0,
    val carbsG: Double = 0.0,
    val fatG: Double = 0.0,
    val waterMl: Long = 0,
    val waterGoalMl: Int = 3500,
    val completedSets: Int = 0,
    val cardioCaloriesBurned: Double = 0.0,
    val weightliftingCaloriesBurned: Double = 0.0,
    val skincareAmDone: Boolean = false,
    val skincarePmDone: Boolean = false,
    val todayWeightKg: Double? = null,
    val hasLoggedWeight: Boolean = false,
    val recoveryScore: Int = 0,
    val hasRecoveryEstimate: Boolean = false
)

data class WeeklyStats(
    val workoutDays: List<Long> = emptyList(),
    val totalCalories: Double = 0.0,
    val daysElapsed: Int = 1
)

data class MonthlyStats(
    val workoutDays: List<Long> = emptyList(),
    val totalCalories: Double = 0.0,
    val daysElapsed: Int = 1
)

data class DashboardState(
    val userProfile: UserProfile? = null,
    val hasProfile: Boolean? = null,
    val bmi: Double = 0.0,
    val bmiCategory: String = "",
    val tdee: Double = 0.0,
    val greeting: String = "",
    val selectedTab: Int = 0,
    val daily: DailyStats = DailyStats(),
    val weekly: WeeklyStats = WeeklyStats(),
    val monthly: MonthlyStats = MonthlyStats(),
    val recentWeights: List<WeightLog> = emptyList(),
    val showWeightDialog: Boolean = false,
    val selectedMonthDay: Long? = null,
    val showMonthDayDialog: Boolean = false,
    val selectedMonthDayStats: SelectedDayStats? = null
)

data class SelectedDayStats(
    val calories: Double,
    val weightKg: Double?,
    val waterMl: Long,
    val completedSets: Int,
    val cardioCaloriesBurned: Double,
    val skincareAmDone: Boolean,
    val skincarePmDone: Boolean
)

private data class NutritionData(val calories: Double, val protein: Double, val carbs: Double, val fat: Double)
private data class StatusData(val waterMl: Long, val completedSets: Int, val skinLogs: List<SkincareLog>, val cardioCals: Double)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val nutritionRepository: NutritionRepository,
    private val hydrationRepository: HydrationRepository,
    private val workoutRepository: WorkoutRepository,
    private val skincareRepository: SkincareRepository,
    private val calculateBmiUseCase: CalculateBmiUseCase,
    private val calculateTdeeUseCase: CalculateTdeeUseCase,
    private val calculateCalorieTargetUseCase: CalculateCalorieTargetUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state.asStateFlow()

    private val userId: String
        get() = authRepository.currentUserId ?: ""

    init {
        viewModelScope.launch {
            val today = DateUtils.todayStartMillis()
            val weekStart = DateUtils.weekStartMillis()
            val monthStart = DateUtils.monthStartMillis()
            
            // Collect user profile
            launch {
                userRepository.getUserProfile(userId).collect { profile ->
                    if (profile != null) {
                        val bmi = calculateBmiUseCase(profile.weightKg, profile.heightCm)
                        val category = CalculateBmiUseCase.getCategory(bmi)
                        val tdee = calculateTdeeUseCase(
                            profile.weightKg,
                            profile.heightCm,
                            profile.age,
                            profile.gender,
                            profile.activityLevel
                        )
                        val greeting = getGreeting(profile.name)
                        
                        val dynamicCalorieTarget = calculateCalorieTargetUseCase(
                            currentWeightKg = profile.weightKg,
                            goalWeightKg = profile.goalWeightKg,
                            heightCm = profile.heightCm,
                            age = profile.age,
                            gender = profile.gender,
                            activityLevel = profile.activityLevel
                        )
                        
                        _state.update { 
                            it.copy(
                                userProfile = profile,
                                hasProfile = true,
                                bmi = bmi,
                                bmiCategory = category,
                                tdee = tdee,
                                greeting = greeting,
                                daily = it.daily.copy(calorieTarget = dynamicCalorieTarget)
                            )
                        }
                    } else {
                        _state.update { it.copy(hasProfile = false) }
                    }
                }
            }
            
            // Daily Stats
            launch {
                val nutritionFlows = combine(
                    nutritionRepository.getTotalCaloriesForDate(userId, today),
                    nutritionRepository.getTotalProteinForDate(userId, today),
                    nutritionRepository.getTotalCarbsForDate(userId, today),
                    nutritionRepository.getTotalFatForDate(userId, today)
                ) { cal, pro, carb, fat ->
                    NutritionData(cal ?: 0.0, pro ?: 0.0, carb ?: 0.0, fat ?: 0.0)
                }

                val statusFlows = combine(
                    hydrationRepository.getTotalForDate(userId, today),
                    workoutRepository.getCompletedSetsCountForDate(userId, today),
                    skincareRepository.getAllLogsForDate(userId, today),
                    workoutRepository.getCardioCaloriesForDate(userId, today)
                ) { water, sets, skinLogs, cardioCals ->
                    StatusData(water ?: 0L, sets, skinLogs, cardioCals)
                }

                combine(
                    nutritionFlows,
                    statusFlows,
                    userRepository.getUserProfile(userId)
                ) { nutrition, status, profile ->
                    val amDone = status.skinLogs.filter { it.routineType == "AM" }.let { logs -> logs.isNotEmpty() && logs.all { it.completed } }
                    val pmDone = status.skinLogs.filter { it.routineType == "PM" }.let { logs -> logs.isNotEmpty() && logs.all { it.completed } }
                    val calorieTarget = profile?.let {
                        calculateCalorieTargetUseCase(
                            currentWeightKg = it.weightKg,
                            goalWeightKg = it.goalWeightKg,
                            heightCm = it.heightCm,
                            age = it.age,
                            gender = it.gender,
                            activityLevel = it.activityLevel
                        )
                    } ?: _state.value.daily.calorieTarget
                    val weightliftingCals = WorkoutMetrics.estimateStrengthCalories(
                        status.completedSets,
                        profile?.weightKg ?: 70.0
                    )
                    val recovery = WorkoutMetrics.recoveryEstimate(
                        waterMl = status.waterMl,
                        waterGoalMl = _state.value.daily.waterGoalMl,
                        caloriesConsumed = nutrition.calories,
                        calorieTarget = calorieTarget,
                        proteinG = nutrition.protein,
                        completedSets = status.completedSets,
                        cardioCalories = status.cardioCals
                    )

                    _state.value.daily.copy(
                        caloriesConsumed = nutrition.calories,
                        calorieTarget = calorieTarget,
                        proteinG = nutrition.protein,
                        carbsG = nutrition.carbs,
                        fatG = nutrition.fat,
                        waterMl = status.waterMl,
                        completedSets = status.completedSets,
                        cardioCaloriesBurned = status.cardioCals,
                        weightliftingCaloriesBurned = weightliftingCals,
                        skincareAmDone = amDone,
                        skincarePmDone = pmDone,
                        recoveryScore = recovery.score,
                        hasRecoveryEstimate = recovery.hasEnoughData
                    )
                }.collect { dailyStats ->
                    _state.update { it.copy(daily = dailyStats) }
                }
            }
            
            // Weekly Stats
            launch {
                val endDate = DateUtils.endOfDay(today)
                val daysElapsed = DateUtils.daysInRange(weekStart, today).size
                combine(
                    workoutRepository.getWorkoutDatesInRange(userId, weekStart, endDate),
                    nutritionRepository.getTotalCaloriesInRange(userId, weekStart, endDate)
                ) { workoutDates, totalCal ->
                    WeeklyStats(
                        workoutDays = workoutDates,
                        totalCalories = totalCal ?: 0.0,
                        daysElapsed = daysElapsed
                    )
                }.collect { weeklyStats ->
                    _state.update { it.copy(weekly = weeklyStats) }
                }
            }
            
            // Monthly Stats
            launch {
                val endDate = DateUtils.endOfDay(today)
                val daysElapsed = DateUtils.dayOfMonth(today)
                combine(
                    workoutRepository.getWorkoutDatesInRange(userId, monthStart, endDate),
                    nutritionRepository.getTotalCaloriesInRange(userId, monthStart, endDate)
                ) { workoutDates, totalCal ->
                    MonthlyStats(
                        workoutDays = workoutDates,
                        totalCalories = totalCal ?: 0.0,
                        daysElapsed = daysElapsed
                    )
                }.collect { monthlyStats ->
                    _state.update { it.copy(monthly = monthlyStats) }
                }
            }

            // Weight Tracking
            launch {
                userRepository.getWeightLogForDate(userId, today).collect { weightLog ->
                    _state.update {
                        it.copy(
                            daily = it.daily.copy(
                                todayWeightKg = weightLog?.weightKg,
                                hasLoggedWeight = weightLog != null
                            )
                        )
                    }
                }
            }

            // Recent weight history
            launch {
                userRepository.getRecentWeightLogs(userId, 30).collect { logs ->
                    _state.update { it.copy(recentWeights = logs) }
                }
            }
        }
    }

    fun selectTab(index: Int) {
        _state.update { it.copy(selectedTab = index) }
    }

    fun showWeightDialog() {
        _state.update { it.copy(showWeightDialog = true) }
    }

    fun dismissWeightDialog() {
        _state.update { it.copy(showWeightDialog = false) }
    }

    fun saveWeight(weightKg: Double) {
        if (!weightKg.isFinite() || weightKg !in 30.0..350.0 || userId.isBlank()) return
        val today = DateUtils.todayStartMillis()
        viewModelScope.launch {
            userRepository.saveWeightLog(WeightLog(userId = userId, date = today, weightKg = weightKg))
            _state.update {
                it.copy(
                    showWeightDialog = false,
                    daily = it.daily.copy(todayWeightKg = weightKg, hasLoggedWeight = true)
                )
            }
            // Also update user profile weight and dynamic target
            val profile = userRepository.getUserProfileOnce(userId)
            if (profile != null) {
                val newTarget = calculateCalorieTargetUseCase(
                    currentWeightKg = weightKg,
                    goalWeightKg = profile.goalWeightKg,
                    heightCm = profile.heightCm,
                    age = profile.age,
                    gender = profile.gender,
                    activityLevel = profile.activityLevel
                )
                userRepository.saveUserProfile(profile.copy(weightKg = weightKg, dailyCalorieTarget = newTarget))
            }
        }
    }

    fun selectMonthDay(dayMillis: Long) {
        _state.update { it.copy(selectedMonthDay = dayMillis, showMonthDayDialog = true, selectedMonthDayStats = null) }
        viewModelScope.launch {
            val calories = nutritionRepository.getTotalCaloriesForDate(userId, dayMillis).firstOrNull() ?: 0.0
            val weightLog = userRepository.getWeightLogForDate(userId, dayMillis).firstOrNull()
            val water = hydrationRepository.getTotalForDate(userId, dayMillis).firstOrNull() ?: 0L
            val sets = workoutRepository.getCompletedSetsCountForDate(userId, dayMillis).firstOrNull() ?: 0
            val cardioCals = workoutRepository.getCardioCaloriesForDate(userId, dayMillis).firstOrNull() ?: 0.0
            val skinLogs = skincareRepository.getAllLogsForDate(userId, dayMillis).firstOrNull() ?: emptyList()
            val amDone = skinLogs.filter { it.routineType == "AM" }.let { logs -> logs.isNotEmpty() && logs.all { it.completed } }
            val pmDone = skinLogs.filter { it.routineType == "PM" }.let { logs -> logs.isNotEmpty() && logs.all { it.completed } }

            _state.update { 
                if (it.selectedMonthDay == dayMillis) {
                    it.copy(
                        selectedMonthDayStats = SelectedDayStats(
                            calories = calories,
                            weightKg = weightLog?.weightKg,
                            waterMl = water,
                            completedSets = sets,
                            cardioCaloriesBurned = cardioCals,
                            skincareAmDone = amDone,
                            skincarePmDone = pmDone
                        )
                    )
                } else it
            }
        }
    }

    fun dismissMonthDayDialog() {
        _state.update { it.copy(showMonthDayDialog = false) }
    }

    private fun getGreeting(name: String): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val prefix = when {
            hour < 12 -> "Good Morning"
            hour < 17 -> "Good Afternoon"
            else -> "Good Evening"
        }
        return if (name.isNotBlank()) "$prefix, $name" else prefix
    }
}
