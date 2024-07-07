package com.bp.dinodata.data.quantities


interface IQuantity

interface IDescribesWeight: IQuantity {
    fun convert(to: Units.Weight): IDescribesWeight
    fun getUnit(): Units.Weight
}

interface IDescribesLength: IQuantity {
    fun convert(to: Units.Length): IDescribesLength
    fun getUnit(): Units.Length
}

interface IWeight: IDescribesWeight {
    override fun convert(to: Units.Weight): IWeight
    fun getValue(): Float
}

interface ILength: IDescribesLength {
    override fun convert(to: Units.Length): ILength
    fun getValue(): Float
}





