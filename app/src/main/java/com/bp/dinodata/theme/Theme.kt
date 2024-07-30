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
    onSurface = MyGrey50,
    primary = MyGrey500,
    onPrimary = MyGrey100,
    scrim = HighlightYellow,
    onSurfaceVariant = MyGrey200,
    secondary = MyGrey800,
    onSecondary = Color.White
)

private val LightColorScheme = lightColorScheme(
    background = MyGrey50,
    onBackground = MyGrey900,
    surface = MyGrey200,
    onSurface = MyGrey800,
    primary = MyGrey400,
    onPrimary = MyGrey50,
    scrim = HighlightYellowLight,
    onSurfaceVariant = MyGrey100,
    secondary = MyGrey600,
    onSecondary = Color.White
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