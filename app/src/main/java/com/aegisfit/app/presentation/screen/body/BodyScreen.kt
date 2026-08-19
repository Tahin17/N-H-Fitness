package com.aegisfit.app.presentation.screen.body

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aegisfit.app.presentation.components.AsymmetryIndicator
import com.aegisfit.app.presentation.components.StatCard
import com.aegisfit.app.presentation.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs

@Composable
fun BodyScreen(
    onNavigateToMeasurement: () -> Unit = {},
    viewModel: BodyViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToMeasurement,
                containerColor = NeonCyan,
                contentColor = AegisDarkBackground
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Measurements")
            }
        },
        containerColor = AegisDarkBackground
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    color = NeonCyan,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (state.userProfile == null) {
                Text(
                    text = "Set up your profile first",
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    BiometricSummarySection(state)
                    WeightAndGoalSection(state)
                    LatestMeasurementsSection(state)
                    if (state.asymmetries.isNotEmpty()) {
                        AsymmetryIndicator(results = state.asymmetries)
                    }
                    MeasurementHistorySection(state)
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}

@Composable
fun BiometricSummarySection(state: BodyState) {
    val bmiColor = when (state.bmiCategory) {
        "Normal" -> NeonGreen
        "Underweight", "Overweight" -> NeonAmber
        else -> NeonRed
    }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StatCard(
                title = "BMI",
                value = "%.1f".format(state.bmi),
                subtitle = state.bmiCategory,
                icon = Icons.Default.Person,
                accentColor = bmiColor,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "BMR",
                value = "${state.bmr.toInt()}",
                subtitle = "kcal/day",
                icon = Icons.Default.Favorite,
                accentColor = NeonAmber,
                modifier = Modifier.weight(1f)
            )
        }
        StatCard(
            title = "TDEE",
            value = "${state.tdee.toInt()}",
            subtitle = "Total Daily Energy Expenditure (kcal/day)",
            icon = Icons.Default.Info,
            accentColor = NeonCyan,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun WeightAndGoalSection(state: BodyState) {
    val profile = state.userProfile ?: return
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Current Weight & Goal",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Current", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${profile.weightKg} kg", style = MaterialTheme.typography.titleLarge, color = NeonCyan)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Goal", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${profile.goalWeightKg} kg", style = MaterialTheme.typography.titleLarge, color = NeonGreen)
                }
            }
            
            val diff = abs(profile.weightKg - profile.goalWeightKg)
            Spacer(modifier = Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = { 0.5f }, // Placeholder progress
                modifier = Modifier.fillMaxWidth().height(8.dp),
                color = NeonCyan,
                trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${"%.1f".format(diff)} kg to go",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun LatestMeasurementsSection(state: BodyState) {
    val measurement = state.latestMeasurement
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Latest Measurements",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (measurement != null) {
                    Text(
                        text = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(measurement.date)),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            
            if (measurement == null) {
                Text(
                    text = "No measurements yet",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(vertical = 16.dp)
                )
            } else {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f)) {
                        MeasurementRow("Chest", measurement.chestCm)
                        MeasurementRow("Waist", measurement.waistCm)
                        MeasurementRow("Hips", measurement.hipsCm)
                        MeasurementRow("Neck", measurement.neckCm)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        MeasurementRow("Bicep (L/R)", measurement.leftBicepCm, measurement.rightBicepCm)
                        MeasurementRow("Forearm (L/R)", measurement.leftForearmCm, measurement.rightForearmCm)
                        MeasurementRow("Quad (L/R)", measurement.leftQuadCm, measurement.rightQuadCm)
                        MeasurementRow("Calf (L/R)", measurement.leftCalfCm, measurement.rightCalfCm)
                    }
                }
            }
        }
    }
}

@Composable
fun MeasurementRow(label: String, val1: Double?, val2: Double? = null) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        val valueStr = if (val2 != null) {
            "${val1 ?: "-"} / ${val2 ?: "-"}"
        } else {
            "${val1 ?: "-"}"
        }
        Text(text = valueStr, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun MeasurementHistorySection(state: BodyState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Measurement History",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${state.allMeasurements.size} measurements recorded",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Detailed charts coming in next update",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
