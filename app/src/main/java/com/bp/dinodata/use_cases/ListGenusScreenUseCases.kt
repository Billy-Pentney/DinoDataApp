package com.bp.dinodata.use_cases

import android.util.Log
import androidx.compose.ui.text.TextRange
import com.bp.dinodata.presentation.list_genus.ListGenusUiState

class ListGenusScreenUseCases {

    fun acceptSearchSuggestion(uiState: ListGenusUiState): ListGenusUiState {
        if (!uiState.hasSuggestions()) {
            Log.d("ListGenusUiState", "Unable to accept search suggestion. No suggestions present!")
            return uiState
        }

        val newQuery = uiState.getSearchSuggestionAutofill()
        return uiState.copy(
            searchQueryText = newQuery,
            cursorRange = TextRange(newQuery.length)
        )
    }
}