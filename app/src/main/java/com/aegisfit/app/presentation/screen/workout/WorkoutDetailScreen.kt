package com.aegisfit.app.presentation.screen.workout

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aegisfit.app.presentation.components.ExerciseSetRow
import com.aegisfit.app.presentation.components.RestTimerDialog
import com.aegisfit.app.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutDetailScreen(
    viewModel: WorkoutDetailViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showRestTimer by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { snackbarHostState.showSnackbar(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (uiState.exerciseName.isNotEmpty()) uiState.exerciseName else "Workout Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showRestTimer = true }) {
                        Icon(Icons.Default.Timer, contentDescription = "Rest Timer")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AegisDarkBackground,
                    titleContentColor = NeonCyan,
                    navigationIconContentColor = NeonCyan,
                    actionIconContentColor = NeonCyan
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.onSetAdded() },
                containerColor = NeonCyan,
                contentColor = AegisDarkBackground
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Set")
            }
        },
        containerColor = AegisDarkBackground
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize()
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    itemsIndexed(uiState.sets) { index, set ->
                        val ghostLog = uiState.ghostSets.find { it.setNumber == set.setNumber }
                        val ghostData = ghostLog?.let { "${it.weight} kg x ${it.reps} reps" }
                        
                        ExerciseSetRow(
                            setNumber = index + 1,
                            reps = set.reps,
                            weight = set.weight,
                            isCompleted = set.isCompleted,
                            ghostData = ghostData,
                            onRepsChange = { newReps ->
                                viewModel.onSetChanged(set.id, set.setNumber, newReps, set.weight, set.isCompleted)
                            },
                            onWeightChange = { newWeight ->
                                viewModel.onSetChanged(set.id, set.setNumber, set.reps, newWeight, set.isCompleted)
                            },
                            onToggleCompleted = { completed ->
                                viewModel.onSetChanged(set.id, set.setNumber, set.reps, set.weight, completed)
                            }
                        )
                    }
                }
                
                Card(
                    modifier = Modifier
                        .align(androidx.compose.ui.Alignment.BottomCenter)
                        .padding(bottom = 32.dp, start = 16.dp, end = 16.dp)
                        .clickable(enabled = !uiState.isFinishing) {
                            viewModel.finishExercise(onNavigateBack)
                        },
                    colors = CardDefaults.cardColors(containerColor = NeonAmber),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 12.dp, horizontal = 24.dp),
                        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (uiState.isFinishing) {
                                "Saving…"
                            } else {
                                "${if (uiState.hasCommittedSets) "Update" else "Finish"} Exercise"
                            },
                            style = MaterialTheme.typography.titleMedium,
                            color = AegisDarkBackground,
                            fontWeight = FontWeight.Bold
                        )
                        if (!uiState.isFinishing) {
                            Text(
                                text = "${uiState.totalCaloriesBurned.toInt()} kcal estimated • saves to dashboard",
                                style = MaterialTheme.typography.labelSmall,
                                color = AegisDarkBackground.copy(alpha = 0.72f)
                            )
                        }
                    }
                }
            }
        }
    }

    if (showRestTimer) {
        RestTimerDialog(onDismiss = { showRestTimer = false })
    }
}
