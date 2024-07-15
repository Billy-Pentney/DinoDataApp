package com.bp.dinodata.presentation.icons

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bp.dinodata.R
import com.bp.dinodata.theme.Cretaceous400
import com.bp.dinodata.theme.Cretaceous700
import com.bp.dinodata.theme.Cretaceous800
import com.bp.dinodata.theme.Jurassic400
import com.bp.dinodata.theme.Jurassic700
import com.bp.dinodata.theme.Jurassic800
import com.bp.dinodata.theme.Triassic400
import com.bp.dinodata.theme.Triassic700
import com.bp.dinodata.theme.Triassic800

@Composable
fun TimePeriodIcon(timePeriod: String?) {
    var text = stringResource(id = R.string.placeholder_unknown_period)
    var colors = listOf(Color.Gray, Color.DarkGray)

    val timePeriodLower = timePeriod?.lowercase() ?: ""
    Log.d("TimePeriodIcon", "Got time period $timePeriodLower")

    when {
        "cretaceous" in timePeriodLower -> {
            text = "Cretaceous"
            colors = listOf(Cretaceous400, Cretaceous700)
        }
        "jurassic" in timePeriodLower ->  {
            text = "Jurassic"
            colors = listOf(Jurassic400, Jurassic700)
        }
        "triassic" in timePeriodLower -> {
            text = "Triassic"
            colors = listOf(Triassic400, Triassic700)
        }
    }

    val shape = RoundedCornerShape(4.dp)
    Box (
        modifier = Modifier
            .background(
                brush = Brush.linearGradient(colors, start = Offset.Zero),
                shape = shape
            )
            .border(width = 1.dp, color = Color.White, shape = shape)
            .padding(vertical = 3.dp, horizontal = 8.dp)
            .heightIn(max=22.dp)
            .fillMaxHeight(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview
@Composable
fun PreviewTimePeriodIcon() {
    val periods  = listOf("Cretaceous", "Jurassic", "Triassic", "Campanian")

    Column {
        periods.forEach { TimePeriodIcon(timePeriod = it) }
    }
}