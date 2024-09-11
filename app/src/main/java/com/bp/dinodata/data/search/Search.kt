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

    /**
     * Returns a copy of this search which doesn't contain the given term.
     * If the given term is not found in the term-list; return a copy of this search. */
    fun withoutTerm(term: ISearchTerm<in T>): ISearch<T>

    /** Returns a copy of this search with an empty query string but preserving completed terms */
    fun withoutQuery(): ISearch<T>
}

