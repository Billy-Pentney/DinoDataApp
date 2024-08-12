package com.bp.dinodata.presentation.list_genus

import androidx.compose.ui.text.TextRange
import com.bp.dinodata.data.IResultsByLetter
import com.bp.dinodata.data.genus.IGenus
import com.bp.dinodata.data.genus.IGenusWithPrefs
import com.bp.dinodata.data.search.GenusSearch
import com.bp.dinodata.data.search.GenusSearchBuilder
import com.bp.dinodata.data.search.terms.ISearchTerm
import com.bp.dinodata.presentation.DataState
import com.bp.dinodata.presentation.map

data class ListGenusUiState(
    val allPageData: DataState<IResultsByLetter<IGenusWithPrefs>> = DataState.Idle(),
    val searchResults: DataState<out List<IGenus>> = DataState.Idle(),
    val searchBarVisible: Boolean = false,
    private val search: GenusSearch = GenusSearch(),
    val pageSelectionVisible: Boolean = true,
    val selectedPageIndex: Int = 0,
    private val textFieldState: TextFieldState = TextFieldState(hintContent = DEFAULT_HINT_TEXT),
    val firstVisibleItem: Int = 0,
    val firstVisibleItemOffset: Int = 0,
    private val locations: List<String> = emptyList(),
    private val taxa: List<String> = emptyList()
): ISearchBarUiState {
    val letterKeys: List<Char> = getDataOrNull()?.getKeys() ?: emptyList()

    companion object {
        const val DEFAULT_HINT_TEXT = "start typing for suggestions..."
    }

    override fun getSearchTextFieldState(): TextFieldState {
        return textFieldState
    }

    fun getDataOrNull(): IResultsByLetter<IGenusWithPrefs>? {
        if (allPageData is DataState.Success) {
            return allPageData.data
        }
        return null
    }

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
            canAutofillHint = true
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
    fun makeNewSearch(
        locations: List<String> = this.locations,
        taxa: List<String> = this.taxa
    ): ListGenusUiState {
        val newSearch = GenusSearchBuilder(
            query = this.textFieldState.textContent,
            terms = search.getCompletedTerms(),
            locations = locations,
            taxa = taxa,
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
}