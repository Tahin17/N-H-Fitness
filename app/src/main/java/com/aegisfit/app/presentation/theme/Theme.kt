package com.aegisfit.app.presentation.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val AegisDarkColorScheme = darkColorScheme(
    primary = NeonCyan,
    secondary = NeonPurple,
    tertiary = NeonGreen,
    error = NeonRed,
    background = AegisDarkBackground,
    surface = AegisDarkSurface,
    surfaceVariant = AegisDarkSurfaceVariant,
    onBackground = AegisDarkOnSurface,
    onSurface = AegisDarkOnSurface,
    onSurfaceVariant = AegisDarkOnSurfaceVariant,
    outline = NhtOutline,
    outlineVariant = NhtOutlineVariant,
    onPrimary = AegisDarkBackground,
    onSecondary = AegisDarkBackground,
    onTertiary = AegisDarkBackground,
    onError = AegisDarkBackground,
    surfaceContainer = AegisDarkSurface
)

@Composable
fun NhtFitnessTheme(content: @Composable () -> Unit) {
    val colorScheme = AegisDarkColorScheme
    val view = LocalView.current
    
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = false
                isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
