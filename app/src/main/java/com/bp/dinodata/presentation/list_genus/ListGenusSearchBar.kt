package com.bp.dinodata.presentation.list_genus

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.bp.dinodata.R
import com.bp.dinodata.presentation.utils.DividerTextRow
import com.bp.dinodata.theme.MyGrey600

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListGenusSearchBar(
    searchBarQuery: State<String>,
    searchBarVisibility: State<Boolean>,
    toggleVisibility: (Boolean) -> Unit,
    updateSearchQuery: (String) -> Unit,
    clearSearchQuery: () -> Unit,
    numCreaturesVisible: Int
) {
    val visibleSearchBar by remember { searchBarVisibility }
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AnimatedVisibility(visibleSearchBar) {
            SearchBar(
                query = searchBarQuery.value,
                onQueryChange = updateSearchQuery,
                onSearch = {
                    updateSearchQuery(it)
                    // Hide the keyboard and drop focus
                    focusManager.clearFocus()
                },
                placeholder = {
                    Text(stringResource(R.string.search_box_hint), fontStyle = FontStyle.Italic)
                },
                active = false,
                onActiveChange = {},
                leadingIcon = {
                    Icon(Icons.Outlined.Search, "search")
                },
                trailingIcon = {
                    IconButton(onClick = {
                        clearSearchQuery()
                        toggleVisibility(false)
                    }) {
                        Icon(
                            Icons.Filled.Close,
                            "clear search",
                            tint = Color.White
                        )
                    }
                },
                colors = SearchBarDefaults.colors(
                    containerColor = MyGrey600
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
            }
        }
        DividerTextRow(
            text = stringResource(R.string.text_showing_X_creatures, numCreaturesVisible),
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp),
            dividerPadding = PaddingValues(horizontal = 8.dp)
        )
    }
}