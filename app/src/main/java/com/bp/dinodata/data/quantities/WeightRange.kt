package com.bp.dinodata.data.quantities

data class WeightRange(
    val lowerValue: Float,
    val upperValue: Float,
    private val unit: Units.Weight
): IWeightRange {

    override fun convert(to: Units.Weight): IWeightRange {
        if (to == this.getUnit()) {
            return this
        }
        val lowerConv = Weight.make(lowerValue, unit).convert(to).getValue()
        val upperConv = Weight.make(upperValue, unit).convert(to).getValue()
        return WeightRange(lowerConv, upperConv, unit)
    }

    override fun toString(): String {
        return "${lowerValue}-${upperValue} ${this.unit.toString().lowercase()}"
    }

    override fun getUnit(): Units.Weight = this.unit

    override fun isAtLeast(minValue: Float): Boolean = this.lowerValue >= minValue
    override fun isAtMost(maxValue: Float): Boolean = this.upperValue <= maxValue
}