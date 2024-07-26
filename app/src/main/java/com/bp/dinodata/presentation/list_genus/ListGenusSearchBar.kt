package com.bp.dinodata.presentation.list_genus

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.Alignment
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bp.dinodata.data.genus.IGenus
import com.bp.dinodata.data.search.DietSearchTerm
import com.bp.dinodata.data.search.BasicSearchTerm
import com.bp.dinodata.data.search.GenusSearchBuilder
import com.bp.dinodata.data.search.ISearchTerm
import com.bp.dinodata.data.search.LocationSearchTerm
import com.bp.dinodata.theme.DinoDataTheme
import com.bp.dinodata.theme.MyGrey600

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListGenusSearchBar(
    updateSearchQuery: (TextFieldValue) -> Unit,
    clearSearchQuery: () -> Unit,
    prefillSearchSuggestion: () -> Unit,
    modifier: Modifier = Modifier,
    onSearchTermTap: (ISearchTerm<in IGenus>) -> Unit,
    uiState: ISearchBarUiState
) {
    val focusManager = LocalFocusManager.current

    val textFieldColors = TextFieldDefaults.colors(
        unfocusedContainerColor = MaterialTheme.colorScheme.background,
        focusedContainerColor = MaterialTheme.colorScheme.background,
        focusedTextColor = MaterialTheme.colorScheme.onSurface,
        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
        disabledTextColor = MaterialTheme.colorScheme.onSurface,
        cursorColor = MaterialTheme.colorScheme.onSurface,
        focusedIndicatorColor = MaterialTheme.colorScheme.onSurface,
        unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface,
    )

    val interactionSource = remember { MutableInteractionSource() }
    val textStyle = TextStyle(
        fontSize = 18.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface
    )

    var searchSuggestion by remember { mutableStateOf("") }
    var imeAction by remember { mutableStateOf(ImeAction.Search) }

    var acceptSuggestionAsQuery by remember { mutableStateOf(false) }
    var textFieldValue by remember { mutableStateOf(TextFieldValue()) }

    SideEffect {
        textFieldValue = textFieldValue.copy(
            text = uiState.getQuery()
        )
        if (uiState.hasSuggestions())
            imeAction = ImeAction.Next
        searchSuggestion = uiState.getAutofillSuggestion()
    }

    LaunchedEffect(acceptSuggestionAsQuery) {
        if (acceptSuggestionAsQuery) {
            acceptSuggestionAsQuery = false
            textFieldValue = textFieldValue.copy(
                text = searchSuggestion,
                selection = TextRange(searchSuggestion.length)
            )
            prefillSearchSuggestion()
        }
    }

    Column (modifier = Modifier.fillMaxWidth()) {
        val searchTerms = uiState.getCompletedSearchTerms()
        LazyColumn (
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            items(searchTerms) { term ->
                InputChip(
                    selected = false,
                    onClick = { onSearchTermTap(term) },
                    label = {
                        Text(
                            term.toString(),
                            modifier = Modifier.padding(vertical=4.dp),
                            maxLines = 2
                        )
                    },
                    leadingIcon = {
                        val icon = term.getIconId()
                        icon?.let {
                            Icon(icon, "null")
                        }
                    },
                    trailingIcon = {
                       Icon(
                           Icons.Filled.Close, "remove term"
                       )
                    }
                )
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
                                if (textFieldValue.text.isNotEmpty()) {
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
}


@Preview
@Composable
fun PreviewSearchBar() {
    DinoDataTheme {
        ListGenusSearchBar(
            updateSearchQuery = {},
            clearSearchQuery = { },
            prefillSearchSuggestion = { },
            onSearchTermTap = {},
            uiState = ListGenusUiState(
                searchBarVisible = true,
                search = GenusSearchBuilder(
                    "xyz",
                    taxa = listOf("abelisauridae", "brachiosauridae"),
                    terms = listOf<ISearchTerm<in IGenus>>(
                        BasicSearchTerm("abc"),
                        DietSearchTerm("diet:carnivore"),
                        LocationSearchTerm(
                            "location:canada+united_kingdom+usa+brazil"
                        )
                    )
                ).build()
            )
        )
    }
}