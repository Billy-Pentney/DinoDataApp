package com.bp.dinodata.presentation.detail_genus

import android.util.Log
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bp.dinodata.presentation.LoadState
import com.bp.dinodata.presentation.utils.MissingDataPlaceholder
import com.bp.dinodata.presentation.utils.NoDataPlaceholder

@Composable
fun DetailGenusScreen(
    detailGenusViewModel: DetailGenusViewModel
) {
//    val genusState by detailGenusViewModel.getVisibleGenus().collectAsState()
    val uiState by remember { detailGenusViewModel.getUiState() }
    val loadState = uiState.loadState

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,

    ) { padding ->
        Crossfade(targetState = loadState, label = "GenusDetailCrossfade") {
            when (it) {
                LoadState.InProgress -> {
                    Log.d("DetailGenus", "Load in progress. Nothing to show")
//                    NoDataPlaceholder()
                }
                LoadState.Loaded -> {
                    GenusDetailScreenContent(
                        uiState = uiState,
                        modifier = Modifier
                            .padding(padding)
                            .padding(horizontal = 8.dp),
                        onPlayNamePronunciation = {
                            detailGenusViewModel.onEvent(DetailGenusUiEvent.PlayNamePronunciation)
                        },
                        onColorSelected = { colorName ->
                            detailGenusViewModel.onEvent(DetailGenusUiEvent.SelectColor(colorName))
                        },
                        setColorPickerDialogVisibility = { visible ->
                            detailGenusViewModel.onEvent(DetailGenusUiEvent.ShowColorSelectDialog(visible))
                        },
                        toggleItemAsFavourite = { isFavourite: Boolean ->
                            detailGenusViewModel.onEvent(DetailGenusUiEvent.ToggleItemFavouriteStatus(isFavourite))
                        }
                    )
                }
                is LoadState.Error -> {
                    Log.e("DetailGenusScreen", "An error occurred when fetching the genus data.")
                    MissingDataPlaceholder()
                }
                LoadState.NotLoading -> {
                    MissingDataPlaceholder()
                }
            }
        }
    }
}