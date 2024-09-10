package com.bp.dinodata.presentation.list_genus

import androidx.compose.ui.text.TextRange


interface ITextFieldState {
    fun getHintText(): String?
    fun getTextContent(): String
}

data class TextFieldState(
    val hintContent: String = DEFAULT_HINT,
    val text: String = "",
    val isHintVisible: Boolean = false,
    val textSelection: TextRange = TextRange.Zero,
    val canAcceptHint: Boolean = false,
    val isFocused: Boolean = false
): ITextFieldState {
    companion object {
        const val DEFAULT_HINT = "start typing for suggestions..."
    }

    fun clearText(): TextFieldState {
        return TextFieldState(
            text = "",
            textSelection = TextRange.Zero,
            isHintVisible = true
        )
    }

    override fun getHintText(): String = hintContent
    override fun getTextContent(): String = text
}