package com.aegisfit.app.presentation.screen.nutrition

import androidx.lifecycle.compose.collectAsStateWithLifecycle

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aegisfit.app.domain.model.MealType
import com.aegisfit.app.domain.model.FoodLog
import com.aegisfit.app.presentation.components.HydrationBottle
import com.aegisfit.app.presentation.components.MacroRingChart
import com.aegisfit.app.util.NutritionMath
import kotlin.math.roundToInt

@Composable
fun NutritionScreen(
    onNavigateToSearch: (MealType) -> Unit = {},
    viewModel: NutritionViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Nutrition",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            state.error?.let { message ->
                item {
                    Text(
                        message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            
            if (state.isLoading) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            } else {
                item {
                    state.summary?.let { summary ->
                        ElevatedCard {
                            val targetCal = summary.targetCalories.coerceAtLeast(1)
                            val proteinGoal = (targetCal * 0.3 / 4).coerceAtLeast(1.0)
                            val carbsGoal = (targetCal * 0.4 / 4).coerceAtLeast(1.0)
                            val fatGoal = (targetCal * 0.3 / 9).coerceAtLeast(1.0)

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                MacroRingChart(
                                    proteinProgress = (summary.totalProtein / proteinGoal).toFloat(),
                                    carbsProgress = (summary.totalCarbs / carbsGoal).toFloat(),
                                    fatProgress = (summary.totalFat / fatGoal).toFloat(),
                                    modifier = Modifier
                                        .size(160.dp)
                                        .padding(8.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "${summary.totalCalories} / ${summary.targetCalories} kcal",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "P: ${summary.totalProtein.toInt()}g | C: ${summary.totalCarbs.toInt()}g | F: ${summary.totalFat.toInt()}g",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                item {
                    MealSection(
                        title = "Breakfast",
                        logs = state.foodLogs.filter { it.mealType == MealType.Breakfast.name },
                        onAddClick = { onNavigateToSearch(MealType.Breakfast) }
                    )
                }

                item {
                    MealSection(
                        title = "Lunch",
                        logs = state.foodLogs.filter { it.mealType == MealType.Lunch.name },
                        onAddClick = { onNavigateToSearch(MealType.Lunch) }
                    )
                }

                item {
                    MealSection(
                        title = "Dinner",
                        logs = state.foodLogs.filter { it.mealType == MealType.Dinner.name },
                        onAddClick = { onNavigateToSearch(MealType.Dinner) }
                    )
                }

                item {
                    MealSection(
                        title = "Snacks",
                        logs = state.foodLogs.filter { it.mealType == MealType.Snack.name },
                        onAddClick = { onNavigateToSearch(MealType.Snack) }
                    )
                }
                
                item {
                    ElevatedCard {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Hydration",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            HydrationBottle(
                                fillPercentage = (state.waterTotalMl / 3000f).coerceIn(0f, 1f),
                                modifier = Modifier.height(150.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { viewModel.addWater(250) }) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Water")
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Add 250ml")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MealSection(
    title: String,
    logs: List<FoodLog>,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                IconButton(onClick = onAddClick) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add $title",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            if (logs.isEmpty()) {
                Text(
                    "Nothing logged yet",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                logs.forEachIndexed { index, log ->
                    if (index > 0) HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
                    val grams = NutritionMath.servingWeightG(
                        log.foodItem.defaultServingSizeG,
                        log.servings
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(log.foodItem.name, style = MaterialTheme.typography.bodyLarge)
                            Text(
                                "${log.servings.formatServing()} × ${log.foodItem.servingDescription ?: "serving"}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            "${NutritionMath.nutrientAmount(log.foodItem.caloriesPer100g, grams).roundToInt()} kcal",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }
    }
}

private fun Double.formatServing(): String =
    if (this % 1.0 == 0.0) toInt().toString() else "%.1f".format(this)


