package com.bp.dinodata.presentation.utils

import android.util.Log
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

    val DefaultColor = ColorReference.NONE

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
        surface = pink,
        onSurface = Color(0xFF552211)
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

    /** Represents the list of valid colours which can be used to theme genera. */
    enum class ColorReference {
        RED,
        ORANGE,
        YELLOW,
        GREEN,
        BLUE,
        CYAN,
        VIOLET,
        BURGUNDY,
        INDIGO,
        PINK,
        BROWN,
        WHITE,
        BLACK,
        LIME,
        STEEL,
        NONE
    }

    private val colorNameToSchemeMap: Map<String, ColorScheme?> = mapOf(
        ColorReference.RED.name to redTheme,
        ColorReference.ORANGE.name to orangeTheme,
        ColorReference.YELLOW.name to yellowTheme,
        ColorReference.GREEN.name to greenTheme,
        ColorReference.BLUE.name to blueTheme,
        ColorReference.INDIGO.name to indigoTheme,
        ColorReference.BURGUNDY.name to burgundyTheme,
        ColorReference.CYAN.name to cyanTheme,
        ColorReference.WHITE.name to whiteTheme,
        ColorReference.BLACK.name to blackTheme,
        ColorReference.BROWN.name to brownTheme,
        ColorReference.PINK.name to pinkTheme,
        ColorReference.VIOLET.name to violetTheme,
        ColorReference.STEEL.name to steelBlueTheme,
        ColorReference.LIME.name to limeTheme,
        ColorReference.NONE.name to null
    )

    val listOfColors = colorNameToSchemeMap.keys.toList()

    override fun getListOfOptions(): List<String> = listOfColors

    fun convertColorNameToColor(name: String?): ColorReference? {
        for (color in ColorReference.entries){
            Log.d("ThemeConverter", "Color name ${color.name}, vs. $name")
        }
        return ColorReference.entries.firstOrNull { name?.uppercase() == it.name }
    }

    /** Get the list of all colours, excluding the 'None' default color. */
    fun getNonNullColours(): List<String> {
        return listOfColors.dropLast(1)
    }

    fun getTheme(name: String?): ColorScheme? {
        val colorRef = convertColorNameToColor(name)?.name
        val theme = colorNameToSchemeMap[colorRef]
        return theme
    }

    fun getPrimaryColor(colorName: String?): Color? {
        return getTheme(colorName)?.surface
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