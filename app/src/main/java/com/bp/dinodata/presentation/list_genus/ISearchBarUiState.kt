package com.bp.dinodata.presentation.list_genus

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.bp.dinodata.data.genus.IGenus
import com.bp.dinodata.data.search.IDataQuery
import com.bp.dinodata.data.search.ISearchTerm

interface ISearchBarUiState: IDataQuery {
//    fun getCursorPosition(): TextRange
//    fun getSearchBarTextFieldValue(): TextFieldValue
    fun hasSuggestions(): Boolean
    fun removeSearchTerm(term: ISearchTerm<in IGenus>): ListGenusUiState
    fun getCompletedSearchTerms(): List<ISearchTerm<in IGenus>>
}