package com.aegisfit.app.presentation.screen.profile

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aegisfit.app.domain.model.ActivityLevel
import com.aegisfit.app.presentation.theme.AegisDarkBackground
import com.aegisfit.app.presentation.theme.NeonAmber
import com.aegisfit.app.presentation.theme.NeonCyan
import com.aegisfit.app.presentation.theme.NeonRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.showSuccessMessage) {
        if (state.showSuccessMessage) {
            snackbarHostState.showSnackbar("Profile updated successfully!")
            viewModel.dismissSuccessMessage()
        }
    }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.dismissErrorMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile & Settings", color = NeonCyan, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = NeonCyan)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        viewModel.signOut()
                    }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Logout", tint = NeonRed)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AegisDarkBackground)
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = AegisDarkBackground
    ) { paddingValues ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = NeonCyan)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // XP & Rank
                RankSection(xp = state.userProfile?.xp ?: 0, rank = state.userProfile?.rank ?: "Starter")

                // Personal Info
                ProfileSectionTitle("Personal Information")
                
                OutlinedTextField(
                    value = state.nameInput,
                    onValueChange = { viewModel.onNameChange(it) },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) }
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedTextField(
                        value = state.ageInput,
                        onValueChange = { viewModel.onAgeChange(it) },
                        label = { Text("Age") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    
                    OutlinedTextField(
                        value = state.targetWeightInput,
                        onValueChange = { viewModel.onTargetWeightChange(it) },
                        label = { Text("Target Weight (kg)") },
                        modifier = Modifier.weight(1.5f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                }

                // Goals & Activity
                ProfileSectionTitle("Goals & Activity")
                
                Text("Activity Level", style = MaterialTheme.typography.titleMedium, color = Color.White)
                
                ActivityLevel.entries.forEach { level ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = state.userProfile?.activityLevel == level,
                            onClick = { viewModel.updateActivityLevel(level) },
                            colors = RadioButtonDefaults.colors(selectedColor = NeonCyan)
                        )
                        Text(
                            text = level.displayName,
                            color = if (state.userProfile?.activityLevel == level) NeonCyan else Color.White,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { viewModel.saveProfile() },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                    enabled = !state.isSaving
                ) {
                    if (state.isSaving) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.Black)
                    } else {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Save Changes", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun RankSection(xp: Long, rank: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(NeonAmber.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Star, contentDescription = null, tint = NeonAmber, modifier = Modifier.size(32.dp))
            }
            Spacer(modifier = Modifier.width(20.dp))
            Column {
                Text(text = rank.uppercase(), style = MaterialTheme.typography.titleLarge, color = NeonAmber, fontWeight = FontWeight.ExtraBold)
                Text(text = "$xp consistency points", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.7f))
                
                Spacer(modifier = Modifier.height(8.dp))
                
                val progress = (xp % 1000).toFloat() / 1000f
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth().height(4.dp).clip(CircleShape),
                    color = NeonAmber,
                    trackColor = NeonAmber.copy(alpha = 0.1f)
                )
            }
        }
    }
}

@Composable
private fun ProfileSectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        color = NeonCyan,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}
