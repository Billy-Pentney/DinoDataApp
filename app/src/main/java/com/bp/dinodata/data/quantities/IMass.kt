package com.bp.dinodata.data.quantities

interface IDescribesMass: IQuantity {
    fun convert(newUnits: MassUnit): IDescribesMass
    override fun getUnit(): MassUnit

    fun isAtLeast(minValue: Float): Boolean
    fun isAtMost(maxValue: Float): Boolean
}

interface IMass: IDescribesMass {
    override fun convert(newUnits: MassUnit): IMass
    fun getValue(): Float
}