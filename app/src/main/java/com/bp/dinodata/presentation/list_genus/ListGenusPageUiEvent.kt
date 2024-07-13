package com.bp.dinodata.presentation.list_genus

sealed class ListGenusPageUiEvent {

    data object InitiateNextPageLoad : ListGenusPageUiEvent()

    data class UpdateSearchQuery(
        val query: String,
        val capSensitive: Boolean = false
    ) : ListGenusPageUiEvent()

    data class ToggleSearchBar(val visible: Boolean) : ListGenusPageUiEvent()
    data object ClearSearchQueryOrHideBar : ListGenusPageUiEvent()
}