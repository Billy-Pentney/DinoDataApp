package com.bp.dinodata.presentation.utils

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color
import com.bp.dinodata.data.DataParsing.getLongestNonMatchingSuffixes
import com.bp.dinodata.data.search.IGeneratesSearchSuggestions
import com.bp.dinodata.data.search.ISearchTypeConverter
import com.bp.dinodata.theme.MyGrey800


object ThemeConverter: ISearchTypeConverter<String> {

    private val redTheme = darkColorScheme(
        surface = Color(0xFF882222)
    )

    private val orangeTheme = darkColorScheme(
        surface = Color(0xFF884422)
    )

    private val yellowTheme = darkColorScheme(
        surface = Color(0xFFAA9944)
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

    private val brownTheme = darkColorScheme(
        surface = Color(0xFF885544)
    )

    private val whiteTheme = darkColorScheme(
        surface = Color(0xFFFFFFFF),
        onSurface = MyGrey800
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
        "PINK" to pinkTheme,
        "BROWN" to brownTheme,
        "WHITE" to whiteTheme
    )

    val listOfColors = stringToTheme.keys.toList()

    fun getTheme(name: String?): ColorScheme? {
        return stringToTheme[name?.uppercase()]
    }

    fun getColor(colorName: String?): Color? {
        return stringToTheme[colorName?.uppercase()]?.surface
    }

    override fun matchType(text: String): String? {
        val upperText = text.uppercase()
        if (getTheme(upperText) != null) {
            return upperText
        }
        return null
    }

    override fun suggestSearchSuffixes(text: String, takeTop: Int): List<String> {
        val keysByCommonPrefix = getLongestNonMatchingSuffixes(text, listOfColors)
        return keysByCommonPrefix.take(takeTop.coerceAtLeast(1))
    }
}