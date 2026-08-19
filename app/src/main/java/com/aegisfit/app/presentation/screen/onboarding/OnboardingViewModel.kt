package com.aegisfit.app.presentation.screen.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aegisfit.app.domain.model.ActivityLevel
import com.aegisfit.app.domain.model.BodyMeasurement
import com.aegisfit.app.domain.model.Gender
import com.aegisfit.app.domain.model.UnitSystem
import com.aegisfit.app.domain.model.UserProfile
import com.aegisfit.app.domain.repository.AuthRepository
import com.aegisfit.app.domain.repository.BodyMeasurementRepository
import com.aegisfit.app.domain.repository.UserRepository
import com.aegisfit.app.domain.usecase.biometrics.CalculateBmiUseCase
import com.aegisfit.app.domain.usecase.biometrics.CalculateBmrUseCase
import com.aegisfit.app.domain.usecase.biometrics.CalculateTdeeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OnboardingState(
    val currentStep: Int = 0,
    val totalSteps: Int = 4,
    // Step 1: Welcome
    val name: String = "",
    val age: String = "25",
    val gender: Gender = Gender.Male,
    // Step 2: Body Basics
    val weightKg: String = "86.0",
    val goalWeightKg: String = "76.0",
    val heightCm: String = "175.0",
    val bodyFatPercent: String = "",
    val unitSystem: UnitSystem = UnitSystem.Metric,
    // Step 3: Measurements (optional)
    val chestCm: String = "",
    val waistCm: String = "",
    val hipsCm: String = "",
    val neckCm: String = "",
    val leftBicepCm: String = "",
    val rightBicepCm: String = "",
    val leftForearmCm: String = "",
    val rightForearmCm: String = "",
    val leftQuadCm: String = "",
    val rightQuadCm: String = "",
    val leftCalfCm: String = "",
    val rightCalfCm: String = "",
    // Step 4: Activity & Results
    val activityLevel: ActivityLevel = ActivityLevel.Moderate,
    val calculatedBmi: Double = 0.0,
    val bmiCategory: String = "",
    val calculatedBmr: Double = 0.0,
    val calculatedTdee: Double = 0.0,
    val dailyCalorieTarget: String = "1700",
    // Navigation
    val isComplete: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    private val bodyMeasurementRepository: BodyMeasurementRepository,
    private val calculateBmiUseCase: CalculateBmiUseCase,
    private val calculateBmrUseCase: CalculateBmrUseCase,
    private val calculateTdeeUseCase: CalculateTdeeUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(OnboardingState())
    val state: StateFlow<OnboardingState> = _state.asStateFlow()

    fun updateName(name: String) = _state.update {
        it.copy(name = name.take(80), errorMessage = null)
    }
    
    fun updateAge(age: String) {
        if (age.isEmpty() || age.toIntOrNull() != null) {
            _state.update { it.copy(age = age.take(3), errorMessage = null) }
        }
    }
    
    fun updateGender(gender: Gender) = _state.update { it.copy(gender = gender) }
    
    fun updateWeight(weight: String) = updateDecimal(weight) { state, value -> state.copy(weightKg = value) }
    
    fun updateGoalWeight(weight: String) = updateDecimal(weight) { state, value -> state.copy(goalWeightKg = value) }
    
    fun updateHeight(height: String) = updateDecimal(height) { state, value -> state.copy(heightCm = value) }
    
    fun updateBodyFatPercent(percent: String) = updateDecimal(percent) { state, value -> state.copy(bodyFatPercent = value) }
    
    fun updateMeasurement(field: String, value: String) {
        if (!isDecimalInput(value)) return
        _state.update {
            when (field) {
                "chest" -> it.copy(chestCm = value)
                "waist" -> it.copy(waistCm = value)
                "hips" -> it.copy(hipsCm = value)
                "neck" -> it.copy(neckCm = value)
                "leftBicep" -> it.copy(leftBicepCm = value)
                "rightBicep" -> it.copy(rightBicepCm = value)
                "leftForearm" -> it.copy(leftForearmCm = value)
                "rightForearm" -> it.copy(rightForearmCm = value)
                "leftQuad" -> it.copy(leftQuadCm = value)
                "rightQuad" -> it.copy(rightQuadCm = value)
                "leftCalf" -> it.copy(leftCalfCm = value)
                "rightCalf" -> it.copy(rightCalfCm = value)
                else -> it
            }.copy(errorMessage = null)
        }
    }
    
    fun updateActivityLevel(level: ActivityLevel) {
        _state.update { it.copy(activityLevel = level) }
        recalculate()
    }
    
    fun updateDailyCalorieTarget(target: String) {
        if (target.length <= 4 && (target.isEmpty() || target.all(Char::isDigit))) {
            _state.update { it.copy(dailyCalorieTarget = target, errorMessage = null) }
        }
    }
    
    fun nextStep() {
        val currentState = _state.value
        validateStep(currentState.currentStep, currentState)?.let { message ->
            _state.update { it.copy(errorMessage = message) }
            return
        }
        if (currentState.currentStep < currentState.totalSteps - 1) {
            val nextStepIndex = currentState.currentStep + 1
            _state.update { it.copy(currentStep = nextStepIndex, errorMessage = null) }
            if (nextStepIndex == 3) {
                recalculate()
            }
        }
    }
    
    fun previousStep() {
        if (_state.value.currentStep > 0) {
            _state.update { it.copy(currentStep = it.currentStep - 1, errorMessage = null) }
        }
    }

    fun skipMeasurements() {
        _state.update {
            it.copy(
                chestCm = "", waistCm = "", hipsCm = "", neckCm = "",
                leftBicepCm = "", rightBicepCm = "", leftForearmCm = "", rightForearmCm = "",
                leftQuadCm = "", rightQuadCm = "", leftCalfCm = "", rightCalfCm = "",
                currentStep = 3,
                errorMessage = null
            )
        }
        recalculate()
    }
    
    private fun recalculate() {
        val s = _state.value
        val weight = s.weightKg.toDoubleOrNull() ?: 0.0
        val height = s.heightCm.toDoubleOrNull() ?: 0.0
        val age = s.age.toIntOrNull() ?: 0
        
        val bmi = if (weight > 0 && height > 0) calculateBmiUseCase(weight, height) else 0.0
        val category = if (bmi > 0) CalculateBmiUseCase.getCategory(bmi) else ""
        
        val bmr = if (weight > 0 && height > 0 && age > 0) calculateBmrUseCase(weight, height, age, s.gender) else 0.0
        val tdee = if (bmr > 0) calculateTdeeUseCase(weight, height, age, s.gender, s.activityLevel) else 0.0
        
        _state.update {
            it.copy(
                calculatedBmi = bmi,
                bmiCategory = category,
                calculatedBmr = bmr,
                calculatedTdee = tdee,
                dailyCalorieTarget = if (tdee > 0) {
                    tdee.toInt().coerceIn(1_000, 6_000).toString()
                } else {
                    it.dailyCalorieTarget
                }
            )
        }
    }
    
    fun completeOnboarding() {
        val validationError = (0..3).firstNotNullOfOrNull { validateStep(it, _state.value) }
        if (validationError != null) {
            _state.update { it.copy(errorMessage = validationError) }
            return
        }
        val userId = authRepository.currentUserId
        if (userId.isNullOrBlank()) {
            _state.update { it.copy(errorMessage = "Your session expired. Please sign in again.") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, errorMessage = null) }
            val s = _state.value
            val now = System.currentTimeMillis()
            val userProfile = UserProfile(
                userId = userId,
                name = s.name.ifBlank { "User" },
                age = s.age.toIntOrNull() ?: 25,
                gender = s.gender,
                weightKg = s.weightKg.toDoubleOrNull() ?: 0.0,
                goalWeightKg = s.goalWeightKg.toDoubleOrNull() ?: 0.0,
                heightCm = s.heightCm.toDoubleOrNull() ?: 0.0,
                bodyFatPercent = s.bodyFatPercent.toDoubleOrNull(),
                activityLevel = s.activityLevel,
                dailyCalorieTarget = s.dailyCalorieTarget.toIntOrNull() ?: s.calculatedTdee.toInt(),
                unitSystem = s.unitSystem,
                createdAt = now,
                updatedAt = now
            )
            
            val hasMeasurements = listOf(
                s.chestCm, s.waistCm, s.hipsCm, s.neckCm,
                s.leftBicepCm, s.rightBicepCm, s.leftForearmCm, s.rightForearmCm,
                s.leftQuadCm, s.rightQuadCm, s.leftCalfCm, s.rightCalfCm
            ).any { it.isNotBlank() }
            
            runCatching {
                userRepository.saveUserProfile(userProfile)
                if (hasMeasurements) {
                    val measurement = BodyMeasurement(
                        userId = userId,
                        date = now,
                        chestCm = s.chestCm.toDoubleOrNull(),
                        waistCm = s.waistCm.toDoubleOrNull(),
                        hipsCm = s.hipsCm.toDoubleOrNull(),
                        neckCm = s.neckCm.toDoubleOrNull(),
                        leftBicepCm = s.leftBicepCm.toDoubleOrNull(),
                        rightBicepCm = s.rightBicepCm.toDoubleOrNull(),
                        leftForearmCm = s.leftForearmCm.toDoubleOrNull(),
                        rightForearmCm = s.rightForearmCm.toDoubleOrNull(),
                        leftQuadCm = s.leftQuadCm.toDoubleOrNull(),
                        rightQuadCm = s.rightQuadCm.toDoubleOrNull(),
                        leftCalfCm = s.leftCalfCm.toDoubleOrNull(),
                        rightCalfCm = s.rightCalfCm.toDoubleOrNull()
                    )
                    bodyMeasurementRepository.saveMeasurement(measurement)
                }
            }.onSuccess {
                _state.update { it.copy(isSaving = false, isComplete = true) }
            }.onFailure { error ->
                _state.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = error.message ?: "Your profile could not be saved."
                    )
                }
            }
        }
    }

    private fun updateDecimal(
        value: String,
        transform: (OnboardingState, String) -> OnboardingState
    ) {
        if (!isDecimalInput(value)) return
        _state.update { transform(it, value).copy(errorMessage = null) }
    }

    private fun isDecimalInput(value: String): Boolean =
        value.length <= 7 && value.count { it == '.' } <= 1 &&
            value.all { it.isDigit() || it == '.' }

    private fun validateStep(step: Int, state: OnboardingState): String? = when (step) {
        0 -> when {
            state.name.trim().length !in 2..80 -> "Enter a name between 2 and 80 characters."
            state.age.toIntOrNull()?.let { it in 13..100 } != true -> "Age must be between 13 and 100."
            else -> null
        }
        1 -> when {
            state.weightKg.toDoubleOrNull()?.let { it.isFinite() && it in 30.0..350.0 } != true ->
                "Current weight must be between 30 and 350 kg."
            state.goalWeightKg.toDoubleOrNull()?.let { it.isFinite() && it in 30.0..350.0 } != true ->
                "Goal weight must be between 30 and 350 kg."
            state.heightCm.toDoubleOrNull()?.let { it.isFinite() && it in 100.0..250.0 } != true ->
                "Height must be between 100 and 250 cm."
            state.bodyFatPercent.isNotBlank() &&
                state.bodyFatPercent.toDoubleOrNull()?.let { it.isFinite() && it in 2.0..75.0 } != true ->
                "Body fat must be between 2% and 75%."
            else -> null
        }
        2 -> {
            val measurements = listOf(
                state.chestCm, state.waistCm, state.hipsCm, state.neckCm,
                state.leftBicepCm, state.rightBicepCm, state.leftForearmCm,
                state.rightForearmCm, state.leftQuadCm, state.rightQuadCm,
                state.leftCalfCm, state.rightCalfCm
            )
            if (measurements.any { value ->
                    value.isNotBlank() &&
                        value.toDoubleOrNull()?.let { it.isFinite() && it in 5.0..300.0 } != true
                }
            ) {
                "Optional measurements must be between 5 and 300 cm."
            } else null
        }
        3 -> if (state.dailyCalorieTarget.toIntOrNull()?.let { it in 1_000..6_000 } != true) {
            "Daily calorie target must be between 1,000 and 6,000 kcal."
        } else null
        else -> null
    }
}
