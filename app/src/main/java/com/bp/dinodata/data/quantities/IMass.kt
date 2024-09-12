package com.bp.dinodata.data.quantities

interface IDescribesMass: IQuantity {
    fun convert(newUnits: MassUnits): IDescribesMass
    fun getUnit(): MassUnits

    fun isAtLeast(minValue: Float): Boolean
    fun isAtMost(maxValue: Float): Boolean
}

interface IMass: IDescribesMass {
    override fun convert(newUnits: MassUnits): IMass
    fun getValue(): Float
}