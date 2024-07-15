package com.bp.dinodata.presentation.list_genus

import com.bp.dinodata.data.IGenus
import com.bp.dinodata.presentation.LoadState

data class ListGenusUiState(
    private val visiblePage: List<IGenus>? = null,
    val loadState: LoadState = LoadState.NotLoading,
    private val searchResults: List<IGenus>? = null,
    val searchBarVisible: Boolean = false,
    val searchBarQuery: String = "",
    val pageSelectionVisible: Boolean = true,
    val pageKeys: List<String> = emptyList(),
    val selectedPageIndex: Int = 0
) {
    fun showSearchBar(visible: Boolean): ListGenusUiState {
        return this.copy(searchBarVisible = visible)
    }

    fun applySearch(searchQuery: String, searchResults: List<IGenus>?): ListGenusUiState {
        return this.copy(
            searchBarQuery = searchQuery,
            searchResults = searchResults
        )
    }

    fun switchPage(index: Int, newResults: List<IGenus>): ListGenusUiState {
        return this.copy(
            selectedPageIndex = index,
            visiblePage = newResults
        )
    }

    fun getVisibleGenera(): List<IGenus>? {
        return if (searchBarVisible) {
            searchResults
        } else {
            visiblePage
        }
    }
}