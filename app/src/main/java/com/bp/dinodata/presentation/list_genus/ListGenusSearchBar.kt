package com.bp.dinodata.presentation.list_genus

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults.Container
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bp.dinodata.theme.MyGrey600

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListGenusSearchBar(
    toggleVisibility: (Boolean) -> Unit,
    updateSearchQuery: (TextFieldValue) -> Unit,
    clearSearchQuery: () -> Unit,
    searchSuggestions: List<String>,
    prefillSearchSuggestion: () -> Unit,
    modifier: Modifier = Modifier,
    uiState: ISearchBarUiState
) {
    val focusManager = LocalFocusManager.current

    val textFieldColors = TextFieldDefaults.colors(
        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
        focusedContainerColor = MaterialTheme.colorScheme.surface,
        focusedTextColor = MaterialTheme.colorScheme.onSurface,
        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
        disabledTextColor = MaterialTheme.colorScheme.onSurface,
        cursorColor = MaterialTheme.colorScheme.onSurface,
        focusedIndicatorColor = MaterialTheme.colorScheme.onSurface,
        unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface
    )

    val interactionSource = remember { MutableInteractionSource() }
    val textStyle = TextStyle(
        fontSize = 18.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface
    )

    val searchBarQuery = uiState.getSearchQuery()
    val searchSuggestion = uiState.getSearchSuggestionAutofill()
    val cursorRange = uiState.getCursorPosition()

    var imeAction by remember { mutableStateOf(ImeAction.Next) }

    var textFieldValue by remember { mutableStateOf(TextFieldValue()) }

    LaunchedEffect(uiState) {
        textFieldValue = textFieldValue.copy(
            text = searchBarQuery,
            selection = cursorRange
        )
        imeAction = if (uiState.hasSuggestions()) {
            ImeAction.Next
        } else {
            ImeAction.Search
        }
    }

    var acceptSuggestionAsQuery by remember { mutableStateOf(false) }

    LaunchedEffect(acceptSuggestionAsQuery) {
        if (acceptSuggestionAsQuery) {
            prefillSearchSuggestion()
            acceptSuggestionAsQuery = false
            imeAction = if (searchSuggestions.isNotEmpty())
                ImeAction.Next
            else
                ImeAction.Search
        }
    }

    SearchBar(
        inputField = {
            BasicTextField(
                value = textFieldValue,
                onValueChange = {
                    textFieldValue = it
                    updateSearchQuery(it)
                },
                decorationBox = { innerTextField ->
                    TextFieldDefaults.DecorationBox(
                        value = textFieldValue.text,
                        innerTextField = {
                            Box {
                                Text(
                                    searchSuggestion,
                                    modifier = Modifier.alpha(0.4f),
                                    style = textStyle.copy(fontWeight = FontWeight.Normal)
                                )
                                innerTextField()
                            }
                        },
                        colors = textFieldColors,
                        interactionSource = interactionSource,
                        leadingIcon = {
                            Icon(
                                Icons.Outlined.Search,
                                "search",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(8.dp)
                            )
                        },
                        trailingIcon = {
                            if (searchBarQuery.isNotEmpty()) {
                                IconButton(onClick = { clearSearchQuery() }) {
                                    Icon(
                                        Icons.Filled.Close,
                                        "clear search",
                                        tint = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        },
                        enabled = true,
                        singleLine = true,
                        visualTransformation = VisualTransformation.None,
                        container = {
                            Container(
                                enabled = true,
                                interactionSource = interactionSource,
                                isError = false,
                                colors = textFieldColors,
                                shape = RoundedCornerShape(32.dp)
                            )
                        }
                    )
                },
                textStyle = textStyle,
                modifier = Modifier.fillMaxWidth(),
                enabled = true,
                keyboardActions = KeyboardActions(
                    onSearch = { focusManager.clearFocus() },
                    onNext = { acceptSuggestionAsQuery = true }
                ),
                keyboardOptions = KeyboardOptions(
                    autoCorrect = false,
                    imeAction = imeAction
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface)
            )
        },
        expanded = false,
        onExpandedChange = { },
        modifier = modifier.fillMaxWidth(),
        shape = SearchBarDefaults.inputFieldShape,
        colors = SearchBarDefaults.colors(
            containerColor = MyGrey600
        ),
        tonalElevation = SearchBarDefaults.TonalElevation,
        shadowElevation = SearchBarDefaults.ShadowElevation,
        windowInsets = SearchBarDefaults.windowInsets
    ) {

    }
}