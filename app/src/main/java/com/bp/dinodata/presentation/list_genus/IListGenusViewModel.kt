package com.bp.dinodata.presentation.list_genus

import androidx.compose.runtime.State
import kotlinx.coroutines.flow.Flow

interface IListGenusViewModel {
    /** Return the current state of the UI for the List-Genus Screen. */
    fun getUiState(): State<ListGenusUiState>

    /** Respond to user-initiated events. */
    fun onUiEvent(event: ListGenusPageUiEvent)

    /** Get a flow which emits text to be shown as Toast messages. */
    fun getToastFlow(): Flow<String>
}