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
                Units.Weight.Tons -> Tons(getValue() / UnitConversions.TONNES_PER_TON)
                Units.Weight.Kg -> Kg(getValue() * 1000f)
            }
        }
    }
    class Tons(value: Float): Weight(value, Units.Weight.Tons) {
        override fun convert(to: Units.Weight): IWeight {
            return when (to) {
                Units.Weight.Tonnes -> Tonnes(getValue() * UnitConversions.TONNES_PER_TON)
                Units.Weight.Tons -> this
                Units.Weight.Kg -> Kg(getValue() * UnitConversions.KG_PER_TON)
            }
        }
    }
    class Kg(value: Float): Weight(value, Units.Weight.Kg) {
        override fun convert(to: Units.Weight): IWeight {
            return when (to) {
                Units.Weight.Tonnes -> Tonnes(getValue() / 1000f)
                Units.Weight.Tons -> Tons(getValue() / UnitConversions.KG_PER_TON)
                Units.Weight.Kg -> this
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
                Units.Weight.Tons -> Tons(floatValue)
            }
        }
    }
}