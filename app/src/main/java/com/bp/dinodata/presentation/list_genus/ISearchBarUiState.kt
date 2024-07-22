package com.bp.dinodata.presentation.list_genus

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue

interface ISearchBarUiState {
    fun getSearchQuery(): String
    fun getCursorPosition(): TextRange
    fun getSearchSuggestionAutofill(): String
    fun getTextFieldValue(): TextFieldValue
    fun hasSuggestions(): Boolean
}