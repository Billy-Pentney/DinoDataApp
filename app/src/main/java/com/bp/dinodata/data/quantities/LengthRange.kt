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
    private val unit: LengthUnits
): ILengthRange {

    override fun convert(newUnits: LengthUnits): ILengthRange {
        if (newUnits == this.getUnit()) {
            return this
        }
        val lowerConv = Length.make(lowerValue, unit).convert(newUnits).getValue()
        val upperConv = Length.make(upperValue, unit).convert(newUnits).getValue()
        return LengthRange(lowerConv, upperConv, unit)
    }

    override fun toString(): String {
        val range = DecimalFormatter.formatRange(lowerValue, upperValue)
        val units = unit.toString().lowercase()
        return "$range $units"
    }

    override fun getUnit(): LengthUnits = this.unit
}