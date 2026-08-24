package com.aegisfit.app.presentation.screen.dashboard

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aegisfit.app.presentation.theme.*
import com.aegisfit.app.util.DateUtils

@Composable
fun DashboardScreen(
    onNavigateToOnboarding: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        if (state.showMonthDayDialog) {
            SelectedDayBottomSheet(
                stats = state.selectedMonthDayStats,
                onDismiss = { viewModel.dismissMonthDayDialog() },
                onSaveWeight = { dayMillis, weight -> viewModel.saveWeightForDate(dayMillis, weight) }
            )
        }

        if (state.showWeightDialog) {
            WeightDialog(
                onDismiss = { viewModel.dismissWeightDialog() },
                onSave = { viewModel.saveWeight(it) }
            )
        }

        when (state.hasProfile) {
            null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = NeonCyan)
                }
            }
            false -> {
                OnboardingPrompt(onNavigateToOnboarding = onNavigateToOnboarding)
            }
            true -> {
                DashboardContent(
                    state = state, 
                    onTabSelected = { viewModel.selectTab(it) },
                    onMonthDaySelected = { viewModel.selectMonthDay(it) },
                    onLogWeightClick = { viewModel.showWeightDialog() },
                    onNavigateToProfile = onNavigateToProfile
                )
            }
        }
    }
}

@Composable
private fun OnboardingPrompt(onNavigateToOnboarding: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Welcome to NHT Fitness",
            style = MaterialTheme.typography.headlineLarge,
            color = NeonCyan,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Set up your profile to unlock all features",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(32.dp))
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(NeonCyan)
                .clickable { onNavigateToOnboarding() }
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Get Started",
                color = Color.Black,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        // Feature cards
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            FeatureCard(
                title = "Workout",
                desc = "Track reps & sets",
                icon = Icons.Filled.FitnessCenter,
                color = NeonAmber,
                modifier = Modifier.weight(1f)
            )
            FeatureCard(
                title = "Nutrition",
                desc = "Macros & calories",
                icon = Icons.Filled.Restaurant,
                color = NeonGreen,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            FeatureCard(
                title = "Body",
                desc = "Measurements & fat",
                icon = Icons.Filled.AccessibilityNew,
                color = NeonCyan,
                modifier = Modifier.weight(1f)
            )
            FeatureCard(
                title = "Skincare",
                desc = "AM/PM Routines",
                icon = Icons.Filled.Face,
                color = NeonPurple,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun FeatureCard(title: String, desc: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(imageVector = icon, contentDescription = title, tint = color, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = desc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun DashboardContent(
    state: DashboardState, 
    onTabSelected: (Int) -> Unit, 
    onMonthDaySelected: (Long) -> Unit,
    onLogWeightClick: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .animateContentSize()
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "TODAY",
                    style = MaterialTheme.typography.labelMedium,
                    color = NeonCyan,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )
                Text(
                    text = state.greeting,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold
                )
            }
            IconButton(onClick = onNavigateToProfile) {
                Icon(
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = "Open profile",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(30.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))

        AnimatedVisibility(
            visible = !state.daily.hasLoggedWeight,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp)
                    .clickable { onLogWeightClick() },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(NeonCyan.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Filled.MonitorWeight, contentDescription = "Log Weight", tint = NeonCyan)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Log Weight Today", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        Text("Keep your profile up to date", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
        
        if (!state.daily.hasLoggedWeight) {
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Day Progress Dial at the top
        Box(modifier = Modifier.padding(horizontal = 24.dp)) {
            DayProgressDial(
                progressPercent = state.daily.dayProgressScore,
                completedGoals = state.daily.completedGoalsCount,
                totalGoals = state.daily.totalGoalsCount
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))

        // Tabs
        val tabs = listOf("Today", "This Week", "This Month")
        TabRow(
            selectedTabIndex = state.selectedTab,
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = NeonCyan,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[state.selectedTab]),
                    color = NeonCyan
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = state.selectedTab == index,
                    onClick = { onTabSelected(index) },
                    text = { Text(title, fontWeight = if (state.selectedTab == index) FontWeight.Bold else FontWeight.Normal) },
                    selectedContentColor = NeonCyan,
                    unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        
        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            when (state.selectedTab) {
                0 -> DailyTabContent(state.daily)
                1 -> WeeklyTabContent(state.weekly, onDaySelected = onMonthDaySelected)
                2 -> MonthlyTabContent(state.monthly, onDaySelected = onMonthDaySelected)
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun DayProgressDial(
    progressPercent: Int,
    completedGoals: Int,
    totalGoals: Int = 4
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .padding(22.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Day Progress",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = when {
                        progressPercent == 100 -> "🎉 All daily goals completed for today!"
                        completedGoals > 0 -> "$completedGoals of $totalGoals daily goals completed"
                        else -> "Log food, workout, water, or weight"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (progressPercent == 100) NeonGreen else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Calories • Workout • Hydration • Weight",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.72f)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Box(contentAlignment = Alignment.Center) {
                val animatedScore by animateIntAsState(
                    targetValue = progressPercent,
                    animationSpec = tween(1200, easing = FastOutSlowInEasing),
                    label = "day_progress_score"
                )

                val dialColor = when {
                    animatedScore >= 100 -> NeonGreen
                    animatedScore >= 50 -> NeonCyan
                    else -> NeonAmber
                }

                CircularProgressIndicator(
                    progress = { animatedScore.toFloat() / 100f },
                    modifier = Modifier.size(88.dp),
                    color = dialColor,
                    trackColor = dialColor.copy(alpha = 0.12f),
                    strokeWidth = 8.dp,
                    strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$animatedScore%",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun DailyTabContent(daily: DailyStats) {
    // 1. Calorie Ring
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(24.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Food calories",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            val progress = if (daily.calorieTarget > 0) (daily.caloriesConsumed / daily.calorieTarget).toFloat().coerceIn(0f, 1f) else 0f
            val animatedProgress by animateFloatAsState(targetValue = progress, animationSpec = tween(1000), label = "cal_progress")
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier.size(120.dp),
                    color = NeonRed,
                    trackColor = NeonRed.copy(alpha = 0.2f),
                    strokeWidth = 8.dp
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(imageVector = Icons.Filled.LocalFireDepartment, contentDescription = "Calories", tint = NeonRed)
                    Text(
                        text = "${daily.caloriesConsumed.toInt()}",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${daily.caloriesConsumed.toInt()} / ${daily.calorieTarget} kcal",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    CaloriesBurnedCard(daily)

    Spacer(modifier = Modifier.height(16.dp))

    // 2. Macro Bars
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            val pTarget = (daily.calorieTarget * 0.3 / 4).toFloat()
            val cTarget = (daily.calorieTarget * 0.4 / 4).toFloat()
            val fTarget = (daily.calorieTarget * 0.3 / 9).toFloat()

            MacroBar("Protein", daily.proteinG.toFloat(), pTarget, NeonCyan)
            Spacer(modifier = Modifier.height(12.dp))
            MacroBar("Carbs", daily.carbsG.toFloat(), cTarget, NeonAmber)
            Spacer(modifier = Modifier.height(12.dp))
            MacroBar("Fat", daily.fatG.toFloat(), fTarget, NeonPurple)
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // 3. Water Card
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = Icons.Filled.WaterDrop, contentDescription = "Hydration", tint = NeonCyan, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Water", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                val waterProgress = if (daily.waterGoalMl > 0) (daily.waterMl.toFloat() / daily.waterGoalMl).coerceIn(0f, 1f) else 0f
                val animatedWaterProgress by animateFloatAsState(targetValue = waterProgress, animationSpec = tween(800), label = "water_progress")
                LinearProgressIndicator(
                    progress = { animatedWaterProgress },
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                    color = NeonCyan,
                    trackColor = NeonCyan.copy(alpha = 0.2f)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = "${daily.waterMl} / ${daily.waterGoalMl} mL", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // 4. Workout Card
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = Icons.Filled.FitnessCenter, contentDescription = "Workout", tint = NeonAmber, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                val workoutText = if (daily.completedSets > 0) "${daily.completedSets} sets completed" else "No workout yet"
                Text(text = workoutText, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                if (daily.weightliftingCaloriesBurned > 0) {
                    Text(
                        text = "🔥 ${daily.weightliftingCaloriesBurned.toInt()} kcal burned",
                        style = MaterialTheme.typography.bodySmall,
                        color = NeonAmber
                    )
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Cardio Card
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = Icons.Filled.DirectionsRun, contentDescription = "Cardio", tint = NeonPurple, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.width(16.dp))
            val cardioText = if (daily.cardioCaloriesBurned > 0) "${daily.cardioCaloriesBurned.toInt()} kcal burned" else "No cardio yet"
            Text(text = cardioText, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // 5. Daily Care Card
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = Icons.Filled.CheckCircle, contentDescription = "Daily Care", tint = NeonPink, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Daily Care", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(2.dp))
                val careText = if (daily.dailyCareTotal > 0) {
                    "${daily.dailyCareCompleted} of ${daily.dailyCareTotal} tasks completed"
                } else {
                    "No care tasks added yet"
                }
                Text(text = careText, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (daily.dailyCareTotal > 0) {
                val isAllDone = daily.dailyCareCompleted == daily.dailyCareTotal
                BadgeText(if (isAllDone) "All Done" else "${daily.dailyCareCompleted}/${daily.dailyCareTotal}", isAllDone)
            }
        }
    }
}

@Composable
private fun CaloriesBurnedCard(daily: DailyStats) {
    val strengthCalories = daily.weightliftingCaloriesBurned.coerceAtLeast(0.0)
    val cardioCalories = daily.cardioCaloriesBurned.coerceAtLeast(0.0)
    val totalCalories = strengthCalories + cardioCalories
    val strengthSweepTarget = if (totalCalories > 0.0) {
        (strengthCalories / totalCalories * 360.0).toFloat()
    } else 0f
    val cardioSweepTarget = if (totalCalories > 0.0) {
        (cardioCalories / totalCalories * 360.0).toFloat()
    } else 0f
    val strengthSweep by animateFloatAsState(
        targetValue = strengthSweepTarget,
        animationSpec = tween(1000),
        label = "strength_calorie_sweep"
    )
    val cardioSweep by animateFloatAsState(
        targetValue = cardioSweepTarget,
        animationSpec = tween(1000),
        label = "cardio_calorie_sweep"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Calories burned",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            Box(contentAlignment = Alignment.Center) {
                val trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.10f)
                Canvas(modifier = Modifier.size(120.dp)) {
                    val ring = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                    drawArc(
                        color = trackColor,
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = ring
                    )
                    if (strengthSweep > 0f) {
                        drawArc(
                            color = NeonAmber,
                            startAngle = -90f,
                            sweepAngle = strengthSweep,
                            useCenter = false,
                            style = ring
                        )
                    }
                    if (cardioSweep > 0f) {
                        drawArc(
                            color = NeonPurple,
                            startAngle = -90f + strengthSweep,
                            sweepAngle = cardioSweep,
                            useCenter = false,
                            style = ring
                        )
                    }
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Filled.LocalFireDepartment,
                        contentDescription = "Calories burned",
                        tint = NeonAmber
                    )
                    Text(
                        text = totalCalories.toInt().toString(),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "kcal",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            if (totalCalories > 0.0) {
                Row(horizontalArrangement = Arrangement.spacedBy(18.dp)) {
                    Text(
                        text = "Strength ${strengthCalories.toInt()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = NeonAmber
                    )
                    Text(
                        text = "Cardio ${cardioCalories.toInt()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = NeonPurple
                    )
                }
            } else {
                Text(
                    text = "No completed exercise yet",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun MacroBar(label: String, value: Float, target: Float, color: Color) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
            Text(text = "${value.toInt()} / ${target.toInt()} g", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(modifier = Modifier.height(4.dp))
        val progress = if (target > 0) (value / target).coerceIn(0f, 1f) else 0f
        val animatedProgress by animateFloatAsState(targetValue = progress, animationSpec = tween(800), label = "macro_progress_$label")
        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
            color = color,
            trackColor = color.copy(alpha = 0.2f)
        )
    }
}

@Composable
private fun BadgeText(label: String, isDone: Boolean) {
    val bgColor = if (isDone) NeonGreen.copy(alpha = 0.2f) else NeonRed.copy(alpha = 0.2f)
    val contentColor = if (isDone) NeonGreen else NeonRed
    val iconStr = if (isDone) "✓" else "✗"
    
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(text = "$label $iconStr", style = MaterialTheme.typography.bodySmall, color = contentColor, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun WeeklyTabContent(weekly: WeeklyStats, onDaySelected: (Long) -> Unit) {
    val todayStart = DateUtils.todayStartMillis()
    val weekStart = DateUtils.weekStartMillis()
    val days = DateUtils.daysInRange(weekStart, DateUtils.addDays(weekStart, 6))
    val dayNames = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    // 1. 7-Day Activity & Weight Row
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Weekly Activity",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Tap day for summary",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                days.take(7).forEachIndexed { index, dayMillis ->
                    val isWorkoutDay = weekly.workoutDays.contains(dayMillis)
                    val isWeightDay = weekly.weightDays.contains(dayMillis)
                    val isToday = dayMillis == todayStart

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onDaySelected(dayMillis) }
                            .padding(horizontal = 4.dp, vertical = 6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        isWorkoutDay -> NeonCyan
                                        isWeightDay -> NeonAmber.copy(alpha = 0.3f)
                                        else -> Color.Transparent
                                    }
                                )
                                .border(
                                    width = if (isToday) 2.dp else 1.dp,
                                    color = when {
                                        isToday -> NeonCyan
                                        isWorkoutDay -> Color.Transparent
                                        else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                                    },
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isWorkoutDay) {
                                Icon(
                                    imageVector = Icons.Filled.FitnessCenter,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(18.dp)
                                )
                            } else if (isWeightDay) {
                                Icon(
                                    imageVector = Icons.Filled.MonitorWeight,
                                    contentDescription = null,
                                    tint = NeonAmber,
                                    modifier = Modifier.size(16.dp)
                                )
                            } else {
                                Text(
                                    text = DateUtils.dayOfMonth(dayMillis).toString(),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isToday) NeonCyan else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = dayNames.getOrElse(index) { "" },
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isToday) NeonCyan else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // 2. Stats Grid
    val avgCals = if (weekly.daysElapsed > 0) weekly.totalCalories / weekly.daysElapsed else 0.0
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        QuickStatCard(
            title = "Avg Calories",
            value = "${avgCals.toInt()} kcal",
            icon = Icons.Filled.LocalFireDepartment,
            color = NeonRed,
            modifier = Modifier.weight(1f)
        )
        
        QuickStatCard(
            title = "Workouts",
            value = "${weekly.workoutDays.size}",
            icon = Icons.Filled.FitnessCenter,
            color = NeonAmber,
            modifier = Modifier.weight(1f)
        )
    }

    Spacer(modifier = Modifier.height(16.dp))

    // 3. Weekly Weight Summary Card
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.MonitorWeight,
                contentDescription = "Weekly Weight",
                tint = NeonAmber,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Weekly Weight",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                val weightText = when {
                    weekly.latestWeightKg != null && weekly.avgWeightKg != null -> 
                        "Latest: ${String.format("%.1f", weekly.latestWeightKg)} kg (Avg: ${String.format("%.1f", weekly.avgWeightKg)} kg)"
                    weekly.latestWeightKg != null -> 
                        "Latest: ${String.format("%.1f", weekly.latestWeightKg)} kg"
                    else -> "No weight logged this week"
                }
                Text(
                    text = weightText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "${weekly.weightLogs.size} logs",
                style = MaterialTheme.typography.labelSmall,
                color = NeonAmber,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun MonthlyTabContent(monthly: MonthlyStats, onDaySelected: (Long) -> Unit) {
    val todayStart = DateUtils.todayStartMillis()
    val daysInMonth = DateUtils.daysInCurrentMonth()
    val monthStart = DateUtils.monthStartMillis()
    val dayOffset = DateUtils.firstDayOfMonthDayOfWeekOffset() // 0=Mon, 6=Sun
    val dayNames = listOf("M", "T", "W", "T", "F", "S", "S")

    // 1. Calendar Grid Card
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Workout & Weight Calendar",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Tap day for summary",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Day of week headers
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                dayNames.forEach { name ->
                    Box(modifier = Modifier.size(36.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = name,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))

            // Calendar cells with correct offset
            val totalCells = dayOffset + daysInMonth
            val rows = (totalCells + 6) / 7

            for (r in 0 until rows) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    for (c in 0..6) {
                        val cellIndex = r * 7 + c
                        val dayNum = cellIndex - dayOffset + 1

                        if (cellIndex >= dayOffset && dayNum <= daysInMonth) {
                            val dayMillis = DateUtils.addDays(monthStart, dayNum - 1)
                            val isWorkout = monthly.workoutDays.contains(dayMillis)
                            val isWeight = monthly.weightDays.contains(dayMillis)
                            val isToday = dayMillis == todayStart

                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            isWorkout -> NeonCyan
                                            isWeight -> NeonAmber.copy(alpha = 0.3f)
                                            else -> Color.Transparent
                                        }
                                    )
                                    .border(
                                        width = if (isToday) 2.dp else 1.dp,
                                        color = when {
                                            isToday -> NeonCyan
                                            isWorkout -> Color.Transparent
                                            else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                                        },
                                        shape = CircleShape
                                    )
                                    .clickable { onDaySelected(dayMillis) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$dayNum",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = if (isToday || isWorkout) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isWorkout) Color.Black else if (isToday) NeonCyan else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        } else {
                            Spacer(modifier = Modifier.size(36.dp))
                        }
                    }
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // 2. Stats Grid
    val avgCals = if (monthly.daysElapsed > 0) monthly.totalCalories / monthly.daysElapsed else 0.0
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        QuickStatCard(
            title = "Total Workouts",
            value = "${monthly.workoutDays.size}",
            icon = Icons.Filled.FitnessCenter,
            color = NeonAmber,
            modifier = Modifier.weight(1f)
        )
        
        QuickStatCard(
            title = "Avg Calories",
            value = "${avgCals.toInt()} kcal",
            icon = Icons.Filled.LocalFireDepartment,
            color = NeonRed,
            modifier = Modifier.weight(1f)
        )
    }

    Spacer(modifier = Modifier.height(16.dp))

    // 3. Monthly Weight Progress Card
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.MonitorWeight,
                contentDescription = "Monthly Weight Progress",
                tint = NeonCyan,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Monthly Weight Progress",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                val weightProgressText = when {
                    monthly.startWeightKg != null && monthly.latestWeightKg != null ->
                        "Start: ${String.format("%.1f", monthly.startWeightKg)} kg → Latest: ${String.format("%.1f", monthly.latestWeightKg)} kg"
                    monthly.latestWeightKg != null ->
                        "Latest: ${String.format("%.1f", monthly.latestWeightKg)} kg"
                    else -> "No weight logged this month"
                }
                Text(
                    text = weightProgressText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (monthly.weightChangeKg != null) {
                val change = monthly.weightChangeKg
                val changeStr = if (change >= 0) "+${String.format("%.1f", change)} kg" else "${String.format("%.1f", change)} kg"
                val changeColor = if (change <= 0) NeonGreen else NeonAmber
                Text(
                    text = changeStr,
                    style = MaterialTheme.typography.titleSmall,
                    color = changeColor,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun QuickStatCard(title: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = icon, contentDescription = title, tint = color, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = title, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SelectedDayBottomSheet(
    stats: SelectedDayStats?,
    onDismiss: () -> Unit,
    onSaveWeight: (Long, Double) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showInlineWeightEntry by remember { mutableStateOf(false) }
    var weightInput by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = AegisDarkSurface,
        contentColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            if (stats == null || stats.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = NeonCyan)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Loading day summary...", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                Text(
                    text = stats.formattedDate,
                    style = MaterialTheme.typography.headlineSmall,
                    color = NeonCyan,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Daily Summary & Activity",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(20.dp))

                // Stats rows
                BottomSheetStatRow(
                    icon = Icons.Filled.LocalFireDepartment,
                    label = "Calories Consumed",
                    value = "${stats.calories.toInt()} / ${stats.calorieTarget} kcal",
                    color = NeonRed
                )

                BottomSheetStatRow(
                    icon = Icons.Filled.FitnessCenter,
                    label = "Strength Workout Sets",
                    value = "${stats.completedSets} sets (${stats.weightliftingCaloriesBurned.toInt()} kcal)",
                    color = NeonAmber
                )

                BottomSheetStatRow(
                    icon = Icons.Filled.DirectionsRun,
                    label = "Cardio Calories",
                    value = "${stats.cardioCaloriesBurned.toInt()} kcal",
                    color = NeonPurple
                )

                BottomSheetStatRow(
                    icon = Icons.Filled.WaterDrop,
                    label = "Water Hydration",
                    value = "${stats.waterMl} / ${stats.waterGoalMl} mL",
                    color = NeonCyan
                )

                // Weight section with inline edit
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Filled.MonitorWeight, contentDescription = "Weight", tint = NeonAmber, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Weight", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
                        Text(
                            text = if (stats.weightKg != null) "${String.format("%.1f", stats.weightKg)} kg" else "Not logged for this day",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = NeonAmber
                        )
                    }
                    TextButton(onClick = { showInlineWeightEntry = !showInlineWeightEntry }) {
                        Text(if (stats.weightKg != null) "Edit" else "+ Log", color = NeonCyan)
                    }
                }

                AnimatedVisibility(visible = showInlineWeightEntry) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = weightInput,
                            onValueChange = { value ->
                                if (value.length <= 6 && value.count { it == '.' } <= 1 && value.all { it.isDigit() || it == '.' }) {
                                    weightInput = value
                                }
                            },
                            label = { Text("Weight (kg)") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f)
                        )
                        Button(
                            onClick = {
                                val w = weightInput.toDoubleOrNull()
                                if (w != null && w in 30.0..350.0) {
                                    onSaveWeight(stats.dateMillis, w)
                                    showInlineWeightEntry = false
                                    weightInput = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color.Black)
                        ) {
                            Text("Save")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text("Daily Care & Tasks", style = MaterialTheme.typography.titleMedium, color = NeonPink, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                if (stats.dailyCareTotal > 0) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${stats.dailyCareCompleted} of ${stats.dailyCareTotal} completed",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        BadgeText(
                            label = if (stats.dailyCareCompleted == stats.dailyCareTotal) "Complete" else "In Progress",
                            isDone = stats.dailyCareCompleted == stats.dailyCareTotal
                        )
                    }
                } else {
                    Text(
                        text = "No care tasks recorded for this day",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun BottomSheetStatRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = label, tint = color, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = label, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = color)
    }
}

@Composable
fun WeightDialog(onDismiss: () -> Unit, onSave: (Double) -> Unit) {
    var weightInput by remember { mutableStateOf("") }
    val weight = weightInput.toDoubleOrNull()
    val isValid = weight?.let { it.isFinite() && it in 30.0..350.0 } == true
    
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        title = {
            Text("Log Weight", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        },
        text = {
            OutlinedTextField(
                value = weightInput,
                onValueChange = { value ->
                    if (value.length <= 7 && value.count { it == '.' } <= 1 &&
                        value.all { it.isDigit() || it == '.' }
                    ) weightInput = value
                },
                label = { Text("Weight (kg)") },
                supportingText = { Text(if (isValid) "30–350 kg" else "Enter a weight from 30 to 350 kg") },
                isError = weightInput.isNotBlank() && !isValid,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    weight?.let(onSave)
                },
                enabled = isValid,
                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color.Black)
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = NeonCyan)
            }
        }
    )
}
