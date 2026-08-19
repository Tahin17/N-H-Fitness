package com.aegisfit.app.presentation.screen.nutrition

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aegisfit.app.domain.model.FoodItem
import com.aegisfit.app.domain.model.MealType
import com.aegisfit.app.domain.usecase.nutrition.FoodSearchPolicy
import com.aegisfit.app.util.NutritionMath
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodSearchScreen(
    viewModel: FoodSearchViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val message by viewModel.message.collectAsStateWithLifecycle()
    val selectedMeal = viewModel.selectedMeal
    var selectedFood by remember { mutableStateOf<FoodItem?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(message) {
        message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.consumeMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Add to ${selectedMeal.displayName}")
                        Text(
                            "Fast local search, optional online refresh",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = viewModel::updateSearchQuery,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Try chicken, dal, oats…") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                supportingText = {
                    Text("Typing searches foods already stored on this device.")
                }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (searchQuery.isBlank()) "Suggested foods" else "Saved results",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                OutlinedButton(
                    onClick = viewModel::searchOnline,
                    enabled = !isRefreshing &&
                        searchQuery.trim().length >= FoodSearchPolicy.MIN_REMOTE_QUERY_LENGTH
                ) {
                    if (isRefreshing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(Icons.Default.CloudDownload, contentDescription = null)
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(if (isRefreshing) "Searching" else "Search online")
                }
            }

            Spacer(Modifier.height(12.dp))

            if (searchResults.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 48.dp, horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.Restaurant,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        if (searchQuery.isBlank()) "No suggested foods yet"
                        else "No saved food matches “$searchQuery”",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        if (searchQuery.trim().length >= FoodSearchPolicy.MIN_REMOTE_QUERY_LENGTH)
                            "Use Search online once; the results will be saved for later."
                        else
                            "Enter at least three characters to search online.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(searchResults, key = { food -> "${food.id}:${food.externalId}:${food.name}" }) { food ->
                        FoodResultItem(food = food, onClick = { selectedFood = food })
                    }
                    item { Spacer(Modifier.height(16.dp)) }
                }
            }
        }
    }

    selectedFood?.let { food ->
        ModalBottomSheet(onDismissRequest = { selectedFood = null }) {
            LogFoodSheetContent(
                food = food,
                mealType = selectedMeal,
                onLogFood = { servings ->
                    viewModel.logFood(food, servings)
                    selectedFood = null
                },
                onCancel = { selectedFood = null }
            )
        }
    }
}

@Composable
private fun FoodResultItem(food: FoodItem, onClick: () -> Unit) {
    val servingGrams = food.defaultServingSizeG.coerceAtLeast(1.0)
    val servingCalories = NutritionMath.nutrientAmount(food.caloriesPer100g, servingGrams)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(food.name, style = MaterialTheme.typography.titleMedium)
                food.brand?.takeIf(String::isNotBlank)?.let { brand ->
                    Text(
                        brand,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    "${servingCalories.roundToInt()} kcal • ${food.servingLabel()} (${servingGrams.roundToInt()} g)",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Spacer(Modifier.width(12.dp))
            AssistChip(onClick = onClick, label = { Text(food.sourceLabel()) })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LogFoodSheetContent(
    food: FoodItem,
    mealType: MealType,
    onLogFood: (Double) -> Unit,
    onCancel: () -> Unit
) {
    var servingsText by remember(food.id, food.externalId) { mutableStateOf("1") }
    val servings = servingsText.toDoubleOrNull()
    val validServings = servings != null && servings.isFinite() && servings in 0.1..20.0
    val grams = if (validServings) {
        NutritionMath.servingWeightG(food.defaultServingSizeG, servings ?: 1.0)
    } else {
        0.0
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(bottom = 32.dp)
    ) {
        Text(food.name, style = MaterialTheme.typography.headlineSmall)
        Text(
            "${food.servingLabel()} • ${food.defaultServingSizeG.roundToInt()} g per serving",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(18.dp))

        OutlinedTextField(
            value = servingsText,
            onValueChange = { value ->
                if (value.length <= 5 && value.all { it.isDigit() || it == '.' }) {
                    servingsText = value
                }
            },
            label = { Text("Servings") },
            supportingText = {
                Text(if (validServings) "${grams.roundToInt()} g total" else "Enter 0.1 to 20")
            },
            isError = servingsText.isNotBlank() && !validServings,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        if (validServings) {
            Spacer(Modifier.height(8.dp))
            Text(
                "${NutritionMath.nutrientAmount(food.caloriesPer100g, grams).roundToInt()} kcal  •  " +
                    "P ${NutritionMath.nutrientAmount(food.proteinPer100g, grams).roundToInt()} g  •  " +
                    "C ${NutritionMath.nutrientAmount(food.carbsPer100g, grams).roundToInt()} g  •  " +
                    "F ${NutritionMath.nutrientAmount(food.fatPer100g, grams).roundToInt()} g",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(Modifier.height(18.dp))
        Text(
            text = "Adding to ${mealType.displayName}",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Meal was selected from the Nutrition screen.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(24.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            TextButton(onClick = onCancel) { Text("Cancel") }
            Spacer(Modifier.width(8.dp))
            Button(
                onClick = { onLogFood(servings ?: return@Button) },
                enabled = validServings
            ) {
                Text("Add food")
            }
        }
    }
}

private fun FoodItem.sourceLabel(): String = when (source) {
    "open_food_facts" -> "Open Food Facts"
    "usda" -> "USDA"
    else -> "Saved"
}

private fun FoodItem.servingLabel(): String =
    servingDescription?.takeIf(String::isNotBlank) ?: "1 serving"
