package com.bp.dinodata.data.filters

interface ICompositeFilter<T>: IFilter<T> {
    fun addFilter(filter: IFilter<T>)
}