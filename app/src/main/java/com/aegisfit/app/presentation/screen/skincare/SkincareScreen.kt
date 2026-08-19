package com.aegisfit.app.presentation.screen.skincare

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aegisfit.app.presentation.theme.AegisDarkBackground
import com.aegisfit.app.presentation.theme.AegisDarkSurface
import com.aegisfit.app.presentation.theme.NeonPink
import com.aegisfit.app.presentation.theme.NeonPurple

val NeonAmber = Color(0xFFFFB300)

@Composable
fun NeonCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        AegisDarkSurface.copy(alpha = 0.8f),
                        AegisDarkSurface.copy(alpha = 0.4f)
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(NeonPink.copy(alpha = 0.5f), NeonPurple.copy(alpha = 0.5f))
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp),
        content = content
    )
}

@Composable
fun SkincareStepItem(item: RoutineItem, onCheckedChange: (Boolean) -> Unit, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Checkbox(
            checked = item.isCompleted,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = color,
                uncheckedColor = Color.Gray
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.padding(top = 2.dp)) {
            Text(text = item.name, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, color = Color.White))
            Text(text = item.category, style = MaterialTheme.typography.bodyMedium.copy(color = Color.LightGray))
            
            if (!item.instructions.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = item.instructions, style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.7f)))
            }
            if (!item.dosage.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = "Dosage: ${item.dosage}", style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.7f)))
            }
            if (!item.warning.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = "Warning: ${item.warning}", style = MaterialTheme.typography.bodySmall.copy(color = NeonAmber))
            }
        }
    }
}

@Composable
fun SkincareScreen(
    viewModel: SkincareViewModel = hiltViewModel(),
    onNavigateToTips: () -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    val dailyTips = listOf(
        "Apply sunscreen every morning, even on cloudy days.",
        "Don't forget to moisturize your neck and hands.",
        "Exfoliate 1-2 times a week for glowing skin."
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AegisDarkBackground)
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Text(
            text = "Skincare Routine",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Progress bar
        LinearProgressIndicator(
            progress = { state.completionStatus },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = NeonPink,
            trackColor = AegisDarkSurface
        )
        
        Spacer(modifier = Modifier.height(24.dp))

        // AM Routine
        NeonCard {
            Text(
                text = "Morning Routine (AM)",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = NeonPink
                ),
                modifier = Modifier.padding(bottom = 12.dp)
            )
            state.amRoutine.forEach { item ->
                SkincareStepItem(
                    item = item,
                    onCheckedChange = { isChecked -> viewModel.toggleAmRoutine(item.id, isChecked) },
                    color = NeonPink
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // PM Routine
        NeonCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Evening Routine (PM)",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = NeonPurple
                        )
                    )
                    Text(
                        text = if (state.isNightA) "Night A: Pore Exfoliation" else "Night B: Barrier Recovery",
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.LightGray)
                    )
                }
                TextButton(onClick = { viewModel.toggleNight() }) {
                    Text("Toggle Night", color = NeonPurple)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            state.pmRoutine.forEach { item ->
                SkincareStepItem(
                    item = item,
                    onCheckedChange = { isChecked -> viewModel.togglePmRoutine(item.id, isChecked) },
                    color = NeonPurple
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Daily Tips Carousel
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Daily Skin Tips",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            )
            TextButton(onClick = onNavigateToTips) {
                Text("See All", color = NeonPink)
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(dailyTips) { tip ->
                NeonCard(
                    modifier = Modifier.width(240.dp)
                ) {
                    Text(
                        text = tip,
                        color = Color.White,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

