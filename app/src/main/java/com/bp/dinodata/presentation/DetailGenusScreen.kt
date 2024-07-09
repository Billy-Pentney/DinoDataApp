package com.bp.dinodata.presentation

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DetailGenusScreen(
    detailGenusViewModel: DetailGenusViewModel
) {
    val genusState by detailGenusViewModel.getVisibleGenus().collectAsState()

    genusState?.let {
        GenusDetail(
            genus = it,
            modifier = Modifier.padding(8.dp),
            onPlayNamePronunciation = {
                detailGenusViewModel.onEvent(DetailGenusUiEvent.PlayNamePronunciation)
            }
        )
    }
}