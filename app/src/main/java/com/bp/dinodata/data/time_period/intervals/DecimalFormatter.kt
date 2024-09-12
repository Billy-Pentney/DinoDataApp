package com.bp.dinodata.data.time_period.intervals

import kotlin.math.abs
import kotlin.math.roundToInt

object DecimalFormatter {
    /** Check if the given value has a decimal component (i.e. is not an integer).
     * i.e. 3.4f returns true, while 7.0 returns false. */
    private fun hasDecimal(value: Float): Boolean {
        val rounded = value.roundToInt()
        return abs(value - rounded) > 0.01f
    }

    /** Returns a minimal string representation of the given value.
     * If the value is an integer, then it is displayed without the decimal places; otherwise
     * the normal float is returned.
     *      e.g. formatFloat(3.4) = "3.4" but formatFloat(8.0) = "8"
     */
    fun formatFloat(value: Float): String {
        return if (hasDecimal(value)) {
            "$value"
        } else {
            "${value.toInt()}"
        }
    }

    /**
     * Gets a minimal string representation of the given range.
     * Both end-points are truncated to the minimum number of decimal places to capture
     * their precision (0 if the number is an integer). Returns the truncated range as a string e.g.
     * formatRange(0.2f, 5.0f) returns "0.2-5"
     * */
    fun formatRange(lowerValue: Float, upperValue: Float): String {
        val lower = formatFloat(lowerValue)
        val upper = formatFloat(upperValue)
        return "$lower-$upper"
    }
}