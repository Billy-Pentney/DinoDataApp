package com.bp.dinodata.data.filters

import com.bp.dinodata.data.genus.IGenus


interface FilterBuilder<T> {
    fun build(): IFilter<T>
    fun addFilter(filter: IFilter<in T>)
}

class FilterBuilderImpl<T>: FilterBuilder<T> {
    private val filters = mutableListOf<IFilter<in T>>()

    override fun build(): IFilter<T> = ConjunctiveFilter(filters)
    override fun addFilter(filter: IFilter<in T>) {
        filters.add(filter)
    }
}