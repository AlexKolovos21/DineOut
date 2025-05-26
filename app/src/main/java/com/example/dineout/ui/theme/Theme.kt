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

private val LightColorScheme = lightColorScheme(
    primary = GreekBlue,
    onPrimary = Color.White,
    primaryContainer = GreekBlue.copy(alpha = 0.12f),
    onPrimaryContainer = GreekBlue,
    secondary = GreekBlue.copy(alpha = 0.8f),
    onSecondary = Color.White,
    secondaryContainer = GreekBlue.copy(alpha = 0.12f),
    onSecondaryContainer = GreekBlue,
    tertiary = GreekBlue.copy(alpha = 0.6f),
    onTertiary = Color.White,
    tertiaryContainer = GreekBlue.copy(alpha = 0.12f),
    onTertiaryContainer = GreekBlue,
    error = Color(0xFFB3261E),
    onError = Color.White,
    errorContainer = Color(0xFFF2B8B5),
    onErrorContainer = Color(0xFF601410),
    background = Color(0xFFFFFBFE),
    onBackground = Color(0xFF1C1B1F),
    surface = Color(0xFFFFFBFE),
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFE7E0EC),
    onSurfaceVariant = Color(0xFF49454F),
    outline = Color(0xFF79747E),
    outlineVariant = Color(0xFFCAC4D0),
    scrim = Color(0xFF000000),
    inverseSurface = Color(0xFF313033),
    inverseOnSurface = Color(0xFFF4EFF4),
    inversePrimary = GreekBlue.copy(alpha = 0.8f),
    surfaceTint = GreekBlue,
    surfaceTintColor = GreekBlue
)

private val DarkColorScheme = darkColorScheme(
    primary = GreekBlue,
    onPrimary = Color.White,
    primaryContainer = GreekBlue.copy(alpha = 0.12f),
    onPrimaryContainer = GreekBlue.copy(alpha = 0.8f),
    secondary = GreekBlue.copy(alpha = 0.8f),
    onSecondary = Color.White,
    secondaryContainer = GreekBlue.copy(alpha = 0.12f),
    onSecondaryContainer = GreekBlue.copy(alpha = 0.8f),
    tertiary = GreekBlue.copy(alpha = 0.6f),
    onTertiary = Color.White,
    tertiaryContainer = GreekBlue.copy(alpha = 0.12f),
    onTertiaryContainer = GreekBlue.copy(alpha = 0.8f),
    error = Color(0xFFF2B8B5),
    onError = Color(0xFF601410),
    errorContainer = Color(0xFF8C1D18),
    onErrorContainer = Color(0xFFF2B8B5),
    background = Color(0xFF1C1B1F),
    onBackground = Color(0xFFE6E1E5),
    surface = Color(0xFF1C1B1F),
    onSurface = Color(0xFFE6E1E5),
    surfaceVariant = Color(0xFF49454F),
    onSurfaceVariant = Color(0xFFCAC4D0),
    outline = Color(0xFF938F99),
    outlineVariant = Color(0xFF49454F),
    scrim = Color(0xFF000000),
    inverseSurface = Color(0xFFE6E1E5),
    inverseOnSurface = Color(0xFF313033),
    inversePrimary = GreekBlue.copy(alpha = 0.8f),
    surfaceTint = GreekBlue,
    surfaceTintColor = GreekBlue
)

@Composable
fun DineOutTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
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