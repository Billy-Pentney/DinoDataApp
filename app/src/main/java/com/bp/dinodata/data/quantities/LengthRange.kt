package com.bp.dinodata.data.quantities

data class LengthRange(
    val lowerValue: Float,
    val upperValue: Float,
    private val unit: Units.Length
): ILengthRange {

    override fun convert(to: Units.Length): ILengthRange {
        if (to == this.getUnit()) {
            return this
        }
        val lowerConv = Length.make(lowerValue, unit).convert(to).getValue()
        val upperConv = Length.make(upperValue, unit).convert(to).getValue()
        return LengthRange(lowerConv, upperConv, unit)
    }

//    class MetreRange(min: Float, max: Float): LengthRange(min, max, Units.Length.Metres)
//    class CentimetreRange(min: Float, max: Float): LengthRange(min, max, Units.Length.Centimetres)
//    class FeetRange(min: Float, max: Float): LengthRange(min, max, Units.Length.Feet)

    override fun toString(): String {
        return "${lowerValue}-${upperValue} ${this.unit.toString().lowercase()}"
    }

    override fun getUnit(): Units.Length = this.unit
}