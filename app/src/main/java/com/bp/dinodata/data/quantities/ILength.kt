package com.bp.dinodata.data.quantities

interface IDescribesLength: IQuantity {
    fun convert(newUnits: LengthUnits): IDescribesLength
    fun getUnit(): LengthUnits
}

interface ILength: IDescribesLength {
    override fun convert(newUnits: LengthUnits): ILength
    fun getValue(): Float
}