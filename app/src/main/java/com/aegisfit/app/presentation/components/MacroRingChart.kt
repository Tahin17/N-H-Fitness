package com.aegisfit.app.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun MacroRingChart(
    proteinProgress: Float,
    carbsProgress: Float,
    fatProgress: Float,
    modifier: Modifier = Modifier,
    strokeWidth: Dp = 12.dp,
    gapWidth: Dp = 4.dp
) {
    val proteinColor = MaterialTheme.colorScheme.secondary
    val carbsColor = MaterialTheme.colorScheme.tertiary
    val fatColor = MaterialTheme.colorScheme.primary
    val backgroundColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f)

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(strokeWidth / 2)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidthPx = strokeWidth.toPx()
            val gapWidthPx = gapWidth.toPx()
            val totalThickness = strokeWidthPx + gapWidthPx

            // Center of the canvas
            val center = Offset(size.width / 2, size.height / 2)

            // Radius for each ring
            val outerRadius = size.width / 2
            val middleRadius = outerRadius - totalThickness
            val innerRadius = middleRadius - totalThickness

            // Draw Background Rings
            drawCircle(
                color = backgroundColor,
                radius = outerRadius,
                center = center,
                style = Stroke(width = strokeWidthPx)
            )
            drawCircle(
                color = backgroundColor,
                radius = middleRadius,
                center = center,
                style = Stroke(width = strokeWidthPx)
            )
            drawCircle(
                color = backgroundColor,
                radius = innerRadius,
                center = center,
                style = Stroke(width = strokeWidthPx)
            )

            // Draw Progress Arcs
            // Protein (Outer Ring) - Neon Pink
            drawArc(
                color = proteinColor,
                startAngle = -90f,
                sweepAngle = 360f * proteinProgress.coerceIn(0f, 1f),
                useCenter = false,
                topLeft = Offset(center.x - outerRadius, center.y - outerRadius),
                size = Size(outerRadius * 2, outerRadius * 2),
                style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
            )

            // Carbs (Middle Ring) - Neon Amber
            drawArc(
                color = carbsColor,
                startAngle = -90f,
                sweepAngle = 360f * carbsProgress.coerceIn(0f, 1f),
                useCenter = false,
                topLeft = Offset(center.x - middleRadius, center.y - middleRadius),
                size = Size(middleRadius * 2, middleRadius * 2),
                style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
            )

            // Fat (Inner Ring) - Neon Cyan
            drawArc(
                color = fatColor,
                startAngle = -90f,
                sweepAngle = 360f * fatProgress.coerceIn(0f, 1f),
                useCenter = false,
                topLeft = Offset(center.x - innerRadius, center.y - innerRadius),
                size = Size(innerRadius * 2, innerRadius * 2),
                style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
            )
        }
    }
}
