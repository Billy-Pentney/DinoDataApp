package com.bp.dinodata.presentation.list_genus

import androidx.compose.ui.text.TextRange
import com.bp.dinodata.data.IResultsByLetter
import com.bp.dinodata.data.genus.IGenus
import com.bp.dinodata.data.genus.IGenusWithPrefs
import com.bp.dinodata.data.search.BlankSearch
import com.bp.dinodata.data.search.ISearch
import com.bp.dinodata.data.search.terms.ISearchTerm
import com.bp.dinodata.presentation.DataState

data class ListGenusSearchUiState(
    val searchResults: DataState<out List<IGenus>> = DataState.Idle(),
    private val firstVisibleItem: Int = 0,
    private val firstVisibleItemOffset: Int = 0,
    private val search: ISearch<IGenus> = BlankSearch(),
    private val searchBarTextFieldState: TextFieldState = TextFieldState(
        text = search.getQuery(),
        hintContent = ListGenusUiState.DEFAULT_HINT_TEXT
    )
): IMutableSearchBarUiState {
    override fun getSearchTextFieldState(): TextFieldState = searchBarTextFieldState

    override fun getSearchResultsAsList(): List<IGenus> {
        return when (searchResults) {
            is DataState.Success -> searchResults.data
            else -> emptyList()
        }
    }

    override fun getFirstVisibleItemIndex(): Int = firstVisibleItem
    override fun getFirstVisibleItemOffset(): Int = firstVisibleItemOffset

    override fun getSearch(): ISearch<IGenus> = search



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
        newSearch: ISearch<IGenus> = search
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
            hintText = ListGenusUiState.DEFAULT_HINT_TEXT
            canAutofillHint = false
            hintVisible = true
        }
        else {
            hintText = autofillSuggestion
            canAutofillHint = autofillSuggestion.isNotEmpty()
            hintVisible = hintText.isNotEmpty()
        }

        return searchBarTextFieldState.copy(
            text = newQuery,
            hintContent = hintText,
            canAcceptHint = canAutofillHint,
            isHintVisible = hintVisible
        )
    }

    override fun getFullQuery(): String = search.getFullQuery()
    override fun getQuery(): String = searchBarTextFieldState.getTextContent()
    override fun isQueryEmpty(): Boolean = search.isQueryEmpty()
    override fun getSuggestedSuffixes(): List<String> = search.getSuggestedSuffixes()
    override fun getAutofillSuggestion(): String = search.getAutofillSuggestion()
    override fun hasSuggestions(): Boolean = search.getSuggestedSuffixes().isNotEmpty()
    override fun getCompletedSearchTerms(): List<ISearchTerm<in IGenus>> =
        search.getCompletedTerms()

    override fun updateSearchTextState(
        newQueryText: String,
        selection: TextRange
    ): ListGenusSearchUiState {
        return this.copy(
            searchBarTextFieldState = this.searchBarTextFieldState.copy(
                text = newQueryText,
                textSelection = selection
            )
        )
    }

    override fun removeSearchTerm(term: ISearchTerm<in IGenus>): ListGenusSearchUiState {
        return this.copy(
            search = search.withoutTerm(term)
        )
    }


    override fun getSearchResultState(): DataState<out List<IGenus>> {
        return searchResults
    }

    override fun updateSearch(newSearch: ISearch<IGenus>): ListGenusSearchUiState {
        val newTextFieldState = this.makeNewTextFieldState(newSearch)
        return this.copy(
            // Update the visible query text with the last term in the search object.
            // This removes completed terms (i.e. those followed by a space).
            searchBarTextFieldState = newTextFieldState,
            search = newSearch,
            searchResults = DataState.LoadInProgress()
        )
    }

}