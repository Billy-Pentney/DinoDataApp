package com.bp.dinodata.presentation.icons

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.bp.dinodata.R
import com.bp.dinodata.presentation.icons.chronology.ITimeMarkerRange
import com.bp.dinodata.theme.Cretaceous300
import com.bp.dinodata.theme.Cretaceous600
import com.bp.dinodata.theme.DinoDataTheme
import com.bp.dinodata.theme.Jurassic300
import com.bp.dinodata.theme.Jurassic700
import com.bp.dinodata.theme.Triassic200
import com.bp.dinodata.theme.Triassic800

data class TimeMarker(
    private val mya: Float
): ITimeMarkerRange {
    override fun getStartTimeInMYA(): Float = mya
    override fun getEndTimeInMYA(): Float = mya
    override fun overlapsWith(other: ITimeMarkerRange): Boolean {
        return other.getStartTimeInMYA() <= mya
                && mya <= other.getEndTimeInMYA()
    }
}

data class TimeMarkerRange(
    private var from: Float,
    private var to: Float
): ITimeMarkerRange {
    init {
        // Swap to enforce that start <= end
        if (from < to) {
            val temp = from
            from = to
            to = temp
        }
    }

    override fun getStartTimeInMYA(): Float = from
    override fun getEndTimeInMYA(): Float = to
    override fun overlapsWith(other: ITimeMarkerRange): Boolean {
        return other.getStartTimeInMYA() <= from
                && to <= other.getEndTimeInMYA()
    }
}

interface ITimePeriodBar: ITimeMarkerRange {
    fun getBrush(): Brush
    @Composable fun getName(): String
}

data class TimePeriodBar(
    private val timePeriodRange: ITimeMarkerRange,
    private val nameResId: Int,
    private val colorLight: Color,
    private val colorDark: Color = colorLight
): ITimePeriodBar {
    private val brush: Brush = Brush.linearGradient(
        0.0f to colorDark,
        1.0f to colorLight
    )

    override fun getStartTimeInMYA(): Float = timePeriodRange.getStartTimeInMYA()
    override fun getEndTimeInMYA(): Float = timePeriodRange.getEndTimeInMYA()
    override fun overlapsWith(other: ITimeMarkerRange): Boolean =
        timePeriodRange.overlapsWith(other)

    override fun getBrush(): Brush = brush
    @Composable override fun getName(): String = stringResource(id = nameResId)
}

val Triassic = TimePeriodBar(
    timePeriodRange = TimeMarkerRange(251.9f, 201.4f),
    nameResId = R.string.time_period_triassic,
    colorLight = Triassic200,
    colorDark = Triassic800
)

val Jurassic = TimePeriodBar(
    timePeriodRange = TimeMarkerRange(201.4f,145f),
    nameResId = R.string.time_period_jurassic,
    colorLight = Jurassic300,
    colorDark = Jurassic700
)

val Cretaceous = TimePeriodBar(
    timePeriodRange = TimeMarkerRange(145f, 65.5f),
    nameResId = R.string.time_period_cretaceous,
    colorLight = Cretaceous300,
    colorDark = Cretaceous600
)

@Composable
fun TimePeriodMarker(
    width: Dp,
    height: Dp
) {
    Box(
        modifier = Modifier
            .width(width)
            .height(height)
            .background(Color(0xAAFFFFFF))
    )
}

@Composable
fun GenericChronologyBar(
    barPeriods: List<ITimePeriodBar>,
    markers: List<ITimeMarkerRange>,
    modifier: Modifier = Modifier,
    barHeight: Dp = 14.dp,
    fontSize: TextUnit = 12.sp,
    markerWidth: Dp = 4.dp,
) {

    // The time-range this bar covers
    val earliest = barPeriods.maxOf { it.getStartTimeInMYA() }
    val latest = barPeriods.minOf { it.getEndTimeInMYA() }
    val totalBarDuration = earliest - latest

    Column (
        modifier = modifier.fillMaxWidth()
    ) {
        // Time Period labels
        Row(Modifier.fillMaxWidth()) {
            barPeriods.forEach { period ->
                val duration = period.getDurationInMYA()
                val proportion = duration / totalBarDuration
                Column(
                    Modifier.weight(proportion),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        period.getName().uppercase(),
                        fontSize = fontSize,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
        Box (Modifier.fillMaxWidth()) {
            // Time Period Bars
            Row(Modifier.fillMaxWidth().padding(top=barHeight/2)) {
                barPeriods.forEach { period ->
                    val duration = period.getDurationInMYA()
                    val proportion = duration / totalBarDuration
                    Box(
                        Modifier
                            .weight(proportion)
                            .height(barHeight)
                            .fillMaxWidth()
                            .background(period.getBrush())
                    )
                }
            }

            // Marker over periods
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .zIndex(1f),
            ) {
                markers.forEach() { timeMarker ->
                    val startMarker = timeMarker.getStartTimeInMYA()
                    val startProportion = (earliest - timeMarker.getStartTimeInMYA()) / totalBarDuration
                    Spacer(modifier= Modifier
                        .fillMaxWidth(startProportion)
                        .padding(end = markerWidth)
                    )
                    Column {
                        TimePeriodMarker(markerWidth, barHeight*2)
    //                    Text("$startMarker MYA")
                    }
                    val endMarker = timeMarker.getEndTimeInMYA()
                    if (endMarker != startMarker) {
                        val endMyaProportion = (earliest - endMarker) / totalBarDuration
                        // Shaded region between markers
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(endMyaProportion - startProportion)
                                .height(barHeight / 2)
                                .offset(y = barHeight * 0.75f)
                                .background(Color(0x88FFFFFF))
                        )
                        TimePeriodMarker(markerWidth, barHeight*2)
                    }
                }

                Spacer(Modifier.fillMaxWidth())
            }
        }
    }
}


@Composable
fun MesozoicChronologyBar(
    markers: List<ITimeMarkerRange>,
    modifier: Modifier = Modifier
) {
    GenericChronologyBar(
        barPeriods = listOf(Triassic, Jurassic, Cretaceous),
        markers = markers,
        modifier = modifier
    )
}

@Preview
@Composable
fun TimePeriodBarPreview() {
    DinoDataTheme (darkTheme=true) {
        Surface (
            Modifier.fillMaxHeight()
        ) {
            MesozoicChronologyBar(
                markers = listOf(
                    TimeMarkerRange(90f, 144f),
                    TimeMarker(81f)
                ),
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}