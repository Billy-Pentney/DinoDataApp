package com.bp.dinodata.presentation.list_genus

import androidx.compose.ui.text.TextRange
import com.bp.dinodata.data.genus.IGenus
import com.bp.dinodata.data.search.IDataQuery
import com.bp.dinodata.data.search.terms.ISearchTerm

interface ISearchBarUiState: IDataQuery {
    fun hasSuggestions(): Boolean
    fun removeSearchTerm(term: ISearchTerm<in IGenus>): ISearchBarUiState
    fun getCompletedSearchTerms(): List<ISearchTerm<in IGenus>>
    fun updateSearchQuery(
        newQuery: String, selection: TextRange = TextRange(newQuery.length)
    ): ISearchBarUiState
    fun runSearch(): ISearchBarUiState
    fun getSearchTextFieldState(): TextFieldState
}