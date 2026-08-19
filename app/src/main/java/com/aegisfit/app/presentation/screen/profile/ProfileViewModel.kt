package com.aegisfit.app.presentation.screen.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aegisfit.app.domain.model.ActivityLevel
import com.aegisfit.app.domain.model.UserProfile
import com.aegisfit.app.domain.repository.AuthRepository
import com.aegisfit.app.domain.repository.UserRepository
import com.aegisfit.app.domain.usecase.biometrics.CalculateCalorieTargetUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileState(
    val userProfile: UserProfile? = null,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val targetWeightInput: String = "",
    val nameInput: String = "",
    val ageInput: String = "",
    val showSuccessMessage: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val calculateCalorieTargetUseCase: CalculateCalorieTargetUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val userId = authRepository.currentUserId
            if (userId.isNullOrBlank()) {
                _state.update { it.copy(isLoading = false, errorMessage = "Your session expired. Please sign in again.") }
                return@launch
            }
            val profile = userRepository.getUserProfileOnce(userId)
            _state.update { 
                it.copy(
                    userProfile = profile,
                    isLoading = false,
                    targetWeightInput = profile?.goalWeightKg?.toString() ?: "",
                    nameInput = profile?.name ?: "",
                    ageInput = profile?.age?.toString() ?: ""
                )
            }
        }
    }

    fun onNameChange(name: String) = _state.update {
        it.copy(nameInput = name.take(80), errorMessage = null)
    }
    fun onAgeChange(age: String) {
        if (age.length <= 3 && (age.isEmpty() || age.all(Char::isDigit))) {
            _state.update { it.copy(ageInput = age, errorMessage = null) }
        }
    }
    fun onTargetWeightChange(weight: String) {
        if (weight.length <= 7 && weight.count { it == '.' } <= 1 &&
            weight.all { it.isDigit() || it == '.' }
        ) {
            _state.update { it.copy(targetWeightInput = weight, errorMessage = null) }
        }
    }

    fun updateActivityLevel(level: ActivityLevel) {
        _state.update { it.copy(userProfile = it.userProfile?.copy(activityLevel = level)) }
    }

    fun saveProfile() {
        val currentProfile = _state.value.userProfile ?: return
        val newGoalWeight = _state.value.targetWeightInput.toDoubleOrNull()
        val newName = _state.value.nameInput.trim()
        val newAge = _state.value.ageInput.toIntOrNull()
        val userId = authRepository.currentUserId
        val validationMessage = when {
            userId.isNullOrBlank() -> "Your session expired. Please sign in again."
            newName.length !in 2..80 -> "Name must be between 2 and 80 characters."
            newAge == null || newAge !in 13..100 -> "Age must be between 13 and 100."
            newGoalWeight == null || !newGoalWeight.isFinite() || newGoalWeight !in 30.0..350.0 ->
                "Target weight must be between 30 and 350 kg."
            else -> null
        }
        if (validationMessage != null) {
            _state.update { it.copy(errorMessage = validationMessage) }
            return
        }
        val validUserId = requireNotNull(userId)
        val validAge = requireNotNull(newAge)
        val validGoalWeight = requireNotNull(newGoalWeight)

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, errorMessage = null) }
            
            // Recalculate calorie target dynamically
            val newTarget = calculateCalorieTargetUseCase(
                currentWeightKg = currentProfile.weightKg,
                goalWeightKg = validGoalWeight,
                heightCm = currentProfile.heightCm,
                age = validAge,
                gender = currentProfile.gender,
                activityLevel = currentProfile.activityLevel
            )

            val updatedProfile = currentProfile.copy(
                userId = validUserId,
                name = newName,
                age = validAge,
                goalWeightKg = validGoalWeight,
                dailyCalorieTarget = newTarget,
                updatedAt = System.currentTimeMillis()
            )

            runCatching { userRepository.saveUserProfile(updatedProfile) }
                .onSuccess {
                    _state.update {
                        it.copy(isSaving = false, userProfile = updatedProfile, showSuccessMessage = true)
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isSaving = false,
                            errorMessage = error.message ?: "Profile changes could not be saved."
                        )
                    }
                }
        }
    }

    fun dismissSuccessMessage() {
        _state.update { it.copy(showSuccessMessage = false) }
    }

    fun dismissErrorMessage() {
        _state.update { it.copy(errorMessage = null) }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
        }
    }
}
