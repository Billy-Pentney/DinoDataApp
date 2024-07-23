package com.bp.dinodata.presentation.list_genus

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.bp.dinodata.data.IResultsByLetter
import com.bp.dinodata.data.genus.IGenus
import com.bp.dinodata.data.genus.IGenusWithPrefs
import com.bp.dinodata.data.search.GenusSearch
import com.bp.dinodata.data.search.GenusSearchBuilder
import com.bp.dinodata.data.search.ISearchTerm
import com.bp.dinodata.presentation.LoadState

data class ListGenusUiState(
    private val allPageData: IResultsByLetter<IGenusWithPrefs>? = null,
    val loadState: LoadState = LoadState.NotLoading,
    val searchResults: List<IGenus>? = null,
    val searchBarVisible: Boolean = false,
    val searchQueryText: String = "",
    val search: GenusSearch = GenusSearch(),
    val pageSelectionVisible: Boolean = true,
    val selectedPageIndex: Int = 0,
    val cursorRange: TextRange = TextRange.Zero
): ISearchBarUiState {
    val keys: List<Char> = allPageData?.getKeys() ?: emptyList()

    private val allPageDataList = allPageData?.toList() ?: emptyList()

    fun applySearch(
        query: String = this.searchQueryText,
        locations: List<String> = emptyList(),
        taxa: List<String> = emptyList()
    ): ListGenusUiState {
        val search = GenusSearchBuilder(
            query = query,
            terms = search.getCompletedTerms(),
            locations = locations,
            taxa = taxa,
        ).build()

        return this.copy(
            searchQueryText = search.getQuery(),
            search = search,
            searchResults = search.applyTo(allPageDataList),
        )
    }

    fun getPageByIndex(index: Int): List<IGenusWithPrefs>? {
        return allPageData?.getGroupByIndex(index)
    }

    fun clearSearch(): ListGenusUiState {
        return this.copy(
            searchQueryText = "",
            search = GenusSearch(search.getCompletedTerms()),
            cursorRange = TextRange.Zero
        )
    }

    override fun getFullSearchQuery(): String {
        return search.getQuery()
    }

    override fun getSearchQuery(): String {
        return search.getLastTermText()
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

    override fun getSearchTerms(): List<ISearchTerm<in IGenus>> {
        return search.getAllTerms()
    }

    override fun getCompletedSearchTerms(): List<ISearchTerm<in IGenus>> {
        return search.getCompletedTerms()
    }

    override fun removeSearchTerm(term: ISearchTerm<in IGenus>): ListGenusUiState {
        val newSearch = search.removeTerm(term)
        return this.copy(
            search = newSearch,
            searchQueryText = newSearch.getLastTermText()
        )
    }
}