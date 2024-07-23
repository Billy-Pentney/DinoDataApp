package com.bp.dinodata.data.search

import com.bp.dinodata.data.filters.IFilter

interface IConvertToFilter<T> {
    fun toFilter(): IFilter<T>
}

interface IDataQuery {
    fun getFullQuery(): String
    fun getQuery(): String
    fun isQueryEmpty(): Boolean
    fun getSuggestedSuffixes(): List<String>
    fun getAutofillSuggestion(): String
}

interface ISearch<T>: IConvertToFilter<T>, IFilter<T>, IDataQuery

interface IMutableSearch<T>: ISearch<T> {
    fun removeTerm(term: ISearchTerm<in T>): ISearch<T>
    fun getCompletedTerms(): List<ISearchTerm<in T>>
}