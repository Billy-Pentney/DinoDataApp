package com.bp.dinodata.presentation.list_genus

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.text.input.TextFieldValue
import com.bp.dinodata.data.genus.IGenus
import com.bp.dinodata.data.search.terms.ISearchTerm

sealed class ListGenusPageUiEvent {

    data class UpdateSearchQuery(
        val state: TextFieldValue,
        val capSensitive: Boolean = false,
        val alsoRunSearch: Boolean = false
    ) : ListGenusPageUiEvent()

    data object RunSearch: ListGenusPageUiEvent()

    data object NavigateUp: ListGenusPageUiEvent()

    data class ToggleSearchBar(val visible: Boolean) : ListGenusPageUiEvent()
    data object ClearSearchQueryOrHideBar : ListGenusPageUiEvent()
    data object AcceptSearchSuggestion : ListGenusPageUiEvent()
    data object RefreshFeed : ListGenusPageUiEvent()

    data class SwitchToPage(val pageIndex: Int): ListGenusPageUiEvent()

    data class RemoveSearchTerm(val term: ISearchTerm<in IGenus>): ListGenusPageUiEvent()
    data class UpdateScrollState(val state: LazyListState) : ListGenusPageUiEvent()

    // Ui triggers a toast to be displayed with the given message
    data class ShowToast(val message: String) : ListGenusPageUiEvent()
    data class FocusSearchBar(val focused: Boolean) : ListGenusPageUiEvent()
}