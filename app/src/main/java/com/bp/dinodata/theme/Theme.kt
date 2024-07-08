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
    surface = MyGrey600,
    onSurface = MyGrey50,
    primary = MyGrey700,
    onSurfaceVariant = HighlightYellow
)

private val LightColorScheme = lightColorScheme(
    background = MyGrey50,
    onBackground = MyGrey900,
    surface = MyGrey400,
    onSurface = Color.White,
    primary = MyGrey400,
    onSurfaceVariant = HighlightYellowLight
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