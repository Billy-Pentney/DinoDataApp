package com.bp.dinodata.data.quantities

interface IDescribesLength: IQuantity {
    fun convert(newUnits: LengthUnit): IDescribesLength
    override fun getUnit(): LengthUnit
}

interface ILength: IDescribesLength {
    override fun convert(newUnits: LengthUnit): ILength
    fun getValue(): Float
}