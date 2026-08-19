package com.aegisfit.app.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aegisfit.app.presentation.theme.NeonCyan
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestTimerDialog(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTime by remember { mutableIntStateOf(60) }
    var timeRemaining by remember { mutableIntStateOf(60) }
    var isRunning by remember { mutableStateOf(false) }

    LaunchedEffect(isRunning, timeRemaining) {
        if (isRunning && timeRemaining > 0) {
            delay(1000L)
            timeRemaining -= 1
        } else if (isRunning && timeRemaining == 0) {
            isRunning = false
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Rest Timer") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${timeRemaining / 60}:${(timeRemaining % 60).toString().padStart(2, '0')}",
                    style = MaterialTheme.typography.displayMedium,
                    color = NeonCyan
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    listOf(60, 90, 120).forEach { time ->
                        FilterChip(
                            selected = selectedTime == time,
                            onClick = {
                                selectedTime = time
                                if (!isRunning) {
                                    timeRemaining = time
                                }
                            },
                            label = { Text("${time}s") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = NeonCyan,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (isRunning) {
                    isRunning = false
                    timeRemaining = selectedTime
                } else {
                    isRunning = true
                    if (timeRemaining == 0) timeRemaining = selectedTime
                }
            }) {
                Text(if (isRunning) "Stop" else "Start", color = NeonCyan)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        },
        modifier = modifier
    )
}
