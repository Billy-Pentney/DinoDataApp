package com.bp.dinodata.presentation.list_genus

import androidx.compose.ui.text.input.TextFieldValue

sealed class ListGenusPageUiEvent {

    data class UpdateSearchQuery(
        val state: TextFieldValue,
        val capSensitive: Boolean = false
    ) : ListGenusPageUiEvent()

    data class ToggleSearchBar(val visible: Boolean) : ListGenusPageUiEvent()
    data object ClearSearchQueryOrHideBar : ListGenusPageUiEvent()
    data object AcceptSearchSuggestion : ListGenusPageUiEvent()

    data class SwitchToPage(val pageIndex: Int): ListGenusPageUiEvent()
}