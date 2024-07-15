package com.bp.dinodata.presentation.list_genus

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import com.bp.dinodata.R
import com.bp.dinodata.theme.MyGrey600

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListGenusSearchBar(
    searchBarQuery: String,
    toggleVisibility: (Boolean) -> Unit,
    updateSearchQuery: (String) -> Unit,
    clearSearchQuery: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    val inputFieldColors = SearchBarDefaults.inputFieldColors(
        cursorColor = MaterialTheme.colorScheme.onSurface
    )

    SearchBar(
        inputField = {
            SearchBarDefaults.InputField(
                query = searchBarQuery,
                onQueryChange = updateSearchQuery,
                onSearch = {
                    updateSearchQuery(it)
                    // Hide the keyboard and drop focus
                    focusManager.clearFocus()
                },
                expanded = false,
                onExpandedChange = {  },
                enabled = true,
                placeholder = {
                    Text(stringResource(R.string.search_box_hint), fontStyle = FontStyle.Italic)
                },
                leadingIcon = {
                    Icon(Icons.Outlined.Search, "search")
                },
                trailingIcon = {
                    if (searchBarQuery.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                clearSearchQuery()
                                toggleVisibility(false)
                            }
                        ) {
                            Icon(
                                Icons.Filled.Close,
                                "clear search",
                                tint = Color.White
                            )
                        }
                    }
                },
                colors = inputFieldColors,
                interactionSource = null,
            )
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