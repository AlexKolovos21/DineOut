package com.example.dineout.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Greek-inspired colors
val GreekBlue = Color(0xFF0D5EAF) // Greek flag blue
val GreekWhite = Color(0xFFF9F9F9) // Greek flag white
val GreekCream = Color(0xFFF5F0E1) // Sand/stone color
val GreekTurquoise = Color(0xFF5BC0BE) // Aegean sea color
val GreekOlive = Color(0xFF7D8C38) // Olive green
val GreekTerracotta = Color(0xFFD35400) // Greek pottery
val GreekGold = Color(0xFFDAA520) // Byzantine gold

private val DarkColorScheme = darkColorScheme(
    primary = GreekBlue,
    onPrimary = GreekWhite,
    primaryContainer = GreekBlue.copy(alpha = 0.8f),
    onPrimaryContainer = GreekWhite,
    secondary = GreekTurquoise,
    onSecondary = Color.Black,
    secondaryContainer = GreekTurquoise.copy(alpha = 0.7f),
    onSecondaryContainer = Color.Black,
    tertiary = GreekOlive,
    onTertiary = GreekWhite,
    tertiaryContainer = GreekOlive.copy(alpha = 0.7f),
    onTertiaryContainer = GreekWhite,
    background = Color(0xFF121212),
    onBackground = GreekWhite,
    surface = Color(0xFF1E1E1E),
    onSurface = GreekWhite,
    error = GreekTerracotta,
    onError = GreekWhite
)

private val LightColorScheme = lightColorScheme(
    primary = GreekBlue,
    onPrimary = GreekWhite,
    primaryContainer = GreekBlue.copy(alpha = 0.1f),
    onPrimaryContainer = GreekBlue,
    secondary = GreekTurquoise,
    onSecondary = Color.White,
    secondaryContainer = GreekTurquoise.copy(alpha = 0.1f),
    onSecondaryContainer = GreekTurquoise,
    tertiary = GreekOlive,
    onTertiary = Color.White,
    tertiaryContainer = GreekOlive.copy(alpha = 0.1f),
    onTertiaryContainer = GreekOlive,
    background = GreekCream,
    onBackground = Color.Black,
    surface = GreekWhite,
    onSurface = Color.Black,
    error = GreekTerracotta,
    onError = Color.White,
    surfaceVariant = GreekCream.copy(alpha = 0.7f)
)

@Composable
fun DineOutTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}