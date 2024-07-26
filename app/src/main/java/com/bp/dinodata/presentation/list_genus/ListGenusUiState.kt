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
): ISearchBarUiState {
    val keys: List<Char> = getData()?.getKeys() ?: emptyList()

    fun getData(): IResultsByLetter<IGenusWithPrefs>? {
        if (allPageData is DataState.Success) {
            return allPageData.data
        }
        return null
    }

    fun applySearch(
        query: String = getQuery(),
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
            search = search,
            searchResults = allPageData.map {
                search.applyTo(it)
            },
        )
    }

    fun getPageByIndex(index: Int): List<IGenusWithPrefs>? {
        return getData()?.getGroupByIndex(index)
    }

    fun clearSearch(): ListGenusUiState {
        return this.copy(
            search = GenusSearch(search.getCompletedTerms()),
        )
    }

    override fun getFullQuery(): String = search.getFullQuery()
    override fun getQuery(): String = search.getQuery()
    override fun isQueryEmpty(): Boolean = search.isQueryEmpty()
    override fun getSuggestedSuffixes(): List<String> = search.getSuggestedSuffixes()
    override fun getAutofillSuggestion(): String = search.getAutofillSuggestion()
    override fun hasSuggestions(): Boolean = search.getSuggestedSuffixes().isNotEmpty()
    override fun getCompletedSearchTerms(): List<ISearchTerm<in IGenus>> =
        search.getCompletedTerms()

    override fun removeSearchTerm(term: ISearchTerm<in IGenus>): ListGenusUiState {
        val newSearch = search.removeTerm(term)
        return this.copy(
            search = newSearch
        )
    }
}