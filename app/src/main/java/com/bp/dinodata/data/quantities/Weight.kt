package com.bp.dinodata.data.quantities

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

    companion object {
        fun make(floatValue: Float, units: Units.Weight): IWeight {
            return when (units) {
                Units.Weight.Kg -> Kg(floatValue)
                Units.Weight.Tonnes -> Tonnes(floatValue)
                Units.Weight.TonsImperial -> TonsImperial(floatValue)
                Units.Weight.TonsUS -> TonsUS(floatValue)
            }
        }
    }
}