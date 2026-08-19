package com.aegisfit.app.presentation.screen.onboarding

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aegisfit.app.domain.model.ActivityLevel
import com.aegisfit.app.domain.model.Gender
import com.aegisfit.app.presentation.theme.NeonCyan
import com.aegisfit.app.presentation.theme.NeonPurple
import com.aegisfit.app.presentation.theme.NeonGreen
import com.aegisfit.app.presentation.theme.NeonRed
import com.aegisfit.app.presentation.theme.NeonAmber
import com.aegisfit.app.presentation.theme.NeonPink
import com.aegisfit.app.presentation.theme.NeonBlue
import com.aegisfit.app.presentation.theme.AegisDarkBackground
import com.aegisfit.app.presentation.theme.AegisDarkSurface
import com.aegisfit.app.presentation.theme.AegisDarkSurfaceVariant
import com.aegisfit.app.presentation.theme.GradientCyanPurple


@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.isComplete) {
        if (state.isComplete) {
            onComplete()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AegisDarkBackground)
            .padding(16.dp)
    ) {
        StepIndicator(
            currentStep = state.currentStep,
            totalSteps = state.totalSteps,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        )

        AnimatedContent(
            targetState = state.currentStep,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            transitionSpec = {
                if (targetState > initialState) {
                    slideInHorizontally { width -> width } + fadeIn() togetherWith
                            slideOutHorizontally { width -> -width } + fadeOut()
                } else {
                    slideInHorizontally { width -> -width } + fadeIn() togetherWith
                            slideOutHorizontally { width -> width } + fadeOut()
                }
            },
            label = "StepTransition"
        ) { step ->
            when (step) {
                0 -> WelcomeStep(state, viewModel)
                1 -> BodyBasicsStep(state, viewModel)
                2 -> MeasurementsStep(state, viewModel)
                3 -> SummaryStep(state, viewModel)
            }
        }

        state.errorMessage?.let { message ->
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                textAlign = TextAlign.Center
            )
        }

        BottomNavigation(
            state = state,
            onNext = { viewModel.nextStep() },
            onBack = { viewModel.previousStep() },
            onSkip = { viewModel.skipMeasurements() },
            onFinish = { viewModel.completeOnboarding() }
        )
    }
}

@Composable
fun StepIndicator(currentStep: Int, totalSteps: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until totalSteps) {
            val isCompleted = i <= currentStep
            val color by animateColorAsState(
                targetValue = if (isCompleted) NeonCyan else AegisDarkSurfaceVariant,
                label = "StepColor"
            )
            val weight by animateFloatAsState(
                targetValue = if (i == currentStep) 2f else 1f,
                label = "StepWeight"
            )

            Box(
                modifier = Modifier
                    .weight(weight)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(color)
            )
        }
    }
}

@Composable
fun BottomNavigation(
    state: OnboardingState,
    onNext: () -> Unit,
    onBack: () -> Unit,
    onSkip: () -> Unit,
    onFinish: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (state.currentStep > 0) {
            TextButton(onClick = onBack) {
                Text("Back", color = NeonCyan)
            }
        } else {
            Spacer(modifier = Modifier.width(64.dp))
        }

        when (state.currentStep) {
            0, 1 -> {
                Button(
                    onClick = onNext,
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                ) {
                    Text("Next", color = AegisDarkBackground, fontWeight = FontWeight.Bold)
                }
            }
            2 -> {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedButton(
                        onClick = onSkip,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonCyan),
                        border = ButtonDefaults.outlinedButtonBorder.copy(brush = SolidColor(NeonCyan))
                    ) {
                        Text("Skip")
                    }
                    Button(
                        onClick = onNext,
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                    ) {
                        Text("Next", color = AegisDarkBackground, fontWeight = FontWeight.Bold)
                    }
                }
            }
            3 -> {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 16.dp)
                        .height(48.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Brush.horizontalGradient(GradientCyanPurple))
                        .clickable(enabled = !state.isSaving, onClick = onFinish),
                    contentAlignment = Alignment.Center
                ) {
                    if (state.isSaving) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Get Started",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WelcomeStep(state: OnboardingState, viewModel: OnboardingViewModel) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Welcome to NHT Fitness",
            style = MaterialTheme.typography.headlineLarge,
            color = NeonCyan,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Your privacy-first fitness companion",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(Brush.horizontalGradient(listOf(NeonCyan, Color.Transparent)))
        )

        AegisTextField(
            value = state.name,
            onValueChange = { viewModel.updateName(it) },
            label = "Name"
        )

        AegisTextField(
            value = state.age,
            onValueChange = { viewModel.updateAge(it) },
            label = "Age",
            keyboardType = KeyboardType.Number
        )

        Text("Gender", color = NeonCyan, style = MaterialTheme.typography.titleMedium)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Gender.values().forEach { gender ->
                val selected = state.gender == gender
                FilterChip(
                    selected = selected,
                    onClick = { viewModel.updateGender(gender) },
                    label = { Text(gender.displayName) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = NeonCyan,
                        selectedLabelColor = AegisDarkBackground,
                        containerColor = AegisDarkSurfaceVariant,
                        labelColor = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BodyBasicsStep(state: OnboardingState, viewModel: OnboardingViewModel) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "Body Metrics",
            style = MaterialTheme.typography.headlineLarge,
            color = NeonCyan,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Measurements use kilograms and centimetres.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        AegisTextField(
            value = state.weightKg,
            onValueChange = { viewModel.updateWeight(it) },
            label = "Current Weight",
            suffix = "kg",
            keyboardType = KeyboardType.Decimal
        )

        AegisTextField(
            value = state.goalWeightKg,
            onValueChange = { viewModel.updateGoalWeight(it) },
            label = "Goal Weight",
            suffix = "kg",
            keyboardType = KeyboardType.Decimal
        )

        AegisTextField(
            value = state.heightCm,
            onValueChange = { viewModel.updateHeight(it) },
            label = "Height",
            suffix = "cm",
            keyboardType = KeyboardType.Decimal
        )

        AegisTextField(
            value = state.bodyFatPercent,
            onValueChange = { viewModel.updateBodyFatPercent(it) },
            label = "Body Fat % (Optional)",
            suffix = "%",
            keyboardType = KeyboardType.Decimal
        )
    }
}

@Composable
fun MeasurementsStep(state: OnboardingState, viewModel: OnboardingViewModel) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Body Measurements",
            style = MaterialTheme.typography.headlineLarge,
            color = NeonCyan,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Optional — helps track asymmetries",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(8.dp))

        Text("Core", color = NeonPurple, fontWeight = FontWeight.Bold)
        AegisTextField(state.chestCm, { viewModel.updateMeasurement("chest", it) }, "Chest", "cm")
        AegisTextField(state.waistCm, { viewModel.updateMeasurement("waist", it) }, "Waist", "cm")
        AegisTextField(state.hipsCm, { viewModel.updateMeasurement("hips", it) }, "Hips", "cm")
        AegisTextField(state.neckCm, { viewModel.updateMeasurement("neck", it) }, "Neck", "cm")

        Spacer(modifier = Modifier.height(16.dp))
        Text("Bilateral", color = NeonPurple, fontWeight = FontWeight.Bold)
        
        MeasurementRow("Bicep", state.leftBicepCm, state.rightBicepCm, 
            { viewModel.updateMeasurement("leftBicep", it) },
            { viewModel.updateMeasurement("rightBicep", it) })
            
        MeasurementRow("Forearm", state.leftForearmCm, state.rightForearmCm, 
            { viewModel.updateMeasurement("leftForearm", it) },
            { viewModel.updateMeasurement("rightForearm", it) })
            
        MeasurementRow("Quad", state.leftQuadCm, state.rightQuadCm, 
            { viewModel.updateMeasurement("leftQuad", it) },
            { viewModel.updateMeasurement("rightQuad", it) })
            
        MeasurementRow("Calf", state.leftCalfCm, state.rightCalfCm, 
            { viewModel.updateMeasurement("leftCalf", it) },
            { viewModel.updateMeasurement("rightCalf", it) })
            
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun MeasurementRow(
    label: String,
    leftVal: String,
    rightVal: String,
    onLeftChange: (String) -> Unit,
    onRightChange: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AegisTextField(
            value = leftVal,
            onValueChange = onLeftChange,
            label = "L $label",
            suffix = "cm",
            modifier = Modifier.weight(1f)
        )
        AegisTextField(
            value = rightVal,
            onValueChange = onRightChange,
            label = "R $label",
            suffix = "cm",
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun SummaryStep(state: OnboardingState, viewModel: OnboardingViewModel) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "Your Profile Summary",
            style = MaterialTheme.typography.headlineLarge,
            color = NeonCyan,
            fontWeight = FontWeight.Bold
        )

        Text("Activity Level", color = NeonCyan, style = MaterialTheme.typography.titleMedium)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            ActivityLevel.values().forEach { level ->
                val selected = state.activityLevel == level
                val backgroundColor = if (selected) AegisDarkSurfaceVariant else AegisDarkSurface
                val borderColor = if (selected) NeonCyan else Color.Transparent
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(backgroundColor)
                        .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                        .clickable { viewModel.updateActivityLevel(level) }
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(level.displayName, color = MaterialTheme.colorScheme.onSurface, fontWeight = if(selected) FontWeight.Bold else FontWeight.Normal)
                    Text("x${level.multiplier}", color = if(selected) NeonCyan else MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = AegisDarkSurfaceVariant),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ResultItem(
                    label = "BMI",
                    value = String.format("%.1f — %s", state.calculatedBmi, state.bmiCategory),
                    color = when {
                        state.bmiCategory.contains("Normal", true) -> NeonGreen
                        state.bmiCategory.contains("Obese", true) -> NeonRed
                        else -> NeonAmber
                    }
                )
                
                ResultItem(
                    label = "BMR",
                    value = "${state.calculatedBmr.toInt()} kcal/day",
                    color = NeonPurple
                )
                
                ResultItem(
                    label = "TDEE",
                    value = "${state.calculatedTdee.toInt()} kcal/day",
                    color = NeonCyan
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                AegisTextField(
                    value = state.dailyCalorieTarget,
                    onValueChange = { viewModel.updateDailyCalorieTarget(it) },
                    label = "Daily Calorie Target",
                    suffix = "kcal",
                    keyboardType = KeyboardType.Number
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun ResultItem(label: String, value: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.titleMedium)
        Text(value, color = color, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AegisTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    suffix: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        suffix = suffix?.let { { Text(it, color = MaterialTheme.colorScheme.onSurfaceVariant) } },
        modifier = modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = NeonCyan,
            unfocusedBorderColor = AegisDarkSurfaceVariant,
            focusedLabelColor = NeonCyan,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            cursorColor = NeonCyan,
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
        ),
        shape = RoundedCornerShape(12.dp),
        singleLine = true
    )
}
