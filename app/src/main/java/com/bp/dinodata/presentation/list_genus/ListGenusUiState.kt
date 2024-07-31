package com.bp.dinodata.presentation.list_genus

import com.bp.dinodata.data.IResultsByLetter
import com.bp.dinodata.data.genus.IGenus
import com.bp.dinodata.data.genus.IGenusWithPrefs
import com.bp.dinodata.data.search.GenusSearch
import com.bp.dinodata.data.search.GenusSearchBuilder
import com.bp.dinodata.data.search.ISearchTerm
import com.bp.dinodata.presentation.DataState
import com.bp.dinodata.presentation.map

data class ListGenusUiState(
    val allPageData: DataState<IResultsByLetter<IGenusWithPrefs>> = DataState.Idle(),
    val searchResults: DataState<out List<IGenus>> = DataState.Idle(),
    val searchBarVisible: Boolean = false,
    val search: GenusSearch = GenusSearch(),
    val pageSelectionVisible: Boolean = true,
    val selectedPageIndex: Int = 0,
    val currentQueryText: String = "",
    val firstVisibleItem: Int = 0,
    val firstVisibleItemOffset: Int = 0,
    private val locations: List<String> = emptyList(),
    private val taxa: List<String> = emptyList()
): ISearchBarUiState {
    val letterKeys: List<Char> = getDataOrNull()?.getKeys() ?: emptyList()

    fun getDataOrNull(): IResultsByLetter<IGenusWithPrefs>? {
        if (allPageData is DataState.Success) {
            return allPageData.data
        }
        return null
    }

    /**
     * Constructs a search from the current query text-box content and any existing terms.
     * @param locations A list of all known locations, to be used in the predictive auto-fill.
     * @param taxa A list of all known taxa (e.g. clades/families), to be used in the predictive auto-fill.
     * @return A new UiState with the constructed search and the resulting search items.
     */
    fun makeNewSearch(
        locations: List<String> = this.locations,
        taxa: List<String> = this.taxa
    ): ListGenusUiState {
        val search = GenusSearchBuilder(
            query = this.currentQueryText,
            terms = search.getCompletedTerms(),
            locations = locations,
            taxa = taxa,
        ).build()

        return this.copy(
            // Update the visible query text with the last term in the search object.
            // This removes completed terms (i.e. those followed by a space).
            currentQueryText = search.getQuery(),
            search = search,
            searchResults = DataState.Idle()
        )
    }

    override fun runSearch(): ListGenusUiState {
        return this.copy (
            searchResults = allPageData.map { search.applyTo(it) },
            firstVisibleItem = 0,
            firstVisibleItemOffset = 0
        )
    }

    fun getPageByIndex(index: Int): List<IGenusWithPrefs>? {
        return getDataOrNull()?.getGroupByIndex(index)
    }

    fun clearSearch(): ListGenusUiState {
        return this.copy(
            currentQueryText = "",
            search = GenusSearch(search.getCompletedTerms()),
            searchResults = allPageData.map { it.toList() }
        )
    }

    override fun getFullQuery(): String = search.getFullQuery()
    override fun getQuery(): String = currentQueryText
    override fun isQueryEmpty(): Boolean = search.isQueryEmpty()
    override fun getSuggestedSuffixes(): List<String> = search.getSuggestedSuffixes()
    override fun getAutofillSuggestion(): String = search.getAutofillSuggestion()
    override fun hasSuggestions(): Boolean = search.getSuggestedSuffixes().isNotEmpty()
    override fun getCompletedSearchTerms(): List<ISearchTerm<in IGenus>> =
        search.getCompletedTerms()

    override fun updateSearchQuery(newQuery: String): ListGenusUiState {
        return this.copy(
            currentQueryText = newQuery
        )
    }

    override fun removeSearchTerm(term: ISearchTerm<in IGenus>): ListGenusUiState {
        val newSearch = search.removeTerm(term)
        return this.copy(
            search = newSearch
        )
    }
}