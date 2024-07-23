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
//    val textFieldValue: TextFieldValue = TextFieldValue(),
    val search: GenusSearch = GenusSearch(),
    val pageSelectionVisible: Boolean = true,
    val selectedPageIndex: Int = 0,
): ISearchBarUiState {
    val keys: List<Char> = allPageData?.getKeys() ?: emptyList()

    private val allPageDataList = allPageData?.toList() ?: emptyList()

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
//            textFieldValue = textFieldValue.copy(
//                text = search.getQuery()
//            ),
            search = search,
            searchResults = search.applyTo(allPageDataList),
        )
    }

    fun getPageByIndex(index: Int): List<IGenusWithPrefs>? {
        return allPageData?.getGroupByIndex(index)
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