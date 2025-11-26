package com.example.projecto_prueba_final.ui.theme

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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = NeonGreen,
    secondary = VibrantMagenta,
    background = DarkBackground,
    surface = DarkSurface,
    onPrimary = DarkBackground,
    onSecondary = DarkBackground,
    onBackground = OffWhite,
    onSurface = OffWhite
)

private val LightColorScheme = lightColorScheme(
    primary = BrightBlue,
    secondary = VibrantMagenta,
    background = LightBackground,
    surface = LightSurface,
    onPrimary = LightSurface,
    onSecondary = LightBackground,
    onBackground = DarkCharcoal,
    onSurface = DarkCharcoal
)

@Composable
fun projecto_prueba_finalTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}