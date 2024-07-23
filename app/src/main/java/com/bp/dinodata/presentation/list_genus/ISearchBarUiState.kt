package com.bp.dinodata.presentation.list_genus

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.bp.dinodata.data.genus.IGenus
import com.bp.dinodata.data.search.ISearchTerm

interface ISearchBarUiState {
    fun getFullSearchQuery(): String
    fun getSearchQuery(): String
    fun getCursorPosition(): TextRange
    fun getSearchSuggestionAutofill(): String
    fun getTextFieldValue(): TextFieldValue
    fun hasSuggestions(): Boolean
    fun getSearchTerms(): List<ISearchTerm<in IGenus>>
    fun removeSearchTerm(term: ISearchTerm<in IGenus>): ListGenusUiState
    fun getCompletedSearchTerms(): List<ISearchTerm<in IGenus>>
}