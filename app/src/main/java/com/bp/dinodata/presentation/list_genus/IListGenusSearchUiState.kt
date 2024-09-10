package com.bp.dinodata.presentation.list_genus

import androidx.compose.ui.text.TextRange
import com.bp.dinodata.data.genus.IGenus
import com.bp.dinodata.data.search.IHasSearchQuery
import com.bp.dinodata.data.search.ISearch
import com.bp.dinodata.data.search.terms.ISearchTerm
import com.bp.dinodata.presentation.DataState

interface IListGenusSearchUiState: IHasSearchQuery {
    fun hasSuggestions(): Boolean
    fun getCompletedSearchTerms(): List<ISearchTerm<in IGenus>>

    fun getSearchTextFieldState(): TextFieldState
    fun getSearchResultsAsList(): List<IGenus>

    fun getFirstVisibleItemIndex(): Int
    fun getFirstVisibleItemOffset(): Int

    fun getSearch(): ISearch<IGenus>
    fun getSearchResultState(): DataState<out List<IGenus>>
    fun updateSearch(newSearch: ISearch<IGenus>): IListGenusSearchUiState
}

interface IMutableSearchBarUiState: IListGenusSearchUiState {
    fun updateSearchTextState(
        newQueryText: String,
        selection: TextRange = TextRange(newQueryText.length)
    ): IListGenusSearchUiState

    /** Attempt to drop the given term from the search, while retaining all others */
    fun removeSearchTerm(term: ISearchTerm<in IGenus>): IListGenusSearchUiState
}

interface IListGenusPageUiState {
    /** Get a list of the key titles for each page in the Pager. */
    fun getPagerKeys(): List<String>

    /** Return the number of the page which is currently being displayed */
    fun getCurrentPagerIndex(): Int

    /**
     * Attempt to get the genus data for the page with the given key.
     * If the key is invalid, return null.
     * */
    fun getPageByIndex(index: Int): List<IGenus>?
}