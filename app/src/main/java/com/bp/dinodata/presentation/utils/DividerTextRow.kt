package com.bp.dinodata.presentation.utils

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun DividerTextRow(
    text: String,
    modifier: Modifier = Modifier,
    dividerPadding: PaddingValues = PaddingValues(2.dp)
) {
    val color = LocalContentColor.current
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        HorizontalDivider(
            modifier = Modifier.weight(1f).padding(dividerPadding),
            color = color
        )
        Text(
            text,
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .alpha(0.6f),
            textAlign = TextAlign.Center,
            color = color
        )
        HorizontalDivider(
            modifier = Modifier
                .weight(1f)
                .padding(dividerPadding),
            color = color
        )
    }
}