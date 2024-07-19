package com.bp.dinodata.data

import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color
import com.bp.dinodata.theme.MyGrey400

object ColorConverter {

    private val mapping = mapOf(
        "RED" to Color(0xFFFF0000),
        "ORANGE" to Color(0xFFFF7722),
        "YELLOW" to Color(0xFFFFBB44),
        "GREEN" to Color(0xFF33FF33),
        "CYAN" to Color(0xFF99CCFF),
        "BLUE" to Color(0xFF5588FF),
        "VIOLET" to Color(0xFFFF22FF),
        "BROWN" to Color(0xFF444400),
        "PINK" to Color(0xFFFF9999)
    )

//    private val redTheme = darkColorScheme(
//        background = MyGrey400,
//        surface = Color(0xFF882222)
//    )

    fun convertStringToColor(name: String?): Color? {
        return mapping[name]
    }
}