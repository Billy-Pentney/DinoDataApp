package com.bp.dinodata.presentation.list_genus

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TotalCreaturesCard(
    numCreatures: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 25.dp),
            verticalAlignment = Alignment.Top
        ) {
            Text(
                "Showing:",
                color = Color.White
            )
            Spacer(Modifier.weight(1f))
            Text(
                "$numCreatures ",
                fontSize = 42.sp,
                fontStyle = FontStyle.Italic,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.End,
                modifier = Modifier
                    .offset(y = 0.dp)
                    .padding(end = 4.dp)
            )
            Text(
                "creatures",
                color = Color.White,
                modifier = Modifier
                    .alpha(0.5f)
                    .fillMaxHeight(),
                fontSize = 22.sp
            )
        }
    }
}