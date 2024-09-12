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
    private val unit: LengthUnits
): ILength {

    private var value = valueIn.coerceAtLeast(0f)

    class Metres(value: Float) : Length(value, LengthUnits.Metres) {
        override fun convert(newUnits: LengthUnits): ILength {
            return when (newUnits) {
                LengthUnits.Metres -> this
                LengthUnits.Centimetres -> Centimetres(getValue() * 100f)
                LengthUnits.Feet -> Feet(getValue() * LengthUnitConstants.FT_PER_METRE)
            }
        }
    }

    class Centimetres(value: Float) : Length(value, LengthUnits.Centimetres) {
        override fun convert(newUnits: LengthUnits): ILength {
            return when (newUnits) {
                LengthUnits.Metres -> Metres(getValue() / 100f)
                LengthUnits.Centimetres -> this
                LengthUnits.Feet -> Feet(getValue() / LengthUnitConstants.CM_PER_FT)
            }
        }
    }

    class Feet(value: Float) : Length(value, LengthUnits.Feet) {
        override fun convert(newUnits: LengthUnits): ILength {
            return when (newUnits) {
                LengthUnits.Metres -> Metres(getValue() / LengthUnitConstants.FT_PER_METRE)
                LengthUnits.Centimetres -> Centimetres(getValue() * LengthUnitConstants.CM_PER_FT)
                LengthUnits.Feet -> this
            }
        }
    }

    override fun toString(): String {
        val formattedValue = DecimalFormatter.formatFloat(value)
        val units = unit.toString().lowercase()
        return "$formattedValue $units"
    }

    override fun getUnit(): LengthUnits = this.unit

    override fun getValue(): Float = value

    companion object {
        val UnitsMap = mapOf(
            "metres" to LengthUnits.Metres,
            "cm" to LengthUnits.Centimetres,
            "ft" to LengthUnits.Feet,
            "m" to LengthUnits.Metres
        )

        fun make(value: Float, unit: LengthUnits): ILength {
            return when (unit) {
                LengthUnits.Metres -> Metres(value)
                LengthUnits.Centimetres -> Centimetres(value)
                LengthUnits.Feet -> Feet(value)
            }
        }

        fun tryMake(lengthValue: String, units: LengthUnits): IDescribesLength? {
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