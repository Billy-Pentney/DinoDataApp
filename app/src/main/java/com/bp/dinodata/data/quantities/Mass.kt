package com.bp.dinodata.data.quantities

import android.util.Log
import com.bp.dinodata.data.genus.GenusBuilder.Companion.TAG
import com.bp.dinodata.data.time_period.intervals.DecimalFormatter

/**
 * A representation of a numerical mass quantity, with the given units.
 * @see MassRange for an interval of mass.
 * @see Length
 */
sealed class Mass(
    valueIn: Float,
    private val unit: MassUnit
): IMass {
    private var value = valueIn.coerceAtLeast(0f)

    class Tonnes(value: Float): Mass(value, MassUnit.TONNE) {
        override fun convert(newUnits: MassUnit): IMass {
            return when (newUnits) {
                MassUnit.TONNE -> this
                MassUnit.TON_IMPERIAL -> TonsImperial(getValue() / MassUnitConstants.TONNES_PER_TON_IMP)
                MassUnit.KILOGRAM -> Kilograms(getValue() * 1000f)
                MassUnit.TON_US -> TonsUS(getValue() / MassUnitConstants.TONNES_PER_TON_US)
            }
        }
    }
    class TonsImperial(value: Float): Mass(value, MassUnit.TON_IMPERIAL) {
        override fun convert(newUnits: MassUnit): IMass {
            return when (newUnits) {
                MassUnit.TON_IMPERIAL -> this
                MassUnit.TONNE -> Tonnes(getValue() * MassUnitConstants.TONNES_PER_TON_IMP)
                MassUnit.KILOGRAM -> Kilograms(getValue() * MassUnitConstants.KG_PER_TON_IMP)
                MassUnit.TON_US -> TonsUS(getValue() * MassUnitConstants.TONNES_PER_TON_IMP / MassUnitConstants.TONNES_PER_TON_US)
            }
        }
    }
    class TonsUS(value: Float): Mass(value, MassUnit.TON_US) {
        override fun convert(newUnits: MassUnit): IMass {
            return when (newUnits) {
                MassUnit.TON_US -> this
                MassUnit.TONNE -> Tonnes(getValue() * MassUnitConstants.TONNES_PER_TON_US)
                MassUnit.KILOGRAM -> Kilograms(getValue() * MassUnitConstants.KG_PER_TON_US)
                MassUnit.TON_IMPERIAL -> TonsImperial(getValue() * MassUnitConstants.TONNES_PER_TON_US / MassUnitConstants.TONNES_PER_TON_IMP)
            }
        }
    }
    class Kilograms(value: Float): Mass(value, MassUnit.KILOGRAM) {
        override fun convert(newUnits: MassUnit): IMass {
            return when (newUnits) {
                MassUnit.TONNE -> Tonnes(getValue() / 1000f)
                MassUnit.TON_IMPERIAL -> TonsImperial(getValue() / MassUnitConstants.KG_PER_TON_IMP)
                MassUnit.KILOGRAM -> this
                MassUnit.TON_US -> TonsUS(getValue() / MassUnitConstants.KG_PER_TON_US)
            }
        }
    }

    override fun getUnitString(): String {
        return UnitFormatter.convertToString(this.unit, getValue() != 1f)
    }

    override fun toString(): String {
        val formattedValue = DecimalFormatter.formatFloat(value)
        val units = this.getUnitString()
        return "$formattedValue $units"
    }

    override fun getUnit(): MassUnit = this.unit
    override fun getValue(): Float = value

    override fun isAtLeast(minValue: Float): Boolean = this.value >= minValue
    override fun isAtMost(maxValue: Float): Boolean = this.value <= maxValue

    companion object {
        val UnitsMap = mapOf(
            "kg" to MassUnit.KILOGRAM,
            "tonnes" to MassUnit.TONNE,
            "tons" to MassUnit.TON_IMPERIAL            /// ASSUMPTION
        )

        fun make(floatValue: Float, units: MassUnit): IMass {

            // Auto-convert between kg and tonnes, depending on the given quantity
            if (units == MassUnit.KILOGRAM && floatValue >= 1000f) {
                return Tonnes(floatValue / 1000f)
            }
            else if (units == MassUnit.TONNE && floatValue <= 1f) {
                return Kilograms(floatValue * 1000f)
            }

            return when (units) {
                MassUnit.KILOGRAM -> Kilograms(floatValue)
                MassUnit.TONNE -> Tonnes(floatValue)
                MassUnit.TON_IMPERIAL -> TonsImperial(floatValue)
                MassUnit.TON_US -> TonsUS(floatValue)
            }
        }

        fun tryMake(weightValue: String, units: MassUnit): IDescribesMass? {
            try {
                if ("-" in weightValue) {
                    // Parse a range e.g. "100-98.1"
                    val splits = weightValue.split("-")
                    val lower = splits[0].toFloat()
                    val upper = splits[1].toFloat()
                    return MassRange(lower, upper, units)
                }
                else {
                    // Parse a single value
                    val floatValue = weightValue.toFloat()
                    return Mass.make(floatValue, units)
                }
            }
            catch (nfe:NumberFormatException) {
                Log.e(TAG, "Failed to parse \'$weightValue\' as float")
                return null
            }
        }
    }
}