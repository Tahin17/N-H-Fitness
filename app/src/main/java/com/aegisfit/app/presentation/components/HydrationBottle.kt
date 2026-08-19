package com.aegisfit.app.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke

@Composable
fun HydrationBottle(
    fillPercentage: Float,
    modifier: Modifier = Modifier
) {
    val animatedFill by animateFloatAsState(
        targetValue = fillPercentage.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 1000),
        label = "WaterFillAnimation"
    )

    val bottleColor = MaterialTheme.colorScheme.outline
    val waterColor = MaterialTheme.colorScheme.primary

    Box(
        modifier = modifier.aspectRatio(0.4f)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val cornerRadius = CornerRadius(width * 0.2f, width * 0.2f)

            // Draw Water inside the bottle
            val waterHeight = height * animatedFill
            drawRoundRect(
                color = waterColor,
                topLeft = Offset(0f, height - waterHeight),
                size = Size(width, waterHeight),
                cornerRadius = cornerRadius
            )

            // Draw Bottle Outline
            drawRoundRect(
                color = bottleColor,
                topLeft = Offset(0f, 0f),
                size = Size(width, height),
                cornerRadius = cornerRadius,
                style = Stroke(width = 8f)
            )

            // Draw Bottle Cap
            val capWidth = width * 0.4f
            val capHeight = height * 0.1f
            drawRoundRect(
                color = bottleColor,
                topLeft = Offset((width - capWidth) / 2, -capHeight),
                size = Size(capWidth, capHeight),
                cornerRadius = CornerRadius(capWidth * 0.1f, capWidth * 0.1f)
            )
        }
    }
}
