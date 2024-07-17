package com.bp.dinodata.presentation.list_genus

import android.widget.AutoCompleteTextView
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults.Container
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bp.dinodata.R
import com.bp.dinodata.theme.MyGrey600
import org.w3c.dom.Text

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListGenusSearchBar(
    searchBarQuery: String,
    toggleVisibility: (Boolean) -> Unit,
    updateSearchQuery: (String) -> Unit,
    clearSearchQuery: () -> Unit,
    searchSuggestions: List<String>,
    prefillSearchSuggestion: () -> Unit,
    modifier: Modifier = Modifier,
    searchBarCursorAtEnd: Boolean
) {
    val focusManager = LocalFocusManager.current

    val inputFieldColors = SearchBarDefaults.inputFieldColors(
        cursorColor = MaterialTheme.colorScheme.onSurface
    )
    val textFieldColors = TextFieldDefaults.colors(
        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
        focusedContainerColor = MaterialTheme.colorScheme.surface,
        focusedTextColor = MaterialTheme.colorScheme.onSurface,
        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
        disabledTextColor = MaterialTheme.colorScheme.onSurface,
        cursorColor = MaterialTheme.colorScheme.onSurface
    )

    val interactionSource = remember { MutableInteractionSource() }
    val textStyle = TextStyle(
        fontSize = 18.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface
    )

    val searchSuggestion = searchBarQuery + (searchSuggestions.getOrNull(0) ?: "")

//    var textFieldValueState by remember {
//        mutableStateOf(
//            TextFieldValue(
//                text = searchBarQuery,
//                selection = TextRange.Zero
//            )
//        )
//    }
//
//    SideEffect {
//        if (searchBarCursorAtEnd) {
//            textFieldValueState = textFieldValueState.copy(
//                text = searchBarQuery,
//                selection = TextRange(searchBarQuery.length, searchBarQuery.length)
//            )
//        }
//    }

    SearchBar(
        inputField = {
            BasicTextField(
                value = searchBarQuery,
                onValueChange = updateSearchQuery,
                decorationBox = { innerTextField ->
                    TextFieldDefaults.DecorationBox(
                        value = searchBarQuery,
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
//                inputFieldColors = TextFieldDefaults.colors(
//                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
//                ),
                modifier = Modifier.fillMaxWidth(),
                enabled = true,
                keyboardActions = KeyboardActions(
                    onSearch = {
                        if (searchSuggestions.isNotEmpty()) {
                            prefillSearchSuggestion()
                        }
                        focusManager.clearFocus()
                    },
//                    onNext = {
//                        if (searchSuggestions.isNotEmpty()) {
//                            prefillSearchSuggestion()
//                        }
//                    }
                ),
                keyboardOptions = KeyboardOptions(
                    autoCorrect = false,
                    imeAction = ImeAction.Search
                )
            )

//            SearchBarDefaults.InputField(
//                query = searchBarQuery,
//                onQueryChange = updateSearchQuery,
//                onSearch = {
//                    updateSearchQuery(it)
//                    // Hide the keyboard and drop focus
//                    focusManager.clearFocus()
//                },
//                expanded = false,
//                onExpandedChange = {  },
//                enabled = true,
//                placeholder = {
//                    Text(stringResource(R.string.search_box_hint), fontStyle = FontStyle.Italic)
//                },
//                leadingIcon = {  },
//                trailingIcon = {
//
//                },
//                colors = inputFieldColors,
//                interactionSource = null,
//            )
        },
        expanded = false,
        onExpandedChange = { },
        modifier = modifier.fillMaxWidth(),
        shape = SearchBarDefaults.inputFieldShape,
        colors = SearchBarDefaults.colors(containerColor = MyGrey600),
        tonalElevation = SearchBarDefaults.TonalElevation,
        shadowElevation = SearchBarDefaults.ShadowElevation,
        windowInsets = SearchBarDefaults.windowInsets
    ) {

    }
}