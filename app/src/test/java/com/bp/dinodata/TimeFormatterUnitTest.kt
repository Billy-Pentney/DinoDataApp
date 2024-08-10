package com.bp.dinodata

import com.bp.dinodata.data.genus.GenusBuilder
import com.bp.dinodata.data.time_period.intervals.TimeFormatter
import com.bp.dinodata.data.time_period.intervals.TimeInterval
import com.bp.dinodata.data.time_period.intervals.TimeMarker
import org.junit.Test

import org.junit.Assert.*

/**
 * Verifies that the GenusBuilder extracts the correct information to build a correct Genus object. *
 */
class TimeFormatterUnitTest {
    @Test
    fun format_floatAbove() {
        val inputValue = 123.1f
        val expected = "123.1"

        val actual = TimeFormatter.formatFloat(inputValue)
        assertEquals(expected, actual)
    }

    @Test
    fun format_floatInteger() {
        val inputValue = 77.000f
        val expected = "77"

        val actual = TimeFormatter.formatFloat(inputValue)
        assertEquals(expected, actual)
    }

    @Test
    fun format_floatBelow() {
        val inputValue = 76.8f
        val expected = "76.8"

        val actual = TimeFormatter.formatFloat(inputValue)
        assertEquals(expected, actual)
    }

    @Test
    fun interval_printNormal() {
        val interval = TimeInterval(56.1f, 45f)
        val expected = "56.1-45 mya"

        assertEquals(expected, interval.toString())
    }

    @Test
    fun interval_argsIncorrectOrder() {
        val interval = TimeInterval(33.3f, 99f)
        val expected = "99-33.3 mya"

        assertEquals(expected, interval.toString())
    }

    @Test
    fun marker_exact() {
        val interval = TimeMarker(120f)
        val expected = "~120 mya"
        assertEquals(expected, interval.toString())
    }

    @Test
    fun marker_decimal() {
        val interval = TimeMarker(123.1f)
        val expected = "~123.1 mya"
        assertEquals(expected, interval.toString())
    }
}