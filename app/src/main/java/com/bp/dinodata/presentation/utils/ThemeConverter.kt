package com.bp.dinodata.presentation.utils

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color
import com.bp.dinodata.data.DataParsing.getLongestPotentialSuffixes
import com.bp.dinodata.data.search.ISearchTypeConverter
import com.bp.dinodata.theme.MyGrey100
import com.bp.dinodata.theme.MyGrey800
import com.bp.dinodata.theme.brown
import com.bp.dinodata.theme.burgundy
import com.bp.dinodata.theme.cyan
import com.bp.dinodata.theme.forestGreen
import com.bp.dinodata.theme.indigo
import com.bp.dinodata.theme.lime
import com.bp.dinodata.theme.orange
import com.bp.dinodata.theme.pink
import com.bp.dinodata.theme.red
import com.bp.dinodata.theme.royalBlue
import com.bp.dinodata.theme.steelBlue
import com.bp.dinodata.theme.violet
import com.bp.dinodata.theme.yellow


object ThemeConverter: ISearchTypeConverter<String> {

    private val redTheme = darkColorScheme(
        surface = red
    )

    private val orangeTheme = darkColorScheme(
        surface = orange
    )

    private val yellowTheme = darkColorScheme(
        surface = yellow
    )

    private val greenTheme = darkColorScheme(
        surface = forestGreen
    )

    private val cyanTheme = darkColorScheme(
        surface = cyan
    )

    private val blueTheme = darkColorScheme(
        surface = royalBlue
    )

    private val indigoTheme = darkColorScheme(
        surface = indigo
    )

    private val violetTheme = darkColorScheme(
        surface = violet
    )

    private val pinkTheme = darkColorScheme(
        surface = pink
    )

    private val brownTheme = darkColorScheme(
        surface = brown
    )

    private val whiteTheme = darkColorScheme(
        surface = Color(0xFFDDDDDD),
        onSurface = MyGrey800
    )

    private val blackTheme = darkColorScheme(
        surface = Color(0xFF222222),
        onSurface = MyGrey100
    )

    private val burgundyTheme = darkColorScheme(
        surface = burgundy
    )

    private val limeTheme = darkColorScheme(
        surface = lime
    )

    private val steelBlueTheme = darkColorScheme(
        surface = steelBlue
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
        "WHITE" to whiteTheme,
        "BLACK" to blackTheme,
        "BURGUNDY" to burgundyTheme,
        "LIME" to limeTheme,
        "STEEL" to steelBlueTheme
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
        val keysByCommonPrefix = getLongestPotentialSuffixes(text, listOfColors)
        return keysByCommonPrefix.take(takeTop.coerceAtLeast(1))
    }
}