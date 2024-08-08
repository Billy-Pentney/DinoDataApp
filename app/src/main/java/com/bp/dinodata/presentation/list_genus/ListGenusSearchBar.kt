package com.bp.dinodata.presentation.list_genus

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults.Container
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bp.dinodata.data.genus.IGenus
import com.bp.dinodata.data.search.terms.DietSearchTerm
import com.bp.dinodata.data.search.terms.BasicSearchTerm
import com.bp.dinodata.data.search.GenusSearchBuilder
import com.bp.dinodata.data.search.terms.ISearchTerm
import com.bp.dinodata.data.search.terms.LocationSearchTerm
import com.bp.dinodata.theme.DinoDataTheme
import com.bp.dinodata.theme.MyGrey600


@Composable
fun<T> SearchTermInputChip(
    term: ISearchTerm<T>,
    onSearchTermTap: () -> Unit
) {
    InputChip(
        selected = false,
        onClick = onSearchTermTap,
        label = {
            Row (
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxHeight().padding(vertical=4.dp)
            ){
                Text(
                    term.toString(),
                    modifier = Modifier.padding(vertical = 6.dp),
                    maxLines = 2
                )
            }
        },
        leadingIcon = {
            val icon = term.getIconId()
            icon?.let {
                Icon(
                    icon, null,
                    tint = MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier.padding(vertical=8.dp)
                )
            }
        },
        trailingIcon = {
            Icon(
                Icons.Filled.Close,
                "remove term"
            )
        },
        colors = InputChipDefaults.inputChipColors(
            containerColor = MaterialTheme.colorScheme.secondary,
            labelColor = MaterialTheme.colorScheme.onSecondary
        )
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListGenusSearchBar(
    updateSearchQuery: (TextFieldValue) -> Unit,
    runSearch: () -> Unit,
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
    var keyboardOptions by remember { mutableStateOf(
        KeyboardOptions.Default.copy(
            autoCorrectEnabled = false,
            capitalization = KeyboardCapitalization.None,
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next
        )
    ) }

    var acceptSuggestionAsQuery by remember { mutableStateOf(false) }
    var textFieldValue by remember { mutableStateOf(TextFieldValue("")) }

    val currQuery = uiState.getQuery()
    var canAutofill = uiState.hasSuggestions()
    val searchTerms = uiState.getCompletedSearchTerms()

    if (currQuery.isEmpty() && searchTerms.isEmpty()) {
        searchSuggestion = "start typing for suggestions..."
        canAutofill = false
    }

//    keyboardOptions = keyboardOptions.copy(
        // This dynamic imeAction causes the keyboard to die
//        if (uiState.hasSuggestions()) {
//            ImeAction.Next
//        }
//        else {
//            ImeAction.Search
//        }
//    )

    LaunchedEffect(uiState) {
        textFieldValue = textFieldValue.copy(text = currQuery)
        searchSuggestion = uiState.getAutofillSuggestion()
    }

    LaunchedEffect(acceptSuggestionAsQuery) {
        if (acceptSuggestionAsQuery) {
            acceptSuggestionAsQuery = false
            if (canAutofill) {
                textFieldValue = textFieldValue.copy(
                    text = searchSuggestion,
                    selection = TextRange(searchSuggestion.length)
                )
                prefillSearchSuggestion()
            }
        }
    }

    Column (modifier = Modifier.fillMaxWidth()) {

//        Crossfade (searchTerms.isNotEmpty(), label="crossfadeSearchBarLabel") {
//        AnimatedVisibility (
//            searchTerms.isNotEmpty(),
//            modifier = Modifier.padding(horizontal = 16.dp)
//        ) {
//            Text("Active Filters",
//                color = MaterialTheme.colorScheme.onSurface,
//                fontWeight = FontWeight.SemiBold,
//                fontStyle = FontStyle.Italic
//            )
//        }
//        }

        LazyColumn (
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .animateContentSize()
        ) {
            items(searchTerms) { term ->
                SearchTermInputChip(
                    term,
                    onSearchTermTap = { onSearchTermTap(term) }
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
                        runSearch()
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
                        onSearch = {
                            updateSearchQuery(textFieldValue)
                            runSearch()
                            focusManager.clearFocus()
                        },
                        onNext = {
                            if (uiState.hasSuggestions()) {
                                acceptSuggestionAsQuery = true
                            }
                            else {
                                updateSearchQuery(textFieldValue)
                                runSearch()
                                focusManager.clearFocus()
                            }
                        }
                    ),
                    keyboardOptions = keyboardOptions,
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
        Surface {
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
                ),
                runSearch = {}
            )
        }
    }
}