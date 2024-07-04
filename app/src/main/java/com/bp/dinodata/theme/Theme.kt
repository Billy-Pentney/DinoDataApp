package com.bp.dinodata.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Material 3 color schemes
private val DarkColorScheme = darkColorScheme()
//    primary = Purple80,
//    secondary = PurpleGrey80,
//    tertiary = Pink80
//)
//
private val LightColorScheme = lightColorScheme()
//    primary = Purple40,
//    secondary = PurpleGrey40,
//    tertiary = Pink40
//)

@Composable
fun DinoDataTheme(
    darkTheme: Boolean = false,
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