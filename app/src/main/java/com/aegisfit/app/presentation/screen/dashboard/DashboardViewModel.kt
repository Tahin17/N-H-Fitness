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

import com.aegisfit.app.domain.repository.DailyCareRepository
import com.aegisfit.app.domain.model.DailyCareSummary

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
    val dailyCareCompleted: Int = 0,
    val dailyCareTotal: Int = 0,
    val todayWeightKg: Double? = null,
    val hasLoggedWeight: Boolean = false,
    val dayProgressScore: Int = 0,
    val completedGoalsCount: Int = 0,
    val totalGoalsCount: Int = 4,
    val recoveryScore: Int = 0,
    val hasRecoveryEstimate: Boolean = false
)

data class WeeklyStats(
    val workoutDays: List<Long> = emptyList(),
    val weightDays: List<Long> = emptyList(),
    val weightLogs: List<WeightLog> = emptyList(),
    val avgWeightKg: Double? = null,
    val latestWeightKg: Double? = null,
    val totalCalories: Double = 0.0,
    val daysElapsed: Int = 1
)

data class MonthlyStats(
    val workoutDays: List<Long> = emptyList(),
    val weightDays: List<Long> = emptyList(),
    val weightLogs: List<WeightLog> = emptyList(),
    val avgWeightKg: Double? = null,
    val startWeightKg: Double? = null,
    val latestWeightKg: Double? = null,
    val weightChangeKg: Double? = null,
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
    val dateMillis: Long = 0L,
    val formattedDate: String = "",
    val calories: Double = 0.0,
    val calorieTarget: Int = 2000,
    val proteinG: Double = 0.0,
    val carbsG: Double = 0.0,
    val fatG: Double = 0.0,
    val weightKg: Double? = null,
    val waterMl: Long = 0L,
    val waterGoalMl: Int = 3500,
    val completedSets: Int = 0,
    val cardioCaloriesBurned: Double = 0.0,
    val weightliftingCaloriesBurned: Double = 0.0,
    val skincareAmDone: Boolean = false,
    val skincarePmDone: Boolean = false,
    val dailyCareCompleted: Int = 0,
    val dailyCareTotal: Int = 0,
    val isLoading: Boolean = false
)

private data class NutritionData(val calories: Double, val protein: Double, val carbs: Double, val fat: Double)
private data class StatusData(val waterMl: Long, val completedSets: Int, val skinLogs: List<SkincareLog>, val cardioCals: Double, val careSummary: DailyCareSummary)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val nutritionRepository: NutritionRepository,
    private val hydrationRepository: HydrationRepository,
    private val workoutRepository: WorkoutRepository,
    private val skincareRepository: SkincareRepository,
    private val dailyCareRepository: DailyCareRepository,
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
                    workoutRepository.getCardioCaloriesForDate(userId, today),
                    dailyCareRepository.getDailyCareSummary(userId, today)
                ) { water, sets, skinLogs, cardioCals, careSummary ->
                    StatusData(water ?: 0L, sets, skinLogs, cardioCals, careSummary)
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
                            goalWeightKg = profile.goalWeightKg,
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

                    val hasLogged = _state.value.daily.hasLoggedWeight
                    val (progress, completed) = calculateDayProgress(
                        caloriesConsumed = nutrition.calories,
                        calorieTarget = calorieTarget,
                        waterMl = status.waterMl,
                        waterGoalMl = _state.value.daily.waterGoalMl,
                        completedSets = status.completedSets,
                        cardioCalories = status.cardioCals,
                        hasLoggedWeight = hasLogged
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
                        dailyCareCompleted = status.careSummary.completedCount,
                        dailyCareTotal = status.careSummary.totalCount,
                        dayProgressScore = progress,
                        completedGoalsCount = completed,
                        recoveryScore = recovery.score,
                        hasRecoveryEstimate = recovery.hasEnoughData
                    )
                }.collect { dailyStats ->
                    _state.update { it.copy(daily = dailyStats) }
                }
            }
            
            // Weekly Stats with Weight Logs
            launch {
                val endDate = DateUtils.endOfDay(today)
                val daysElapsed = DateUtils.daysInRange(weekStart, today).size
                combine(
                    workoutRepository.getWorkoutDatesInRange(userId, weekStart, endDate),
                    nutritionRepository.getTotalCaloriesInRange(userId, weekStart, endDate),
                    userRepository.getRecentWeightLogs(userId, 30)
                ) { workoutDates, totalCal, recentLogs ->
                    val weekLogs = recentLogs.filter { it.date >= weekStart && it.date <= endDate }.sortedBy { it.date }
                    val weightDays = weekLogs.map { DateUtils.startOfDay(it.date) }
                    val avgWeight = if (weekLogs.isNotEmpty()) weekLogs.map { it.weightKg }.average() else null
                    val latestWeight = weekLogs.lastOrNull()?.weightKg
                    WeeklyStats(
                        workoutDays = workoutDates,
                        weightDays = weightDays,
                        weightLogs = weekLogs,
                        avgWeightKg = avgWeight,
                        latestWeightKg = latestWeight,
                        totalCalories = totalCal ?: 0.0,
                        daysElapsed = daysElapsed
                    )
                }.collect { weeklyStats ->
                    _state.update { it.copy(weekly = weeklyStats) }
                }
            }
            
            // Monthly Stats with Weight Progress
            launch {
                val endDate = DateUtils.endOfDay(today)
                val daysElapsed = DateUtils.dayOfMonth(today)
                combine(
                    workoutRepository.getWorkoutDatesInRange(userId, monthStart, endDate),
                    nutritionRepository.getTotalCaloriesInRange(userId, monthStart, endDate),
                    userRepository.getRecentWeightLogs(userId, 60)
                ) { workoutDates, totalCal, recentLogs ->
                    val monthLogs = recentLogs.filter { it.date >= monthStart && it.date <= endDate }.sortedBy { it.date }
                    val weightDays = monthLogs.map { DateUtils.startOfDay(it.date) }
                    val avgWeight = if (monthLogs.isNotEmpty()) monthLogs.map { it.weightKg }.average() else null
                    val startWeight = monthLogs.firstOrNull()?.weightKg
                    val latestWeight = monthLogs.lastOrNull()?.weightKg
                    val weightChange = if (startWeight != null && latestWeight != null) latestWeight - startWeight else null
                    MonthlyStats(
                        workoutDays = workoutDates,
                        weightDays = weightDays,
                        weightLogs = monthLogs,
                        avgWeightKg = avgWeight,
                        startWeightKg = startWeight,
                        latestWeightKg = latestWeight,
                        weightChangeKg = weightChange,
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
                        val hasLogged = weightLog != null
                        val (progress, completed) = calculateDayProgress(
                            caloriesConsumed = it.daily.caloriesConsumed,
                            calorieTarget = it.daily.calorieTarget,
                            waterMl = it.daily.waterMl,
                            waterGoalMl = it.daily.waterGoalMl,
                            completedSets = it.daily.completedSets,
                            cardioCalories = it.daily.cardioCaloriesBurned,
                            hasLoggedWeight = hasLogged
                        )
                        it.copy(
                            daily = it.daily.copy(
                                todayWeightKg = weightLog?.weightKg,
                                hasLoggedWeight = hasLogged,
                                dayProgressScore = progress,
                                completedGoalsCount = completed
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

    private fun calculateDayProgress(
        caloriesConsumed: Double,
        calorieTarget: Int,
        waterMl: Long,
        waterGoalMl: Int,
        completedSets: Int,
        cardioCalories: Double,
        hasLoggedWeight: Boolean
    ): Pair<Int, Int> {
        val isCalorieMet = calorieTarget > 0 && caloriesConsumed >= (calorieTarget * 0.85)
        val isWaterMet = waterGoalMl > 0 && waterMl >= waterGoalMl
        val isWorkoutMet = completedSets > 0 || cardioCalories > 0
        val isWeightMet = hasLoggedWeight

        var completedGoals = 0
        if (isCalorieMet) completedGoals++
        if (isWaterMet) completedGoals++
        if (isWorkoutMet) completedGoals++
        if (isWeightMet) completedGoals++

        val calProgress = if (calorieTarget > 0) (caloriesConsumed / calorieTarget).coerceIn(0.0, 1.0) else 0.0
        val waterProgress = if (waterGoalMl > 0) (waterMl.toDouble() / waterGoalMl).coerceIn(0.0, 1.0) else 0.0
        val workoutProgress = if (isWorkoutMet) 1.0 else 0.0
        val weightProgress = if (isWeightMet) 1.0 else 0.0

        val progressPercent = ((calProgress + waterProgress + workoutProgress + weightProgress) / 4.0 * 100.0).toInt().coerceIn(0, 100)
        return Pair(progressPercent, completedGoals)
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
        saveWeightForDate(today, weightKg)
        _state.update { it.copy(showWeightDialog = false) }
    }

    fun saveWeightForDate(dayMillis: Long, weightKg: Double) {
        if (!weightKg.isFinite() || weightKg !in 30.0..350.0 || userId.isBlank()) return
        val today = DateUtils.todayStartMillis()
        val normalizedDate = DateUtils.startOfDay(dayMillis)
        viewModelScope.launch {
            userRepository.saveWeightLog(WeightLog(userId = userId, date = normalizedDate, weightKg = weightKg))
            if (normalizedDate == today) {
                _state.update {
                    val (progress, completed) = calculateDayProgress(
                        caloriesConsumed = it.daily.caloriesConsumed,
                        calorieTarget = it.daily.calorieTarget,
                        waterMl = it.daily.waterMl,
                        waterGoalMl = it.daily.waterGoalMl,
                        completedSets = it.daily.completedSets,
                        cardioCalories = it.daily.cardioCaloriesBurned,
                        hasLoggedWeight = true
                    )
                    it.copy(
                        daily = it.daily.copy(
                            todayWeightKg = weightKg,
                            hasLoggedWeight = true,
                            dayProgressScore = progress,
                            completedGoalsCount = completed
                        )
                    )
                }
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
            // Refresh day summary if open
            if (_state.value.selectedMonthDay == normalizedDate) {
                selectMonthDay(normalizedDate)
            }
        }
    }

    fun selectMonthDay(dayMillis: Long) {
        val normalizedDay = DateUtils.startOfDay(dayMillis)
        val dateFormatted = DateUtils.formatFullDate(normalizedDay)
        val target = _state.value.daily.calorieTarget.takeIf { it > 0 } ?: 2000
        val waterGoal = _state.value.daily.waterGoalMl

        _state.update {
            it.copy(
                selectedMonthDay = normalizedDay,
                showMonthDayDialog = true,
                selectedMonthDayStats = SelectedDayStats(
                    dateMillis = normalizedDay,
                    formattedDate = dateFormatted,
                    calorieTarget = target,
                    waterGoalMl = waterGoal,
                    isLoading = true
                )
            )
        }

        viewModelScope.launch {
            val calories = kotlinx.coroutines.withTimeoutOrNull(2500) {
                nutritionRepository.getTotalCaloriesForDate(userId, normalizedDay).firstOrNull()
            } ?: 0.0
            val protein = kotlinx.coroutines.withTimeoutOrNull(2500) {
                nutritionRepository.getTotalProteinForDate(userId, normalizedDay).firstOrNull()
            } ?: 0.0
            val carbs = kotlinx.coroutines.withTimeoutOrNull(2500) {
                nutritionRepository.getTotalCarbsForDate(userId, normalizedDay).firstOrNull()
            } ?: 0.0
            val fat = kotlinx.coroutines.withTimeoutOrNull(2500) {
                nutritionRepository.getTotalFatForDate(userId, normalizedDay).firstOrNull()
            } ?: 0.0
            val weightLog = kotlinx.coroutines.withTimeoutOrNull(2500) {
                userRepository.getWeightLogForDate(userId, normalizedDay).firstOrNull()
            }
            val water = kotlinx.coroutines.withTimeoutOrNull(2500) {
                hydrationRepository.getTotalForDate(userId, normalizedDay).firstOrNull()
            } ?: 0L
            val sets = kotlinx.coroutines.withTimeoutOrNull(2500) {
                workoutRepository.getCompletedSetsCountForDate(userId, normalizedDay).firstOrNull()
            } ?: 0
            val cardioCals = kotlinx.coroutines.withTimeoutOrNull(2500) {
                workoutRepository.getCardioCaloriesForDate(userId, normalizedDay).firstOrNull()
            } ?: 0.0
            val skinLogs = kotlinx.coroutines.withTimeoutOrNull(2500) {
                skincareRepository.getAllLogsForDate(userId, normalizedDay).firstOrNull()
            } ?: emptyList()
            val careSummary = kotlinx.coroutines.withTimeoutOrNull(2500) {
                dailyCareRepository.getDailyCareSummary(userId, normalizedDay).firstOrNull()
            } ?: DailyCareSummary()

            val amDone = skinLogs.filter { it.routineType == "AM" }.let { logs -> logs.isNotEmpty() && logs.all { it.completed } }
            val pmDone = skinLogs.filter { it.routineType == "PM" }.let { logs -> logs.isNotEmpty() && logs.all { it.completed } }
            val profile = _state.value.userProfile
            val weightliftingCals = WorkoutMetrics.estimateStrengthCalories(sets, profile?.weightKg ?: 70.0)

            val updatedStats = SelectedDayStats(
                dateMillis = normalizedDay,
                formattedDate = dateFormatted,
                calories = calories,
                calorieTarget = target,
                proteinG = protein,
                carbsG = carbs,
                fatG = fat,
                weightKg = weightLog?.weightKg,
                waterMl = water,
                waterGoalMl = waterGoal,
                completedSets = sets,
                cardioCaloriesBurned = cardioCals,
                weightliftingCaloriesBurned = weightliftingCals,
                skincareAmDone = amDone,
                skincarePmDone = pmDone,
                dailyCareCompleted = careSummary.completedCount,
                dailyCareTotal = careSummary.totalCount,
                isLoading = false
            )

            _state.update {
                if (it.selectedMonthDay == normalizedDay) {
                    it.copy(selectedMonthDayStats = updatedStats)
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
