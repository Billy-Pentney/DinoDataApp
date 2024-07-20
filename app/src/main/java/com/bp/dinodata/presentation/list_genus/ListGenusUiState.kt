package com.bp.dinodata.presentation.list_genus

import com.bp.dinodata.data.IResultsByLetter
import com.bp.dinodata.data.search.ISearch
import com.bp.dinodata.data.search.emptySearch
import com.bp.dinodata.data.filters.IFilter
import com.bp.dinodata.data.genus.IGenus
import com.bp.dinodata.data.genus.IGenusWithPrefs
import com.bp.dinodata.presentation.LoadState

data class ListGenusUiState(
    private val allPageData: IResultsByLetter<IGenusWithPrefs>? = null,
    val loadState: LoadState = LoadState.NotLoading,
    val searchResults: List<IGenus>? = null,
    val searchBarVisible: Boolean = false,
    val search: ISearch<IGenus> = emptySearch(),
    val pageSelectionVisible: Boolean = true,
    val selectedPageIndex: Int = 0,
    val searchBarCursorAtEnd: Boolean = false
) {
    val keys: List<Char> = allPageData?.getKeys() ?: emptyList()

    fun applySearch(search: ISearch<IGenus>, jumpCursorToEnd: Boolean = false): ListGenusUiState {
        val allData = allPageData?.toList() ?: emptyList()
        val filter: IFilter<IGenus> = search.toFilter()
        val filteredGenera = filter.applyTo(allData)

        return this.copy(
            search = search,
            searchResults = filteredGenera,
            searchBarCursorAtEnd = jumpCursorToEnd
        )
    }

    fun getPageByIndex(index: Int): List<IGenusWithPrefs>? {
        return allPageData?.getGroupByIndex(index)
    }
}