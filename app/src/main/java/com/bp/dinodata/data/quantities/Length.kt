package com.bp.dinodata.data.quantities

import android.util.Log
import com.bp.dinodata.data.time_period.intervals.DecimalFormatter

/**
 * A representation of an exact length quantity with the given units, for example
 * Length(4.5, Metres) represents "4.5 metres".
 * @see LengthRange for an interval of lengths
 * @see Mass
 * */
sealed class Length(
    valueIn: Float,
    private val unit: LengthUnit
): ILength {

    private var value = valueIn.coerceAtLeast(0f)

    class Metres(value: Float) : Length(value, LengthUnit.METRE) {
        override fun convert(newUnits: LengthUnit): ILength {
            return when (newUnits) {
                LengthUnit.METRE -> this
                LengthUnit.CENTIMETRE -> Centimetres(getValue() * 100f)
                LengthUnit.FOOT -> Feet(getValue() * LengthUnitConstants.FT_PER_METRE)
            }
        }
    }

    class Centimetres(value: Float) : Length(value, LengthUnit.CENTIMETRE) {
        override fun convert(newUnits: LengthUnit): ILength {
            return when (newUnits) {
                LengthUnit.METRE -> Metres(getValue() / 100f)
                LengthUnit.CENTIMETRE -> this
                LengthUnit.FOOT -> Feet(getValue() / LengthUnitConstants.CM_PER_FT)
            }
        }
    }

    class Feet(value: Float) : Length(value, LengthUnit.FOOT) {
        override fun convert(newUnits: LengthUnit): ILength {
            return when (newUnits) {
                LengthUnit.METRE -> Metres(getValue() / LengthUnitConstants.FT_PER_METRE)
                LengthUnit.CENTIMETRE -> Centimetres(getValue() * LengthUnitConstants.CM_PER_FT)
                LengthUnit.FOOT -> this
            }
        }
    }

    override fun getUnitString(): String {
        return UnitFormatter.convertToString(this.unit, this.getValue() != 1f)
    }

    override fun toString(): String {
        val formattedValue = DecimalFormatter.formatFloat(value)
        val units = this.getUnitString()
        return "$formattedValue $units"
    }

    override fun getUnit(): LengthUnit = this.unit

    override fun getValue(): Float = value

    companion object {
        val UnitsMap = mapOf(
            "metres" to LengthUnit.METRE,
            "m" to LengthUnit.METRE,
            "cm" to LengthUnit.CENTIMETRE,
            "ft" to LengthUnit.FOOT
        )

        fun make(value: Float, unit: LengthUnit): ILength {
            return when (unit) {
                LengthUnit.METRE -> Metres(value)
                LengthUnit.CENTIMETRE -> Centimetres(value)
                LengthUnit.FOOT -> Feet(value)
            }
        }

        fun tryMake(lengthValue: String, units: LengthUnit): IDescribesLength? {
            try {
                if ("-" in lengthValue) {
                    // Parse a range e.g. "98.1-100"
                    val splits = lengthValue.split("-")
                    val lower = splits[0].trim().toFloat()
                    val upper = splits[1].trim().toFloat()
                    return LengthRange(lower, upper, units)
                }
                else {
                    // Parse a single value
                    val floatValue = lengthValue.toFloat()
                    return Length.make(floatValue, units)
                }
            }
            catch (nfe:NumberFormatException) {
                Log.e("GenusBuilder", "Failed to parse \'$lengthValue\' as float")
                return null
            }
        }
    }
}