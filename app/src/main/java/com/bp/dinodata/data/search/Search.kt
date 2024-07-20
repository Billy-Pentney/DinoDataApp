package com.bp.dinodata.data.search

import com.bp.dinodata.data.filters.IFilter
import com.bp.dinodata.data.genus.IGenus

interface IConvertToFilter<T> {
    fun toFilter(): IFilter<T>
}

interface ISearch<T>: IConvertToFilter<T> {
    fun getQuery(): String
    fun isQueryEmpty(): Boolean
    fun getSuggestedSuffixes(): List<String>
}

fun emptySearch(): ISearch<IGenus> = GenusSearch()

