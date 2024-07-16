package com.bp.dinodata.presentation.detail_genus

import android.util.Log
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bp.dinodata.presentation.LoadState
import com.bp.dinodata.presentation.utils.NoDataPlaceholder

@Composable
fun DetailGenusScreen(
    detailGenusViewModel: DetailGenusViewModel
) {
    val loadState by detailGenusViewModel.getLoadState()
    val genusState by detailGenusViewModel.getVisibleGenus().collectAsState()

    Scaffold { padding ->
        Crossfade(targetState = loadState, label = "GenusDetailCrossfade") {
            when (it) {
                LoadState.InProgress -> {
                    Log.d("DetailGenus", "Load in progress. Nothing to show")
                }
                LoadState.Loaded -> {
                    GenusDetail(
                        genus = genusState!!,
                        modifier = Modifier
                            .padding(padding)
                            .padding(horizontal = 8.dp),
                        onPlayNamePronunciation = {
                            detailGenusViewModel.onEvent(DetailGenusUiEvent.PlayNamePronunciation)
                        }
                    )
                }
                is LoadState.Error -> {
                    Log.e("DetailGenusScreen", "An error occurred when fetching the genus data.")
                    NoDataPlaceholder()
                }
                LoadState.NotLoading -> {
                    NoDataPlaceholder()
                }
            }
        }
    }
}