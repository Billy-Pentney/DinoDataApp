package com.bp.dinodata.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Material 3 color schemes
private val DarkColorScheme = darkColorScheme(
    background = MyGrey800,
    onBackground = MyGrey50,
    surface = MyGrey700,
    surfaceVariant = MyGrey600,
    onSurface = MyGrey50,
    primary = MyGrey500,
    onPrimary = MyGrey100,
    scrim = HighlightYellow,
    onSurfaceVariant = Color.White,
    secondary = MyGrey800,
    onSecondary = Color.White,
    primaryContainer = MyGrey700,
    onPrimaryContainer = Color.White,
    secondaryContainer = MyGrey800,
    onSecondaryContainer = MyGrey100
)

private val LightColorScheme = lightColorScheme(
    background = MyGrey50,
    onBackground = MyGrey900,
    surface = MyGrey300,
    surfaceVariant = MyGrey500,
    onSurfaceVariant = Color.White,
    onSurface = MyGrey800,
    primary = MyGrey400,
    onPrimary = MyGrey50,
    scrim = HighlightYellowLight,
    secondary = MyGrey600,
    onSecondary = Color.White,
    primaryContainer = MyGrey400,
    secondaryContainer = MyGrey100,
    onPrimaryContainer = MyGrey50,
    onSecondaryContainer = Color.White
)

@Composable
fun DinoDataTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val myColorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    MaterialTheme(
        colorScheme = myColorScheme,
        content = content
    )
}