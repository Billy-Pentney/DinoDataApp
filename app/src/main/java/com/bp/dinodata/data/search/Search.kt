package com.bp.dinodata.data.search

import com.bp.dinodata.data.filters.IFilter
import com.bp.dinodata.data.search.terms.ISearchTerm

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

interface ISearch<T>: IConvertToFilter<T>, IFilter<T>, IHasSearchQuery {
    fun getCompletedTerms(): List<ISearchTerm<in T>>
    fun withoutTerm(term: ISearchTerm<in T>): ISearch<T>
    fun withoutQuery(): ISearch<T>
}

