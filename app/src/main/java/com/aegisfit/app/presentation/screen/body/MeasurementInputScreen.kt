package com.aegisfit.app.presentation.screen.body

import androidx.lifecycle.compose.collectAsStateWithLifecycle

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aegisfit.app.domain.model.AsymmetryResult
import com.aegisfit.app.domain.model.BodyMeasurement
import com.aegisfit.app.domain.repository.AuthRepository
import com.aegisfit.app.domain.repository.BodyMeasurementRepository
import com.aegisfit.app.domain.usecase.biometrics.DetectAsymmetryUseCase
import com.aegisfit.app.presentation.components.AsymmetryIndicator
import com.aegisfit.app.presentation.theme.AegisDarkBackground
import com.aegisfit.app.presentation.theme.NeonCyan
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MeasurementInputState(
    val chest: String = "",
    val waist: String = "",
    val hips: String = "",
    val neck: String = "",
    val leftBicep: String = "",
    val rightBicep: String = "",
    val leftForearm: String = "",
    val rightForearm: String = "",
    val leftQuad: String = "",
    val rightQuad: String = "",
    val leftCalf: String = "",
    val rightCalf: String = "",
    val asymmetries: List<AsymmetryResult> = emptyList(),
    val isSaving: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class MeasurementInputViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val bodyMeasurementRepository: BodyMeasurementRepository,
    private val detectAsymmetryUseCase: DetectAsymmetryUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(MeasurementInputState())
    val state: StateFlow<MeasurementInputState> = _state.asStateFlow()

    private val userId: String
        get() = authRepository.currentUserId ?: ""

    fun updateField(field: String, value: String) {
        if (value.length > 7 || value.count { it == '.' } > 1 ||
            value.any { !it.isDigit() && it != '.' }
        ) return
        _state.update { currentState ->
            val nextState = when (field) {
                "chest" -> currentState.copy(chest = value)
                "waist" -> currentState.copy(waist = value)
                "hips" -> currentState.copy(hips = value)
                "neck" -> currentState.copy(neck = value)
                "leftBicep" -> currentState.copy(leftBicep = value)
                "rightBicep" -> currentState.copy(rightBicep = value)
                "leftForearm" -> currentState.copy(leftForearm = value)
                "rightForearm" -> currentState.copy(rightForearm = value)
                "leftQuad" -> currentState.copy(leftQuad = value)
                "rightQuad" -> currentState.copy(rightQuad = value)
                "leftCalf" -> currentState.copy(leftCalf = value)
                "rightCalf" -> currentState.copy(rightCalf = value)
                else -> currentState
            }
            
            val tempMeasurement = BodyMeasurement(
                userId = userId,
                id = 0L,
                date = System.currentTimeMillis(),
                chestCm = nextState.chest.toDoubleOrNull(),
                waistCm = nextState.waist.toDoubleOrNull(),
                hipsCm = nextState.hips.toDoubleOrNull(),
                neckCm = nextState.neck.toDoubleOrNull(),
                leftBicepCm = nextState.leftBicep.toDoubleOrNull(),
                rightBicepCm = nextState.rightBicep.toDoubleOrNull(),
                leftForearmCm = nextState.leftForearm.toDoubleOrNull(),
                rightForearmCm = nextState.rightForearm.toDoubleOrNull(),
                leftQuadCm = nextState.leftQuad.toDoubleOrNull(),
                rightQuadCm = nextState.rightQuad.toDoubleOrNull(),
                leftCalfCm = nextState.leftCalf.toDoubleOrNull(),
                rightCalfCm = nextState.rightCalf.toDoubleOrNull()
            )
            val newAsymmetries = detectAsymmetryUseCase(tempMeasurement)
            
            nextState.copy(asymmetries = newAsymmetries, errorMessage = null)
        }
    }

    fun saveMeasurement(onComplete: () -> Unit) {
        val currentState = _state.value
        val values = listOf(
            currentState.chest, currentState.waist, currentState.hips, currentState.neck,
            currentState.leftBicep, currentState.rightBicep,
            currentState.leftForearm, currentState.rightForearm,
            currentState.leftQuad, currentState.rightQuad,
            currentState.leftCalf, currentState.rightCalf
        )
        val userId = authRepository.currentUserId
        val error = when {
            userId.isNullOrBlank() -> "Your session expired. Please sign in again."
            values.none(String::isNotBlank) -> "Enter at least one measurement."
            values.any { value ->
                value.isNotBlank() &&
                    value.toDoubleOrNull()?.let { it.isFinite() && it in 5.0..300.0 } != true
            } -> "Measurements must be between 5 and 300 cm."
            else -> null
        }
        if (error != null) {
            _state.update { it.copy(errorMessage = error) }
            return
        }
        if (currentState.isSaving) return

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, errorMessage = null) }
            val measurement = BodyMeasurement(
                userId = requireNotNull(userId),
                id = 0L,
                date = System.currentTimeMillis(),
                chestCm = currentState.chest.toDoubleOrNull(),
                waistCm = currentState.waist.toDoubleOrNull(),
                hipsCm = currentState.hips.toDoubleOrNull(),
                neckCm = currentState.neck.toDoubleOrNull(),
                leftBicepCm = currentState.leftBicep.toDoubleOrNull(),
                rightBicepCm = currentState.rightBicep.toDoubleOrNull(),
                leftForearmCm = currentState.leftForearm.toDoubleOrNull(),
                rightForearmCm = currentState.rightForearm.toDoubleOrNull(),
                leftQuadCm = currentState.leftQuad.toDoubleOrNull(),
                rightQuadCm = currentState.rightQuad.toDoubleOrNull(),
                leftCalfCm = currentState.leftCalf.toDoubleOrNull(),
                rightCalfCm = currentState.rightCalf.toDoubleOrNull()
            )
            runCatching { bodyMeasurementRepository.saveMeasurement(measurement) }
                .onSuccess { onComplete() }
                .onFailure { throwable ->
                    _state.update {
                        it.copy(
                            isSaving = false,
                            errorMessage = throwable.message ?: "Measurements could not be saved."
                        )
                    }
                }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeasurementInputScreen(
    onNavigateBack: () -> Unit,
    viewModel: MeasurementInputViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Measurements") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AegisDarkBackground,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        containerColor = AegisDarkBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Core Measurements", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
            MeasurementInput("Chest", state.chest) { viewModel.updateField("chest", it) }
            MeasurementInput("Waist", state.waist) { viewModel.updateField("waist", it) }
            MeasurementInput("Hips", state.hips) { viewModel.updateField("hips", it) }
            MeasurementInput("Neck", state.neck) { viewModel.updateField("neck", it) }

            Spacer(modifier = Modifier.height(8.dp))
            Text("Bilateral Measurements", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
            BilateralInputRow("Bicep", state.leftBicep, state.rightBicep, 
                onLeftChange = { viewModel.updateField("leftBicep", it) },
                onRightChange = { viewModel.updateField("rightBicep", it) }
            )
            BilateralInputRow("Forearm", state.leftForearm, state.rightForearm,
                onLeftChange = { viewModel.updateField("leftForearm", it) },
                onRightChange = { viewModel.updateField("rightForearm", it) }
            )
            BilateralInputRow("Quad", state.leftQuad, state.rightQuad,
                onLeftChange = { viewModel.updateField("leftQuad", it) },
                onRightChange = { viewModel.updateField("rightQuad", it) }
            )
            BilateralInputRow("Calf", state.leftCalf, state.rightCalf,
                onLeftChange = { viewModel.updateField("leftCalf", it) },
                onRightChange = { viewModel.updateField("rightCalf", it) }
            )

            if (state.asymmetries.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                AsymmetryIndicator(results = state.asymmetries)
            }

            state.errorMessage?.let { message ->
                Text(
                    message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { viewModel.saveMeasurement(onNavigateBack) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = AegisDarkBackground),
                enabled = !state.isSaving
            ) {
                if (state.isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                } else {
                    Text("Save Measurements", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}

@Composable
fun MeasurementInput(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        suffix = { Text("cm") },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = NeonCyan,
            focusedLabelColor = NeonCyan
        )
    )
}

@Composable
fun BilateralInputRow(
    label: String,
    leftValue: String,
    rightValue: String,
    onLeftChange: (String) -> Unit,
    onRightChange: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = leftValue,
            onValueChange = onLeftChange,
            label = { Text("L $label") },
            suffix = { Text("cm") },
            modifier = Modifier.weight(1f),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonCyan,
                focusedLabelColor = NeonCyan
            )
        )
        OutlinedTextField(
            value = rightValue,
            onValueChange = onRightChange,
            label = { Text("R $label") },
            suffix = { Text("cm") },
            modifier = Modifier.weight(1f),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonCyan,
                focusedLabelColor = NeonCyan
            )
        )
    }
}
