package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = SimXCyanAccent,
    onPrimary = Color(0xFF031633),
    primaryContainer = SimXBluePrimary,
    onPrimaryContainer = Color(0xFFD6E3FF),
    secondary = SimXGoldLight,
    onSecondary = Color(0xFF3B2D00),
    secondaryContainer = SimXGoldDark,
    onSecondaryContainer = Color(0xFFFFECC2),
    tertiary = SimXCyanAccent,
    background = SimXDarkBackground,
    onBackground = SimXDarkTextPrimary,
    surface = SimXDarkSurface,
    onSurface = SimXDarkTextPrimary,
    surfaceVariant = SimXDarkSurfaceVariant,
    onSurfaceVariant = SimXDarkTextSecondary,
    outline = SimXDarkBorder,
    error = SimXError
)

private val LightColorScheme = lightColorScheme(
    primary = SimXBluePrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDCE2FF),
    onPrimaryContainer = Color(0xFF001550),
    secondary = SimXGoldDark,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFECC2),
    onSecondaryContainer = Color(0xFF261900),
    tertiary = SimXBlueDark,
    background = SimXLightBackground,
    onBackground = SimXLightTextPrimary,
    surface = SimXLightSurface,
    onSurface = SimXLightTextPrimary,
    surfaceVariant = SimXLightSurfaceVariant,
    onSurfaceVariant = SimXLightTextSecondary,
    outline = SimXLightBorder,
    error = SimXError
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Use Sim X custom branded palette by default
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
