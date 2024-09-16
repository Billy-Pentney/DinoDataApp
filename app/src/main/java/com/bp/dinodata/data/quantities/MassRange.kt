package com.bp.dinodata.data.quantities

import com.bp.dinodata.data.time_period.intervals.DecimalFormatter

/**
 * Representation of an (inclusive) range of masses from lowerValue to upperValue with the given
 * units.
 * @see Mass for an exact Mass quantity.
 * */
data class MassRange(
    val lowerValue: Float,
    val upperValue: Float,
    private val unit: MassUnit
): IMassRange {

    companion object {
        /**
         * Attempts to construct a MassRange for the given values. Fails if either value cannot
         * be converted to a non-negative number
         * */
        fun tryMake(minValue: String, maxValue: String, units: MassUnit): MassRange? {
            try {
                val minAsFloat = minValue.toFloat()
                val maxAsFloat = maxValue.toFloat()
                if (minAsFloat < 0 || maxAsFloat < 0) {
                    return null
                }
                return MassRange(minAsFloat, maxAsFloat, units)
            } catch (ex: NumberFormatException) {
                return null
            }
        }
    }

    override fun getUnitString(): String {
        return UnitFormatter.convertToString(this.unit, isPlural = true)
    }

    override fun convert(newUnits: MassUnit): IMassRange {
        if (newUnits == this.getUnit()) {
            return this
        }
        val lowerConv = Mass.make(lowerValue, unit).convert(newUnits).getValue()
        val upperConv = Mass.make(upperValue, unit).convert(newUnits).getValue()
        return MassRange(lowerConv, upperConv, unit)
    }

    override fun toString(): String {
        val range = DecimalFormatter.formatRange(lowerValue, upperValue)
        val units = getUnitString()
        return "$range $units"
    }

    override fun getUnit(): MassUnit = this.unit

    override fun isAtLeast(minValue: Float): Boolean = this.lowerValue >= minValue
    override fun isAtMost(maxValue: Float): Boolean = this.upperValue <= maxValue
}