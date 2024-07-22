package com.bp.dinodata.presentation.list_genus

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.bp.dinodata.data.IResultsByLetter
import com.bp.dinodata.data.filters.IFilter
import com.bp.dinodata.data.genus.IGenus
import com.bp.dinodata.data.genus.IGenusWithPrefs
import com.bp.dinodata.data.search.GenusSearch
import com.bp.dinodata.presentation.LoadState

data class ListGenusUiState(
    private val allPageData: IResultsByLetter<IGenusWithPrefs>? = null,
    val loadState: LoadState = LoadState.NotLoading,
    val searchResults: List<IGenus>? = null,
    val searchBarVisible: Boolean = false,
    val searchQueryText: String = "",
    val search: GenusSearch = GenusSearch(searchQueryText),
    val pageSelectionVisible: Boolean = true,
    val selectedPageIndex: Int = 0,
    val cursorRange: TextRange = TextRange.Zero
): ISearchBarUiState {
    val keys: List<Char> = allPageData?.getKeys() ?: emptyList()

    fun applySearch(
        search: GenusSearch = this.search.copy(query=searchQueryText)
    ): ListGenusUiState {
        val allData = allPageData?.toList() ?: emptyList()
        val filter: IFilter<IGenus> = search.toFilter()
        val filteredGenera = filter.applyTo(allData)

        return this.copy(
            search = search,
            searchResults = filteredGenera,
        )
    }

    fun getPageByIndex(index: Int): List<IGenusWithPrefs>? {
        return allPageData?.getGroupByIndex(index)
    }

    fun clearSearch(): ListGenusUiState {
        return this.copy(
            searchQueryText = "",
            cursorRange = TextRange.Zero
        )
    }

    override fun getSearchQuery(): String {
        return searchQueryText
    }

    override fun getCursorPosition(): TextRange {
        return cursorRange
    }

    override fun getSearchSuggestionAutofill(): String {
        return getSearchQuery() + (search.getSuggestedSuffixes().getOrNull(0) ?: "")
    }

    override fun getTextFieldValue(): TextFieldValue {
        return TextFieldValue(
            getSearchQuery(),
            getCursorPosition()
        )
    }

    override fun hasSuggestions(): Boolean {
        return search.getSuggestedSuffixes().isNotEmpty()
    }
}