package com.bp.dinodata.presentation.utils

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.bp.dinodata.R

@Composable
fun NoDataPlaceholder() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .alpha(0.5f),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
    ) {
        Icon(Icons.Filled.QuestionMark, contentDescription = "question mark")
        Text("No data to display")
    }
}

@Composable
fun MissingDataPlaceholder(
    retryAction: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .alpha(0.5f),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
    ) {
        Icon(Icons.Filled.Close, contentDescription = null)
        Text("No data to display")
        retryAction?.let {
            Button(
                onClick = retryAction
            ) {
                Text("Retry?")
            }
        }
    }
}

@Composable
fun LoadingItemsPlaceholder() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .alpha(0.5f),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically)
    ) {
        CircularProgressIndicator(color = White)
        Text(stringResource(R.string.info_loading_placeholder))
    }
}