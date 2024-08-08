package com.bp.dinodata.presentation.icons

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bp.dinodata.R
import com.bp.dinodata.data.time_period.Eras
import com.bp.dinodata.data.time_period.IDisplayableTimePeriod
import com.bp.dinodata.data.time_period.Subepoch
import com.bp.dinodata.theme.DinoDataTheme

@Composable
fun TimePeriodIcon(timePeriod: IDisplayableTimePeriod?) {
    val placeholderText = stringResource(id = R.string.placeholder_unknown_period)
    val text = timePeriod?.getName() ?: placeholderText

    var brush = timePeriod?.getBrushBrightToDark()

    if (brush == null) {
        Log.i("TimePeriodIcon", "Unrecognised time-period: $timePeriod")
        brush = Brush.linearGradient(listOf(Color.Gray, Color.DarkGray))
    }

    val shape = RoundedCornerShape(4.dp)
    Box (
        modifier = Modifier
            .background(
                brush = brush,
                shape = shape
            )
            .border(width = 1.dp, color = Color.White, shape = shape)
            .padding(vertical = 3.dp, horizontal = 8.dp)
            .heightIn(max = 22.dp)
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
    val eras = Eras.getList()
    val subepochs = Subepoch.entries

    DinoDataTheme(darkTheme = true) {
        Surface {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier=Modifier.padding(16.dp)
            ) {
                eras.forEach { era ->
                    val epochs = era.getSubdivisions()
                    val timePeriods = epochs.map{ it.with(subepochs.random()) }

                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(era.getName())
                        timePeriods.forEach {
                            TimePeriodIcon(timePeriod = it)
                        }
                    }
                }
            }
        }
    }
}