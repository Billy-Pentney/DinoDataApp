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
    private val unit: MassUnits
): IMass {
    private var value = valueIn.coerceAtLeast(0f)

    class Tonnes(value: Float): Mass(value, MassUnits.Tonnes) {
        override fun convert(newUnits: MassUnits): IMass {
            return when (newUnits) {
                MassUnits.Tonnes -> this
                MassUnits.TonsImperial -> TonsImperial(getValue() / MassUnitConstants.TONNES_PER_TON_IMP)
                MassUnits.Kilograms -> Kg(getValue() * 1000f)
                MassUnits.TonsUS -> TonsUS(getValue() / MassUnitConstants.TONNES_PER_TON_US)
            }
        }
    }
    class TonsImperial(value: Float): Mass(value, MassUnits.TonsImperial) {
        override fun convert(newUnits: MassUnits): IMass {
            return when (newUnits) {
                MassUnits.TonsImperial -> this
                MassUnits.Tonnes -> Tonnes(getValue() * MassUnitConstants.TONNES_PER_TON_IMP)
                MassUnits.Kilograms -> Kg(getValue() * MassUnitConstants.KG_PER_TON_IMP)
                MassUnits.TonsUS -> TonsUS(getValue() * MassUnitConstants.TONNES_PER_TON_IMP / MassUnitConstants.TONNES_PER_TON_US)
            }
        }
    }
    class TonsUS(value: Float): Mass(value, MassUnits.TonsUS) {
        override fun convert(newUnits: MassUnits): IMass {
            return when (newUnits) {
                MassUnits.TonsUS -> this
                MassUnits.Tonnes -> Tonnes(getValue() * MassUnitConstants.TONNES_PER_TON_US)
                MassUnits.Kilograms -> Kg(getValue() * MassUnitConstants.KG_PER_TON_US)
                MassUnits.TonsImperial -> TonsImperial(getValue() * MassUnitConstants.TONNES_PER_TON_US / MassUnitConstants.TONNES_PER_TON_IMP)
            }
        }
    }
    class Kg(value: Float): Mass(value, MassUnits.Kilograms) {
        override fun convert(newUnits: MassUnits): IMass {
            return when (newUnits) {
                MassUnits.Tonnes -> Tonnes(getValue() / 1000f)
                MassUnits.TonsImperial -> TonsImperial(getValue() / MassUnitConstants.KG_PER_TON_IMP)
                MassUnits.Kilograms -> this
                MassUnits.TonsUS -> TonsUS(getValue() / MassUnitConstants.KG_PER_TON_US)
            }
        }
    }

    override fun toString(): String {
        val formattedValue = DecimalFormatter.formatFloat(value)
        val units = unit.toString().lowercase()
        return "$formattedValue $units"
    }

    override fun getUnit(): MassUnits = this.unit
    override fun getValue(): Float = value

    override fun isAtLeast(minValue: Float): Boolean = this.value >= minValue
    override fun isAtMost(maxValue: Float): Boolean = this.value <= maxValue

    companion object {
        val UnitsMap = mapOf(
            "kg" to MassUnits.Kilograms,
            "tonnes" to MassUnits.Tonnes,
            "tons" to MassUnits.TonsImperial            /// ASSUMPTION
        )

        fun make(floatValue: Float, units: MassUnits): IMass {

            // Auto-convert between kg and tonnes, depending on the given quantity
            if (units == MassUnits.Kilograms && floatValue >= 1000f) {
                return Tonnes(floatValue / 1000f)
            }
            else if (units == MassUnits.Tonnes && floatValue <= 1f) {
                return Kg(floatValue * 1000f)
            }

            return when (units) {
                MassUnits.Kilograms -> Kg(floatValue)
                MassUnits.Tonnes -> Tonnes(floatValue)
                MassUnits.TonsImperial -> TonsImperial(floatValue)
                MassUnits.TonsUS -> TonsUS(floatValue)
            }
        }

        fun tryMake(weightValue: String, units: MassUnits): IDescribesMass? {
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