package com.bp.dinodata.data.quantities

import android.util.Log

sealed class Length(
    valueIn: Float,
    private val unit: Units.Length
): ILength {

    private var value = valueIn.coerceAtLeast(0f)

    class Metres(value: Float) : Length(value, Units.Length.Metres) {
        override fun convert(to: Units.Length): ILength {
            return when (to) {
                Units.Length.Metres -> this
                Units.Length.Centimetres -> Centimetres(getValue() * 100f)
                Units.Length.Feet -> Feet(getValue() * UnitConversions.FT_PER_METRE)
            }
        }
    }

    class Centimetres(value: Float) : Length(value, Units.Length.Centimetres) {
        override fun convert(to: Units.Length): ILength {
            return when (to) {
                Units.Length.Metres -> Metres(getValue() / 100f)
                Units.Length.Centimetres -> this
                Units.Length.Feet -> Feet(getValue() / UnitConversions.CM_PER_FT)
            }
        }
    }

    class Feet(value: Float) : Length(value, Units.Length.Feet) {
        override fun convert(to: Units.Length): ILength {
            return when (to) {
                Units.Length.Metres -> Metres(getValue() / UnitConversions.FT_PER_METRE)
                Units.Length.Centimetres -> Centimetres(getValue() * UnitConversions.CM_PER_FT)
                Units.Length.Feet -> this
            }
        }
    }

    override fun toString(): String = "${this.value} ${this.unit.toString().lowercase()}"

    override fun getUnit(): Units.Length = this.unit

    override fun getValue(): Float = value

    companion object {
        val UnitsMap = mapOf(
            "metres" to Units.Length.Metres,
            "cm" to Units.Length.Centimetres,
            "ft" to Units.Length.Feet,
            "m" to Units.Length.Metres
        )

        fun make(value: Float, unit: Units.Length): ILength {
            return when (unit) {
                Units.Length.Metres -> Metres(value)
                Units.Length.Centimetres -> Centimetres(value)
                Units.Length.Feet -> Feet(value)
            }
        }

        fun tryMake(lengthValue: String, units: Units.Length): IDescribesLength? {
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