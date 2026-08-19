package com.aegisfit.app.presentation.screen.workout

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aegisfit.app.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardioScreen(
    onNavigateBack: () -> Unit,
    viewModel: CardioViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.isSaved) {
        if (state.isSaved) onNavigateBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Log Cardio", color = NeonCyan, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = NeonCyan)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AegisDarkBackground)
            )
        },
        containerColor = AegisDarkBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
        ) {
            Text(
                "Choose Activity", 
                style = MaterialTheme.typography.titleLarge, 
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val types = listOf("Treadmill", "Stairmaster", "Cycling", "Outdoor Stairs")
                types.forEach { type ->
                    FilterChip(
                        selected = state.selectedType == type,
                        onClick = { viewModel.selectType(type) },
                        label = { Text(type) }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                "Duration (minutes)", 
                style = MaterialTheme.typography.titleLarge, 
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = state.durationInput,
                    onValueChange = { viewModel.updateDuration(it) },
                    label = { Text("Minutes") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.width(150.dp),
                    singleLine = true,
                    isError = state.durationInput.toIntOrNull()?.let { it in 1..600 } != true,
                    supportingText = { Text("1–600 min") }
                )
                
                // Quick presets
                listOf(15, 30, 45).forEach { min ->
                    Button(
                        onClick = { viewModel.updateDuration(min.toString()) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (state.durationInput == min.toString()) NeonCyan else Color.Gray.copy(alpha = 0.3f),
                            contentColor = if (state.durationInput == min.toString()) Color.Black else Color.White
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp)
                    ) {
                        Text("${min}m")
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(48.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = androidx.compose.material3.CardDefaults.cardColors(
                    containerColor = NeonCyan.copy(alpha = 0.1f)
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Estimated Burn", color = Color.LightGray)
                    Text(
                        "${state.estimatedCalories} kcal", 
                        style = MaterialTheme.typography.headlineLarge,
                        color = NeonCyan,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))

            state.errorMessage?.let { message ->
                Text(
                    message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
            
            Button(
                onClick = viewModel::logCardio,
                enabled = !state.isSaving && state.durationInput.toIntOrNull()?.let { it in 1..600 } == true,
                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
            ) {
                if (state.isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                } else {
                    Text("Confirm and Log", color = Color.Black, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}
