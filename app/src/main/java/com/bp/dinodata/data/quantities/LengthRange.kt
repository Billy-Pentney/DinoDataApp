package com.bp.dinodata.data.quantities

import com.bp.dinodata.data.time_period.intervals.DecimalFormatter

/**
 * Representation of an (inclusive) range of length values from lowerValue to upperValue in
 * the given units.
 * @see Length for an exact length quantity.
 * */
data class LengthRange(
    val lowerValue: Float,
    val upperValue: Float,
    private val unit: LengthUnit
): ILengthRange {

    override fun convert(newUnits: LengthUnit): ILengthRange {
        if (newUnits == this.getUnit()) {
            return this
        }
        val lowerConv = Length.make(lowerValue, unit).convert(newUnits).getValue()
        val upperConv = Length.make(upperValue, unit).convert(newUnits).getValue()
        return LengthRange(lowerConv, upperConv, unit)
    }

    override fun getUnitString(): String {
        return UnitFormatter.convertToString(this.unit, isPlural = true)
    }

    override fun toString(): String {
        val range = DecimalFormatter.formatRange(lowerValue, upperValue)
        val units = getUnitString()
        return "$range $units"
    }

    override fun getUnit(): LengthUnit = this.unit

    companion object {
        /**
         * Attempts to construct a MassRange for the given values. Fails if either value cannot
         * be converted to a non-negative number
         * */
        fun tryMake(minValue: String, maxValue: String, units: LengthUnit): LengthRange? {
            try {
                val minAsFloat = minValue.toFloat()
                val maxAsFloat = maxValue.toFloat()
                if (minAsFloat < 0 || maxAsFloat < 0) {
                    return null
                }
                return LengthRange(minAsFloat, maxAsFloat, units)
            } catch (ex: NumberFormatException) {
                return null
            }
        }
    }
}