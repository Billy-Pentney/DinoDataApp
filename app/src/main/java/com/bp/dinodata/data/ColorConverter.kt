package com.bp.dinodata.data

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color
import com.bp.dinodata.theme.MyGrey400

object ColorConverter {


    private val redTheme = darkColorScheme(
        surface = Color(0xFF882222)
    )

    private val orangeTheme = darkColorScheme(
        surface = Color(0xFF884422)
    )

    private val yellowTheme = darkColorScheme(
        surface = Color(0xFF884422)
    )

    private val greenTheme = darkColorScheme(
        surface = Color(0xFF228822)
    )

    private val cyanTheme = darkColorScheme(
        surface = Color(0xFF228899)
    )

    private val blueTheme = darkColorScheme(
        surface = Color(0xFF224488)
    )

    private val violetTheme = darkColorScheme(
        surface = Color(0xFF882288)
    )

    private val pinkTheme = darkColorScheme(
        surface = Color(0xFFAA5555)
    )

    private val mapping = mapOf(
        "RED" to redTheme,
        "ORANGE" to orangeTheme,
        "YELLOW" to yellowTheme,
        "GREEN" to greenTheme,
        "CYAN" to cyanTheme,
        "BLUE" to blueTheme,
        "VIOLET" to violetTheme,
//        "BROWN" to Color(0xFF444400),
//        "PINK" to Color(0xFFFF9999)
    )

    fun convertStringToTheme(name: String?): ColorScheme? {
        return mapping[name]
    }
}