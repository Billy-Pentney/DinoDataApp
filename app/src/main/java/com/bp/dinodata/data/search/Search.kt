package com.bp.dinodata.data.search

import com.bp.dinodata.data.filters.IFilter

interface IConvertToFilter<T> {
    fun toFilter(): IFilter<T>
}

interface IHasSearchQuery {
    fun getFullQuery(): String
    fun getQuery(): String
    fun isQueryEmpty(): Boolean
    fun getSuggestedSuffixes(): List<String>
    fun getAutofillSuggestion(): String
}

interface ISearch<T>: IConvertToFilter<T>, IFilter<T>, IHasSearchQuery

