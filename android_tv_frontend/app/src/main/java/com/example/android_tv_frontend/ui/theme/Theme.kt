package com.example.android_tv_frontend.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val OceanPrimary = Color(0xFF2563EB)
private val OceanSecondary = Color(0xFFF59E0B)
private val OceanError = Color(0xFFEF4444)
private val OceanBackground = Color(0xFFF9FAFB)
private val OceanSurface = Color(0xFFFFFFFF)
private val OceanText = Color(0xFF111827)

private val LightColors = lightColorScheme(
    primary = OceanPrimary,
    onPrimary = Color.White,
    secondary = OceanSecondary,
    onSecondary = Color.Black,
    error = OceanError,
    onError = Color.White,
    background = OceanBackground,
    onBackground = OceanText,
    surface = OceanSurface,
    onSurface = OceanText,
    surfaceVariant = Color(0xFFEFF3FF),
    onSurfaceVariant = OceanText
)

private val DarkColors = darkColorScheme(
    primary = OceanPrimary,
    secondary = OceanSecondary,
    error = OceanError
)

/**
 * PUBLIC_INTERFACE
 * OceanTVTheme
 * Material3 theme for the app following Ocean Professional palette.
 */
@Composable
fun OceanTVTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colors: ColorScheme = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content
    )
}
