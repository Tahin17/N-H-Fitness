package com.aegisfit.app.presentation.screen.skincare

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aegisfit.app.presentation.theme.AegisDarkBackground
import com.aegisfit.app.presentation.theme.AegisDarkSurface
import com.aegisfit.app.presentation.theme.NeonPink
import com.aegisfit.app.presentation.theme.NeonPurple

@Composable
private fun TipsNeonCard(
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkinTipsScreen(
    onNavigateBack: () -> Unit = {}
) {
    val allTips = listOf(
        "Apply sunscreen every morning, even on cloudy days.",
        "Don't forget to moisturize your neck and hands.",
        "Exfoliate 1-2 times a week for glowing skin.",
        "Drink plenty of water to keep your skin hydrated.",
        "Get at least 7-8 hours of sleep for cell repair.",
        "Always remove your makeup before going to bed.",
        "Use a silk pillowcase to prevent wrinkles and breakouts."
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("All Skin Tips", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AegisDarkBackground
                )
            )
        },
        containerColor = AegisDarkBackground
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
            items(allTips) { tip ->
                TipsNeonCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "✨",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(end = 12.dp)
                        )
                        Text(
                            text = tip,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
