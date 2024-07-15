package com.bp.dinodata.presentation.list_genus

import com.bp.dinodata.data.IGenus
import com.bp.dinodata.data.IResultsByLetter
import com.bp.dinodata.data.ResultsByLetter
import com.bp.dinodata.presentation.LoadState

data class ListGenusUiState(
    private val allPageData: IResultsByLetter<IGenus>? = null,
    val loadState: LoadState = LoadState.NotLoading,
    val searchResults: List<IGenus>? = null,
    val searchBarVisible: Boolean = false,
    val searchBarQuery: String = "",
    val pageSelectionVisible: Boolean = true,
    val selectedPageIndex: Int = 0
) {
    val keys: List<Char> = allPageData?.getKeys() ?: emptyList()

    fun applySearch(searchQuery: String, searchResults: List<IGenus>?): ListGenusUiState {
        return this.copy(
            searchBarQuery = searchQuery,
            searchResults = searchResults
        )
    }

    fun getPageByIndex(index: Int): List<IGenus>? {
        return allPageData?.getGroupByIndex(index)
    }
}