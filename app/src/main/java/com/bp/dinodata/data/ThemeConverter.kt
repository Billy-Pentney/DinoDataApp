package com.bp.dinodata.data

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color

object ThemeConverter {


    private val redTheme = darkColorScheme(
        surface = Color(0xFF882222)
    )

    private val orangeTheme = darkColorScheme(
        surface = Color(0xFF884422)
    )

    private val yellowTheme = darkColorScheme(
        surface = Color(0xFFAA7744)
    )

    private val greenTheme = darkColorScheme(
        surface = Color(0xFF337733)
    )

    private val cyanTheme = darkColorScheme(
        surface = Color(0xFF338899)
    )

    private val blueTheme = darkColorScheme(
        surface = Color(0xFF336699)
    )

    private val indigoTheme = darkColorScheme(
        surface = Color(0xFF554488)
    )

    private val violetTheme = darkColorScheme(
        surface = Color(0xFF884488)
    )

    private val pinkTheme = darkColorScheme(
        surface = Color(0xFFAA5555)
    )

    private val stringToTheme = mapOf(
        "RED" to redTheme,
        "ORANGE" to orangeTheme,
        "YELLOW" to yellowTheme,
        "GREEN" to greenTheme,
        "CYAN" to cyanTheme,
        "BLUE" to blueTheme,
        "INDIGO" to indigoTheme,
        "VIOLET" to violetTheme,
        "PINK" to pinkTheme
    )

    val listOfColors = stringToTheme.keys.toList()

    fun getTheme(name: String?): ColorScheme? {
        return stringToTheme[name]
    }

    fun getColor(colorName: String): Color? {
        return stringToTheme[colorName]?.surface
    }
}