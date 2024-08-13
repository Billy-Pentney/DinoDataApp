package com.bp.dinodata.presentation.list_genus

import android.provider.ContactsContract.Data
import androidx.compose.ui.text.TextRange
import com.bp.dinodata.data.IResultsByLetter
import com.bp.dinodata.data.genus.IGenus
import com.bp.dinodata.data.genus.IGenusWithPrefs
import com.bp.dinodata.data.search.GenusSearch
import com.bp.dinodata.data.search.GenusSearchBuilder
import com.bp.dinodata.data.search.terms.ISearchTerm
import com.bp.dinodata.presentation.DataState
import com.bp.dinodata.presentation.map

enum class ListGenusContentMode {
    Search, Pager
}

interface IListGenusUiState: IMutableSearchBarUiState, IListGenusPageUiState {
    fun getDataState(): DataState<IResultsByLetter<IGenusWithPrefs>>
    fun getContentMode(): ListGenusContentMode
    fun hideSearchBar(): IListGenusUiState
}

/**
 * Manages the state of the UI for the ListGenusScreen.
 */
data class ListGenusUiState(
    val allPageData: DataState<IResultsByLetter<IGenusWithPrefs>> = DataState.Idle(),
    val searchResults: DataState<out List<IGenus>> = DataState.Idle(),
    private val contentMode: ListGenusContentMode = ListGenusContentMode.Pager,
    private val firstVisibleItem: Int = 0,
    private val firstVisibleItemOffset: Int = 0,
    private val search: GenusSearch = GenusSearch(),
    val pageSelectionVisible: Boolean = true,
    private val selectedPageIndex: Int = 0,
    private val textFieldState: TextFieldState = TextFieldState(hintContent = DEFAULT_HINT_TEXT),
    private val locations: List<String> = emptyList(),
    private val taxa: List<String> = emptyList()
): IListGenusUiState {

    private val letterKeys: List<Char> = getDataOrNull()?.getKeys() ?: emptyList()

    private fun getDataOrNull(): IResultsByLetter<IGenusWithPrefs>? {
        if (allPageData is DataState.Success) {
            return allPageData.data
        }
        return null
    }

    override fun getDataState(): DataState<IResultsByLetter<IGenusWithPrefs>> {
        return allPageData
    }

    override fun getContentMode(): ListGenusContentMode = contentMode
    override fun hideSearchBar(): ListGenusUiState {
        return this.copy (contentMode = ListGenusContentMode.Pager)
    }

    companion object {
        const val DEFAULT_HINT_TEXT = "start typing for suggestions..."
    }

    override fun getSearchTextFieldState(): TextFieldState {
        return textFieldState
    }

    override fun getSearchResultsAsList(): List<IGenus> {
        return when (searchResults) {
            is DataState.Success -> searchResults.data
            else -> emptyList()
        }
    }

    override fun getFirstVisibleItemIndex(): Int = firstVisibleItem
    override fun getFirstVisibleItemOffset(): Int = firstVisibleItemOffset

    /**
     * Update the SearchBar TextFieldState based on the given search. This involves setting
     * whether the hint (auto-fill suggestion) is visible, and indicating if the user
     * can accept the suggestion.
     * @param newSearch The new search which should be used to update the text-field;
     * defaults to the current search
     * @return A new TextFieldState with the content, hint and visibility constructed from the given
     * search.
     */
    private fun makeNewTextFieldState(
        newSearch: GenusSearch = search
    ): TextFieldState {
        val newQuery = newSearch.getQuery()
        val hasCompletedSearchTerms = newSearch.getCompletedTerms().isNotEmpty()
        val autofillSuggestion = newSearch.getAutofillSuggestion()

        // Now update the TextFieldState to reflect the constructed search.
        // This will remove the text which has been converted to SearchTerms.

        val canAutofillHint: Boolean
        val hintText: String
        val hintVisible: Boolean

        if (newQuery.isEmpty() && !hasCompletedSearchTerms) {
            hintText = DEFAULT_HINT_TEXT
            canAutofillHint = false
            hintVisible = true
        }
        else {
            hintText = autofillSuggestion
            canAutofillHint = autofillSuggestion.isNotEmpty()
            hintVisible = hintText.isNotEmpty()
        }

        return textFieldState.copy(
            textContent = newQuery,
            hintContent = hintText,
            canAcceptHint = canAutofillHint,
            isHintVisible = hintVisible
        )
    }

    /**
     * Constructs a search from the current query text-box content and any existing terms.
     * @param locations A list of all known locations, to be used in the predictive auto-fill.
     * @param taxa A list of all known taxa (e.g. clades/families), to be used in the predictive auto-fill.
     * @return A new UiState with the constructed search and the resulting search items.
     */
    override fun makeNewSearch(
        locations: List<String>?,
        taxa: List<String>?
    ): ListGenusUiState {
        val newSearch = GenusSearchBuilder(
            query = this.textFieldState.textContent,
            terms = search.getCompletedTerms(),
            locations = locations ?: this.locations,
            taxa = taxa ?: this.taxa
        ).build()

        val newTextFieldState = this.makeNewTextFieldState(newSearch)

        return this.copy(
            // Update the visible query text with the last term in the search object.
            // This removes completed terms (i.e. those followed by a space).
            textFieldState = newTextFieldState,
            search = newSearch,
            searchResults = DataState.Idle()
        )
    }

    override fun runSearch(resetScroll: Boolean): ListGenusUiState {
        return this.copy (
            searchResults = allPageData.map { search.applyTo(it) },
            firstVisibleItem = if (resetScroll) 0 else this.firstVisibleItem,
            firstVisibleItemOffset = if (resetScroll) 0 else this.firstVisibleItemOffset
        )
    }


    override fun clearSearchTextField(): ListGenusUiState {
        return this.copy(
            textFieldState = this.textFieldState.clearText(),
            search = GenusSearch(search.getCompletedTerms()),
            searchResults = allPageData.map { it.toList() }
        )
    }

    override fun getFullQuery(): String = search.getFullQuery()
    override fun getQuery(): String = textFieldState.textContent
    override fun isQueryEmpty(): Boolean = search.isQueryEmpty()
    override fun getSuggestedSuffixes(): List<String> = search.getSuggestedSuffixes()
    override fun getAutofillSuggestion(): String = search.getAutofillSuggestion()
    override fun hasSuggestions(): Boolean = search.getSuggestedSuffixes().isNotEmpty()
    override fun getCompletedSearchTerms(): List<ISearchTerm<in IGenus>> =
        search.getCompletedTerms()

    override fun updateSearchQuery(
        newQuery: String,
        selection: TextRange
    ): ListGenusUiState {
        return this.copy(
            textFieldState = this.textFieldState.copy(
                textContent = newQuery,
                textSelection = selection
            )
        )
    }

    override fun removeSearchTerm(term: ISearchTerm<in IGenus>): ListGenusUiState {
        val newSearch = search.removeTerm(term)
        return this.copy(
            search = newSearch
        )
    }

    override fun isSearchBarVisible(): Boolean = contentMode == ListGenusContentMode.Search

    override fun getPagerKeys(): List<String> {
        // These are the keys which identify the page of elements to show.
        // Currently, this is the letters A-Z
        return letterKeys.map { char -> char.toString() }
    }

    override fun getCurrentPagerIndex(): Int = selectedPageIndex

    override fun getPageByIndex(index: Int): List<IGenusWithPrefs>? {
        return getDataOrNull()?.getGroupByIndex(index)
    }
}