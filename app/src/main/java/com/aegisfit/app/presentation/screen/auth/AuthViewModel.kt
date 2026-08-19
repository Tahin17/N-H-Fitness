package com.aegisfit.app.presentation.screen.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aegisfit.app.domain.repository.AuthRepository
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthResult>(AuthResult.Idle)
    val authState = _authState.asStateFlow()

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthResult.Loading
            val result = authRepository.signIn(email, password)
            if (result.isSuccess) {
                _authState.value = AuthResult.Success
            } else {
                val error = result.exceptionOrNull()
                Log.w(TAG, "Sign-in failed", error)
                _authState.value = AuthResult.Error(friendlyMessage(error, AuthAction.SignIn))
            }
        }
    }

    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthResult.Loading
            val result = authRepository.signUp(email, password)
            if (result.isSuccess) {
                _authState.value = AuthResult.Success
            } else {
                val error = result.exceptionOrNull()
                Log.w(TAG, "Account creation failed", error)
                _authState.value = AuthResult.Error(friendlyMessage(error, AuthAction.SignUp))
            }
        }
    }

    fun sendPasswordReset(email: String) {
        viewModelScope.launch {
            _authState.value = AuthResult.Loading
            val result = authRepository.sendPasswordReset(email)
            _authState.value = if (result.isSuccess) {
                AuthResult.Notice("If an account exists for that email, a reset link has been sent.")
            } else {
                val error = result.exceptionOrNull()
                Log.w(TAG, "Password reset failed", error)
                AuthResult.Error(friendlyMessage(error, AuthAction.PasswordReset))
            }
        }
    }

    fun setError(message: String) {
        _authState.value = AuthResult.Error(message)
    }

    fun resetState() {
        _authState.value = AuthResult.Idle
    }

    private fun friendlyMessage(error: Throwable?, action: AuthAction): String {
        if (error is FirebaseNetworkException) {
            return "No network connection. Check your internet and try again."
        }

        val detail = error?.message.orEmpty()
        if (detail.contains("CONFIGURATION_NOT_FOUND", ignoreCase = true) ||
            detail.contains("OPERATION_NOT_ALLOWED", ignoreCase = true)
        ) {
            return "Email sign-in is not enabled for this Firebase project. Enable Email/Password in Firebase Authentication."
        }
        if (detail.contains("API_KEY_INVALID", ignoreCase = true) ||
            detail.contains("API_KEY_SERVICE_BLOCKED", ignoreCase = true) ||
            detail.contains("PROJECT_NOT_FOUND", ignoreCase = true)
        ) {
            return "The app's Firebase configuration is invalid. Replace google-services.json with the Android configuration for this app."
        }

        val code = (error as? FirebaseAuthException)?.errorCode
        return when (code) {
            "ERROR_INVALID_EMAIL" -> "Enter a valid email address."
            "ERROR_WEAK_PASSWORD" -> "Use at least 8 characters with a letter and a number."
            "ERROR_EMAIL_ALREADY_IN_USE" -> "An account already exists for this email."
            "ERROR_INVALID_CREDENTIAL", "ERROR_WRONG_PASSWORD", "ERROR_USER_NOT_FOUND" ->
                "The email or password is incorrect."
            "ERROR_USER_DISABLED" -> "This account has been disabled."
            "ERROR_TOO_MANY_REQUESTS" -> "Too many attempts. Wait a few minutes and try again."
            "ERROR_NETWORK_REQUEST_FAILED" -> "No network connection. Check your internet and try again."
            "ERROR_INTERNAL_ERROR" -> "The sign-in service returned an internal error. Try again shortly."
            else -> when (action) {
                AuthAction.SignIn -> "We couldn't sign you in. Please try again."
                AuthAction.SignUp -> "We couldn't create your account. Please try again."
                AuthAction.PasswordReset -> "We couldn't send the reset email. Please try again."
            }
        }
    }

    private enum class AuthAction { SignIn, SignUp, PasswordReset }

    companion object {
        private const val TAG = "NHTAuth"
    }
}

sealed class AuthResult {
    object Idle : AuthResult()
    object Loading : AuthResult()
    object Success : AuthResult()
    data class Notice(val message: String) : AuthResult()
    data class Error(val message: String) : AuthResult()
}
