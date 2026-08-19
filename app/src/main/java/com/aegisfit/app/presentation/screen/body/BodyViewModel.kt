package com.aegisfit.app.presentation.screen.body

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aegisfit.app.domain.model.AsymmetryResult
import com.aegisfit.app.domain.model.BodyMeasurement
import com.aegisfit.app.domain.model.UserProfile
import com.aegisfit.app.domain.repository.AuthRepository
import com.aegisfit.app.domain.repository.BodyMeasurementRepository
import com.aegisfit.app.domain.repository.UserRepository
import com.aegisfit.app.domain.usecase.biometrics.CalculateBmiUseCase
import com.aegisfit.app.domain.usecase.biometrics.CalculateBmrUseCase
import com.aegisfit.app.domain.usecase.biometrics.CalculateTdeeUseCase
import com.aegisfit.app.domain.usecase.biometrics.DetectAsymmetryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BodyState(
    val userProfile: UserProfile? = null,
    val latestMeasurement: BodyMeasurement? = null,
    val allMeasurements: List<BodyMeasurement> = emptyList(),
    val bmi: Double = 0.0,
    val bmiCategory: String = "",
    val bmr: Double = 0.0,
    val tdee: Double = 0.0,
    val asymmetries: List<AsymmetryResult> = emptyList(),
    val isLoading: Boolean = true,
    val weightHistory: List<Pair<Long, Double>> = emptyList()
)

@HiltViewModel
class BodyViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val bodyMeasurementRepository: BodyMeasurementRepository,
    private val calculateBmiUseCase: CalculateBmiUseCase,
    private val calculateBmrUseCase: CalculateBmrUseCase,
    private val calculateTdeeUseCase: CalculateTdeeUseCase,
    private val detectAsymmetryUseCase: DetectAsymmetryUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(BodyState())
    val state: StateFlow<BodyState> = _state.asStateFlow()

    private val userId: String
        get() = authRepository.currentUserId ?: ""

    init {
        viewModelScope.launch {
            combine(
                userRepository.getUserProfile(userId),
                bodyMeasurementRepository.getAllMeasurements(userId),
                bodyMeasurementRepository.getLatestMeasurement(userId)
            ) { profile, allMeasurements, latestMeasurement ->
                var bmi = 0.0
                var bmiCategory = ""
                var bmr = 0.0
                var tdee = 0.0
                val weightHistory = mutableListOf<Pair<Long, Double>>()

                if (profile != null) {
                    bmi = calculateBmiUseCase(profile.weightKg, profile.heightCm)
                    bmiCategory = CalculateBmiUseCase.getCategory(bmi)
                    bmr = calculateBmrUseCase(profile.weightKg, profile.heightCm, profile.age, profile.gender)
                    tdee = calculateTdeeUseCase(profile.weightKg, profile.heightCm, profile.age, profile.gender, profile.activityLevel)
                    
                    weightHistory.add(Pair(profile.updatedAt, profile.weightKg))
                }

                val asymmetries = latestMeasurement?.let {
                    detectAsymmetryUseCase(it)
                } ?: emptyList()

                BodyState(
                    userProfile = profile,
                    latestMeasurement = latestMeasurement,
                    allMeasurements = allMeasurements,
                    bmi = bmi,
                    bmiCategory = bmiCategory,
                    bmr = bmr,
                    tdee = tdee,
                    asymmetries = asymmetries,
                    isLoading = false,
                    weightHistory = weightHistory
                )
            }.collect { newState ->
                _state.update { newState }
            }
        }
    }
}
