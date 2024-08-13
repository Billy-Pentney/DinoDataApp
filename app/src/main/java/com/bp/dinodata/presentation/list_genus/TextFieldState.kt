package com.bp.dinodata.presentation.list_genus

import androidx.compose.ui.text.TextRange

data class TextFieldState(
    val textContent: String = "",
    val hintContent: String = "start typing for suggestions...",
    val isHintVisible: Boolean = false,
    val textSelection: TextRange = TextRange.Zero,
    val canAcceptHint: Boolean = false,
    val isFocused: Boolean = false
) {
    fun clearText(): TextFieldState {
        return TextFieldState(
            textContent = "",
            isHintVisible = true,
            textSelection = TextRange.Zero
        )
    }
}