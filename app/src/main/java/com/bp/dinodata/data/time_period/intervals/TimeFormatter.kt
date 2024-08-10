package com.bp.dinodata.data.time_period.intervals

import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.ulp

object TimeFormatter {
    /** Check if the given value has a decimal component (i.e. is not an integer).
     * i.e. 3.4f returns true, while 7.0 returns false. */
    private fun hasDecimal(value: Float): Boolean {
        val rounded = value.roundToInt()
        return abs(value - rounded) > 0.01f
    }

    /** Returns a minimal string representation of the given value.
     * If the value is an integer, then it is displayed without the decimal places (
     * e.g. 3.4 becomes "3.4" but 8.0 becomes "8"
     */
    fun formatFloat(value: Float): String {
        return if (hasDecimal(value)) {
            "$value"
        } else {
            "${value.toInt()}"
        }
    }
}