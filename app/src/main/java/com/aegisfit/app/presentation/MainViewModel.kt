package com.aegisfit.app.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aegisfit.app.domain.repository.AuthRepository
import com.aegisfit.app.domain.repository.DataSyncRepository
import android.util.Log
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject

data class MainUiState(
    val isReady: Boolean = false,
    val isAuthenticated: Boolean = false
)

@HiltViewModel
class MainViewModel @Inject constructor(
    authRepository: AuthRepository,
    private val dataSyncRepository: DataSyncRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MainUiState())
    val state: StateFlow<MainUiState> = _state.asStateFlow()
    private var recoveryJob: Job? = null

    init {
        viewModelScope.launch {
            authRepository.authState.collect { userId ->
                recoveryJob?.cancel()
                _state.update {
                    it.copy(isReady = true, isAuthenticated = userId != null)
                }
                if (userId != null) {
                    recoveryJob = launch {
                        val result = withTimeoutOrNull(CLOUD_RECOVERY_TIMEOUT_MS) {
                            dataSyncRepository.pullAllCloudData(userId)
                        }
                        if (result == null) {
                            Log.w(TAG, "Background cloud recovery timed out")
                        } else {
                            result.exceptionOrNull()?.let { error ->
                                Log.w(TAG, "Background cloud recovery was incomplete", error)
                            }
                        }
                    }
                }
            }
        }
    }

    private companion object {
        const val TAG = "NHTSession"
        const val CLOUD_RECOVERY_TIMEOUT_MS = 12_000L
    }
}
