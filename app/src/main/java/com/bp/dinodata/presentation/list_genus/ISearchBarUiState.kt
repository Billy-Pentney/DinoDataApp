package com.bp.dinodata.presentation.list_genus

import com.bp.dinodata.data.genus.IGenus
import com.bp.dinodata.data.search.IDataQuery
import com.bp.dinodata.data.search.terms.ISearchTerm

interface ISearchBarUiState: IDataQuery {
    fun hasSuggestions(): Boolean
    fun removeSearchTerm(term: ISearchTerm<in IGenus>): ISearchBarUiState
    fun getCompletedSearchTerms(): List<ISearchTerm<in IGenus>>
    fun updateSearchQuery(newQuery: String): ISearchBarUiState
    fun runSearch(): ISearchBarUiState
}