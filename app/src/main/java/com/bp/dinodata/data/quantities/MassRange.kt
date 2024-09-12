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
    private val unit: MassUnits
): IMassRange {

    override fun convert(newUnits: MassUnits): IMassRange {
        if (newUnits == this.getUnit()) {
            return this
        }
        val lowerConv = Mass.make(lowerValue, unit).convert(newUnits).getValue()
        val upperConv = Mass.make(upperValue, unit).convert(newUnits).getValue()
        return MassRange(lowerConv, upperConv, unit)
    }

    override fun toString(): String {
        val range = DecimalFormatter.formatRange(lowerValue, upperValue)
        val units = unit.toString().lowercase()
        return "$range $units"
    }

    override fun getUnit(): MassUnits = this.unit

    override fun isAtLeast(minValue: Float): Boolean = this.lowerValue >= minValue
    override fun isAtMost(maxValue: Float): Boolean = this.upperValue <= maxValue
}