package com.bp.dinodata.presentation.detail_genus

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.bp.dinodata.presentation.DataState
import com.bp.dinodata.presentation.utils.MissingDataPlaceholder
import kotlinx.coroutines.flow.collectLatest

@Composable
fun DetailGenusScreen(
    detailGenusViewModel: DetailGenusViewModel
) {
    val uiState by remember { detailGenusViewModel.getUiState() }
    val dialogState = remember { detailGenusViewModel.getDialogState() }
    val genusData = uiState.genusData

    val context = LocalContext.current

    LaunchedEffect(null) {
        val toastFlow = detailGenusViewModel.getToastFlow()
        toastFlow.collectLatest { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        Crossfade(
            targetState = genusData,
            label = "GenusDetailCrossfade"
        ) {
            when (it) {
                is DataState.LoadInProgress -> {
                    Log.d("DetailGenus", "Load in progress. Nothing to show")
                }
                is DataState.Success -> {
                    GenusDetailScreenContent(
                        uiState = uiState,
                        dialogState = dialogState,
                        modifier = Modifier
                            .padding(padding)
                            .padding(horizontal = 8.dp),
                        onEvent = { event: DetailGenusUiEvent ->
                            detailGenusViewModel.onEvent(event)
                        }
                    )
                }
                is DataState.Failed -> {
                    Log.e("DetailGenusScreen", "An error occurred when fetching the genus data.")
                    MissingDataPlaceholder()
                }
                is DataState.Idle -> {
                    MissingDataPlaceholder()
                }
            }
        }
    }
}