package com.bp.dinodata.presentation.list_genus

import androidx.compose.runtime.State
import kotlinx.coroutines.flow.Flow

interface IListGenusViewModel {
    fun getUiState(): State<ListGenusUiState>
    fun onUiEvent(event: ListGenusPageUiEvent)
    fun getToastFlow(): Flow<String>
}