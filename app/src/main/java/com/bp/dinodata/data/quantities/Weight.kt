package com.bp.dinodata.data.quantities

import android.util.Log
import com.bp.dinodata.data.genus.GenusBuilder.Companion.TAG

sealed class Weight(
    valueIn: Float,
    private val unit: Units.Weight
): IWeight {
    private var value = valueIn.coerceAtLeast(0f)

    class Tonnes(value: Float): Weight(value, Units.Weight.Tonnes) {
        override fun convert(to: Units.Weight): IWeight {
            return when (to) {
                Units.Weight.Tonnes -> this
                Units.Weight.TonsImperial -> TonsImperial(getValue() / UnitConversions.TONNES_PER_TON_IMP)
                Units.Weight.Kg -> Kg(getValue() * 1000f)
                Units.Weight.TonsUS -> TonsUS(getValue() / UnitConversions.TONNES_PER_TON_US)
            }
        }
    }
    class TonsImperial(value: Float): Weight(value, Units.Weight.TonsImperial) {
        override fun convert(to: Units.Weight): IWeight {
            return when (to) {
                Units.Weight.TonsImperial -> this
                Units.Weight.Tonnes -> Tonnes(getValue() * UnitConversions.TONNES_PER_TON_IMP)
                Units.Weight.Kg -> Kg(getValue() * UnitConversions.KG_PER_TON_IMP)
                Units.Weight.TonsUS -> TonsUS(getValue() * UnitConversions.TONNES_PER_TON_IMP / UnitConversions.TONNES_PER_TON_US)
            }
        }
    }
    class TonsUS(value: Float): Weight(value, Units.Weight.TonsUS) {
        override fun convert(to: Units.Weight): IWeight {
            return when (to) {
                Units.Weight.TonsUS -> this
                Units.Weight.Tonnes -> Tonnes(getValue() * UnitConversions.TONNES_PER_TON_US)
                Units.Weight.Kg -> Kg(getValue() * UnitConversions.KG_PER_TON_US)
                Units.Weight.TonsImperial -> TonsImperial(getValue() * UnitConversions.TONNES_PER_TON_US / UnitConversions.TONNES_PER_TON_IMP)
            }
        }
    }
    class Kg(value: Float): Weight(value, Units.Weight.Kg) {
        override fun convert(to: Units.Weight): IWeight {
            return when (to) {
                Units.Weight.Tonnes -> Tonnes(getValue() / 1000f)
                Units.Weight.TonsImperial -> TonsImperial(getValue() / UnitConversions.KG_PER_TON_IMP)
                Units.Weight.Kg -> this
                Units.Weight.TonsUS -> TonsUS(getValue() / UnitConversions.KG_PER_TON_US)
            }
        }
    }

    override fun toString(): String = "${this.value} ${this.unit.toString().lowercase()}"

    override fun getUnit(): Units.Weight = this.unit
    override fun getValue(): Float = value

    override fun isAtLeast(minValue: Float): Boolean = this.value >= minValue
    override fun isAtMost(maxValue: Float): Boolean = this.value <= maxValue

    companion object {
        val UnitsMap = mapOf(
            "kg" to Units.Weight.Kg,
            "tonnes" to Units.Weight.Tonnes,
            "tons" to Units.Weight.TonsImperial,            /// ASSUMPTION
        )

        fun make(floatValue: Float, units: Units.Weight): IWeight {

            // Auto-convert between kg and tonnes, depending on the given quantity
            if (units == Units.Weight.Kg && floatValue >= 1000f) {
                return Tonnes(floatValue / 1000f)
            }
            else if (units == Units.Weight.Tonnes && floatValue <= 1f) {
                return Kg(floatValue * 1000f)
            }


            return when (units) {
                Units.Weight.Kg -> Kg(floatValue)
                Units.Weight.Tonnes -> Tonnes(floatValue)
                Units.Weight.TonsImperial -> TonsImperial(floatValue)
                Units.Weight.TonsUS -> TonsUS(floatValue)
            }
        }

        fun tryMake(weightValue: String, units: Units.Weight): IDescribesWeight? {
            try {
                if ("-" in weightValue) {
                    // Parse a range e.g. "100-98.1"
                    val splits = weightValue.split("-")
                    val lower = splits[0].toFloat()
                    val upper = splits[1].toFloat()
                    return WeightRange(lower, upper, units)
                }
                else {
                    // Parse a single value
                    val floatValue = weightValue.toFloat()
                    return Weight.make(floatValue, units)
                }
            }
            catch (nfe:NumberFormatException) {
                Log.e(TAG, "Failed to parse \'$weightValue\' as float")
                return null
            }
        }
    }
}