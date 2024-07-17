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
import com.bp.dinodata.data.Epoch
import com.bp.dinodata.data.Subepoch
import com.bp.dinodata.data.TimePeriod
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
fun TimePeriodIcon(timePeriod: TimePeriod?) {
    val epoch = timePeriod?.epoch
    val placeholderText = stringResource(id = R.string.placeholder_unknown_period)
    val text = epoch?.toString() ?: placeholderText

    val colors = when (epoch) {
        Epoch.Cretaceous -> listOf(Cretaceous400, Cretaceous700)
        Epoch.Jurassic -> listOf(Jurassic400, Jurassic700)
        Epoch.Triassic -> listOf(Triassic400, Triassic700)
        else -> {
            Log.i("TimePeriodIcon", "Unrecognised time-period: $timePeriod")
            listOf(Color.Gray, Color.DarkGray)
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
    val periods  = listOf(
        TimePeriod(Epoch.Cretaceous),
        TimePeriod(Epoch.Jurassic, Subepoch.Late),
        TimePeriod(Epoch.Triassic, Subepoch.Middle),
        null
    )

    Column {
        periods.forEach { TimePeriodIcon(timePeriod = it) }
    }
}