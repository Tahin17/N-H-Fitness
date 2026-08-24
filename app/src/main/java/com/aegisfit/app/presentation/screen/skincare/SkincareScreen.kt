package com.aegisfit.app.presentation.screen.skincare

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aegisfit.app.domain.model.CareCategory
import com.aegisfit.app.domain.model.CareItem
import com.aegisfit.app.domain.model.CareTimeSlot
import com.aegisfit.app.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkincareScreen(
    viewModel: SkincareViewModel = hiltViewModel(),
    onNavigateToTips: () -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = AegisDarkBackground,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.openAddDialog() },
                containerColor = NeonCyan,
                contentColor = Color.Black,
                icon = { Icon(imageVector = Icons.Filled.Add, contentDescription = "Add Item") },
                text = { Text("Add Item", fontWeight = FontWeight.Bold) }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            // Title Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Daily Care",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Text(
                        text = "Routines, medicines, hygiene & daily tasks",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progress Summary Card
            CareProgressCard(
                completed = state.completedCount,
                total = state.totalCount,
                progress = state.progress
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Filter Chips
            TimeSlotFilterRow(
                selectedFilter = state.selectedTimeSlotFilter,
                onSelectFilter = { viewModel.selectFilter(it) }
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (state.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = NeonCyan)
                }
            } else if (state.items.isEmpty()) {
                // Empty State
                EmptyCareStateCard(onAddClick = { viewModel.openAddDialog() })
            } else {
                // Time Slot Sections
                val slotsToDisplay = if (state.selectedTimeSlotFilter != null) {
                    listOf(state.selectedTimeSlotFilter!!)
                } else {
                    CareTimeSlot.entries
                }

                slotsToDisplay.forEach { slot ->
                    val slotItems = state.items.filter { it.timeSlot == slot }
                    if (slotItems.isNotEmpty() || state.selectedTimeSlotFilter == slot) {
                        CareTimeSlotSection(
                            timeSlot = slot,
                            items = slotItems,
                            onToggleItem = { id, done -> viewModel.toggleItem(id, done) },
                            onDeleteItem = { item -> viewModel.setItemPendingDelete(item) }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(80.dp)) // Space for FAB
        }
    }

    // Add Item Dialog / BottomSheet
    if (state.showAddDialog) {
        AddCareItemDialog(
            onDismiss = { viewModel.dismissAddDialog() },
            onSave = { title, slot, category, notes, timeHint ->
                viewModel.addItem(title, slot, category, notes, timeHint)
            }
        )
    }

    // Delete Confirmation Dialog
    if (state.itemPendingDelete != null) {
        val item = state.itemPendingDelete!!
        AlertDialog(
            onDismissRequest = { viewModel.setItemPendingDelete(null) },
            containerColor = AegisDarkSurface,
            title = {
                Text(
                    text = "Delete Care Item?",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to remove \"${item.title}\" from your daily care routine?",
                    color = Color.LightGray
                )
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.deleteItem(item.id) },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonRed, contentColor = Color.White)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.setItemPendingDelete(null) }) {
                    Text("Cancel", color = NeonCyan)
                }
            }
        )
    }
}

@Composable
private fun CareProgressCard(completed: Int, total: Int, progress: Float) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Today's Completion",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = if (total > 0) "$completed / $total" else "0 items",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (total > 0 && completed == total) NeonGreen else NeonCyan
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = if (total > 0 && completed == total) NeonGreen else NeonCyan,
                trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = when {
                    total == 0 -> "Add items to start tracking your daily care"
                    completed == total -> "✨ All daily care completed for today!"
                    else -> "${total - completed} task(s) remaining today • Resets daily"
                },
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun TimeSlotFilterRow(
    selectedFilter: CareTimeSlot?,
    onSelectFilter: (CareTimeSlot?) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        item {
            FilterChip(
                selected = selectedFilter == null,
                onClick = { onSelectFilter(null) },
                label = { Text("All") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = NeonCyan,
                    selectedLabelColor = Color.Black
                )
            )
        }
        items(CareTimeSlot.entries) { slot ->
            val iconStr = when (slot) {
                CareTimeSlot.MORNING -> "🌅"
                CareTimeSlot.AFTERNOON -> "☀️"
                CareTimeSlot.EVENING -> "🌇"
                CareTimeSlot.NIGHT -> "🌙"
            }
            FilterChip(
                selected = selectedFilter == slot,
                onClick = { onSelectFilter(if (selectedFilter == slot) null else slot) },
                label = { Text("$iconStr ${slot.displayName}") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = getSlotColor(slot),
                    selectedLabelColor = Color.Black
                )
            )
        }
    }
}

@Composable
private fun CareTimeSlotSection(
    timeSlot: CareTimeSlot,
    items: List<CareItem>,
    onToggleItem: (Long, Boolean) -> Unit,
    onDeleteItem: (CareItem) -> Unit
) {
    val slotColor = getSlotColor(timeSlot)
    val slotIcon = when (timeSlot) {
        CareTimeSlot.MORNING -> "🌅"
        CareTimeSlot.AFTERNOON -> "☀️"
        CareTimeSlot.EVENING -> "🌇"
        CareTimeSlot.NIGHT -> "🌙"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = slotIcon, fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${timeSlot.displayName} Care",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = slotColor
                    )
                }
                val completedInSlot = items.count { it.isCompleted }
                Text(
                    text = "$completedInSlot/${items.size}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (items.isEmpty()) {
                Text(
                    text = "No items in ${timeSlot.displayName.lowercase()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            } else {
                items.forEach { item ->
                    DailyCareItemRow(
                        item = item,
                        accentColor = slotColor,
                        onCheckedChange = { onToggleItem(item.id, it) },
                        onDeleteClick = { onDeleteItem(item) }
                    )
                    if (item != items.last()) {
                        Divider(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DailyCareItemRow(
    item: CareItem,
    accentColor: Color,
    onCheckedChange: (Boolean) -> Unit,
    onDeleteClick: () -> Unit
) {
    val categoryIcon = when (item.category) {
        CareCategory.MEDICINE -> "💊"
        CareCategory.SKINCARE -> "✨"
        CareCategory.HYGIENE -> "🚿"
        CareCategory.HABIT -> "🎯"
        CareCategory.TASK -> "📋"
        CareCategory.OTHER -> "🏷️"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!item.isCompleted) }
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = item.isCompleted,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = accentColor,
                uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (item.isCompleted) Color.Gray else Color.White,
                        textDecoration = if (item.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                // Category badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(accentColor.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "$categoryIcon ${item.category.displayName}",
                        style = MaterialTheme.typography.labelSmall,
                        color = accentColor,
                        fontSize = 10.sp
                    )
                }
            }

            if (!item.notes.isNullOrBlank() || !item.timeHint.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (!item.timeHint.isNullOrBlank()) {
                        Text(
                            text = "⏰ ${item.timeHint} ",
                            style = MaterialTheme.typography.bodySmall,
                            color = NeonAmber,
                            fontSize = 11.sp
                        )
                    }
                    if (!item.notes.isNullOrBlank()) {
                        Text(
                            text = item.notes,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        IconButton(
            onClick = onDeleteClick,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.DeleteOutline,
                contentDescription = "Delete item",
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun EmptyCareStateCard(onAddClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .padding(28.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(NeonCyan.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = "Care Checklist",
                    tint = NeonCyan,
                    modifier = Modifier.size(36.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No Daily Care Items Yet",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Add medicines, facewash, showers, routines, or daily tasks for Morning, Afternoon, Evening, or Night. Checked items stay in your list and automatically reset daily.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = onAddClick,
                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color.Black)
            ) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Add First Care Item", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddCareItemDialog(
    onDismiss: () -> Unit,
    onSave: (String, CareTimeSlot, CareCategory, String?, String?) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedSlot by remember { mutableStateOf(CareTimeSlot.MORNING) }
    var selectedCategory by remember { mutableStateOf(CareCategory.MEDICINE) }
    var notes by remember { mutableStateOf("") }
    var timeHint by remember { mutableStateOf("") }

    val isValid = title.trim().isNotBlank()

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = AegisDarkSurface,
        title = {
            Text("Add Daily Care Item", fontWeight = FontWeight.Bold, color = Color.White)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Item Title Input
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Item Name (e.g. Facewash, Medicine, Shower)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Time Slot Selector
                Text("Time of Day", style = MaterialTheme.typography.labelMedium, color = Color.LightGray)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    CareTimeSlot.entries.forEach { slot ->
                        val isSelected = selectedSlot == slot
                        val iconStr = when (slot) {
                            CareTimeSlot.MORNING -> "🌅"
                            CareTimeSlot.AFTERNOON -> "☀️"
                            CareTimeSlot.EVENING -> "🌇"
                            CareTimeSlot.NIGHT -> "🌙"
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) getSlotColor(slot) else MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { selectedSlot = slot }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(iconStr, fontSize = 14.sp)
                                Text(
                                    text = slot.displayName.take(3),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color.Black else Color.White
                                )
                            }
                        }
                    }
                }

                // Category Selector
                Text("Category / Type", style = MaterialTheme.typography.labelMedium, color = Color.LightGray)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(CareCategory.entries) { cat ->
                        val isSelected = selectedCategory == cat
                        val iconStr = when (cat) {
                            CareCategory.MEDICINE -> "💊"
                            CareCategory.SKINCARE -> "✨"
                            CareCategory.HYGIENE -> "🚿"
                            CareCategory.HABIT -> "🎯"
                            CareCategory.TASK -> "📋"
                            CareCategory.OTHER -> "🏷️"
                        }
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCategory = cat },
                            label = { Text("$iconStr ${cat.displayName}") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = NeonCyan,
                                selectedLabelColor = Color.Black
                            )
                        )
                    }
                }

                // Notes / Instructions Input
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes / Dosage (optional e.g. 1 pill with meal)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Time Hint Input
                OutlinedTextField(
                    value = timeHint,
                    onValueChange = { timeHint = it },
                    label = { Text("Time (optional e.g. 8:00 AM, 9:00 PM)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (isValid) {
                        onSave(title, selectedSlot, selectedCategory, notes, timeHint)
                    }
                },
                enabled = isValid,
                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color.Black)
            ) {
                Text("Save Item")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = NeonCyan)
            }
        }
    )
}

private fun getSlotColor(slot: CareTimeSlot): Color {
    return when (slot) {
        CareTimeSlot.MORNING -> NeonPink
        CareTimeSlot.AFTERNOON -> NeonAmber
        CareTimeSlot.EVENING -> NeonPurple
        CareTimeSlot.NIGHT -> NeonCyan
    }
}
