package com.bp.dinodata.data.filters

import com.bp.dinodata.data.genus.IGenus


interface FilterBuilder<T> {
    fun build(): IFilter<T>
    fun addFilter(filter: IFilter<in T>)
}

class FilterBuilderImpl<T>(
    private val filters: MutableList<IFilter<in T>> = mutableListOf()
): FilterBuilder<T> {
    override fun build(): IFilter<T> = ConjunctiveFilter(filters)
    override fun addFilter(filter: IFilter<in T>) {
        filters.add(filter)
    }
}