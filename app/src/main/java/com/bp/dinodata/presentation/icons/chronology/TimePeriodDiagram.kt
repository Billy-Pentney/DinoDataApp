package com.bp.dinodata.presentation.icons.chronology

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.bp.dinodata.data.time_period.Eras
import com.bp.dinodata.data.time_period.IDisplayableTimePeriod
import com.bp.dinodata.data.time_period.IPartitionedTimePeriod
import com.bp.dinodata.data.time_period.ITimeInterval
import com.bp.dinodata.data.time_period.TimeInterval
import com.bp.dinodata.data.time_period.epochs.MesozoicEpochs
import com.bp.dinodata.theme.DinoDataTheme


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

/**
 * Displays a horizontal bar, consisting of the given labelled
 * time-periods, drawn to scale. Markers can optionally be provided
 * to highlight specific points on the bar
 */
@Composable
fun HorizontalChronologyBar(
    period: IDisplayableTimePeriod,
    markers: List<ITimeInterval>,
    modifier: Modifier = Modifier,
    barHeight: Dp = 14.dp,
    fontSize: TextUnit = 12.sp,
    markerWidth: Dp = 3.dp,
    barCornerRadius: Dp = 4.dp
) {
    val eraName = period.getName()
    val barPeriods =
        if (period is IPartitionedTimePeriod) {
            period.getSubdivisions()
        }
        else {
            listOf(period)
        }

    // The time-range this bar covers
    val earliest = barPeriods.maxOf { it.getStartTimeInMYA() }
    val latest = barPeriods.minOf { it.getEndTimeInMYA() }
    val totalBarDuration = earliest - latest

    // These are the points which will have their year displayed beneath the bar
    // Don't add the "latest" finish time, since it will be added by the last period.
    val yearLabelPoints = mutableListOf(
        earliest
    )

    var barSize by remember { mutableStateOf(IntSize.Zero) }
    var barSizeDp by remember { mutableStateOf(Pair(0.dp,0.dp)) }

    with(LocalDensity.current) {
        val xDp = barSize.width.toDp()
        val yDp = barSize.height.toDp()
        barSizeDp = Pair(xDp, yDp)
    }

    /**
     * Given a value in 0-1, return the corresponding width of the bar.
     */
    val computeWidth = { barFrac: Float ->
        barSizeDp.first * barFrac
    }

    Surface (
        shape = RoundedCornerShape(8.dp),
        modifier = modifier,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                eraName,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.alpha(0.7f)
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Time Period labels
            Box(Modifier.fillMaxWidth()) {
                barPeriods.forEach { subPeriod ->
                    val elapsedTime = earliest - subPeriod.getStartTimeInMYA()
                    val widthProportion = subPeriod.getDurationInMYA() / totalBarDuration
                    val width = barSizeDp.first * widthProportion

                    val offsetProportion = elapsedTime / totalBarDuration
                    val xOffset = barSizeDp.first * minOf(offsetProportion, 0.85f)

                    val angle =
                        if (widthProportion < 0.15f)
                            1f
                        else
                            minOf(1f, maxOf(0f,widthProportion-0.15f) / 0.8f)

                    val rotation = angle * -90f

                    Column(
                        modifier = Modifier
                            .offset(x = xOffset)
                            .rotate(rotation)
                            .width(width),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Text(
                            subPeriod.getName().uppercase(),
                            modifier = Modifier.widthIn(max=width),
                            overflow = TextOverflow.Ellipsis,
                            fontSize = fontSize,
                            lineHeight = 14.sp,
                            textAlign = TextAlign.Center,
//                        maxLines = 1,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                    yearLabelPoints.add(subPeriod.getEndTimeInMYA())
                }
            }
            Spacer(Modifier.height(16.dp))
            Box(Modifier.fillMaxWidth()) {
                // Time Period Bars
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = barHeight / 2)
                        .onSizeChanged { barSize = it }
                ) {
                    barPeriods.forEachIndexed { i, subPeriod ->

                        val leftCorners =
                            if (i == 0) barCornerRadius else 0.dp

                        val rightCorners =
                            if (i == barPeriods.size - 1) barCornerRadius
                            else 0.dp

                        val shape = RoundedCornerShape(
                            topStart = leftCorners,
                            topEnd = rightCorners,
                            bottomStart = leftCorners,
                            bottomEnd = rightCorners
                        )

                        val elapsed = earliest - subPeriod.getStartTimeInMYA()
                        val elapsedWidth = barSizeDp.first * elapsed / totalBarDuration
                        val duration = subPeriod.getDurationInMYA()
                        val proportion = barSizeDp.first * duration / totalBarDuration
                        Box(
                            Modifier
                                .width(proportion)
                                .offset(x = elapsedWidth)
                                .height(barHeight)
                                .background(subPeriod.getBrushDarkToBright(), shape)
                        )
                    }
                }

                // Display only the markers which appear in this era
                markers
                    .filter { period.contains(it) }
                    .forEach { timeMarker ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .zIndex(1f),
                        ) {
                            val startMarker = timeMarker.getStartTimeInMYA()
                            val weightToStart = (earliest - startMarker) / totalBarDuration

                            var weightToEnd = weightToStart

                            Spacer(modifier = Modifier.weight(weightToStart))

                            TimePeriodMarker(markerWidth, barHeight * 2)
                            val endMarker = timeMarker.getEndTimeInMYA()
                            if (endMarker != startMarker) {
                                // Draw a shaded region between the two markers
                                weightToEnd = (earliest - endMarker) / totalBarDuration
                                // The interval as a fraction of the total bar
                                val intervalWeight = weightToEnd - weightToStart
                                Box(
                                    modifier = Modifier
                                        .weight(intervalWeight)
                                        .height(barHeight / 2)
                                        .offset(y = barHeight * 0.75f)
                                        .background(Color(0x88FFFFFF))
                                )
                                TimePeriodMarker(markerWidth, barHeight * 2)
                            }

                            if (weightToEnd < 1f) {
                                Spacer(Modifier.weight(1f - weightToEnd))
                            }
                        }
                    }
            }

            // Display labels indicating year-number of each transition point
            Box(modifier = Modifier.fillMaxWidth()) {
                var lastTime: Float? = null
                yearLabelPoints.forEachIndexed { i, time ->
                    val alignment = when (i) {
                        0 -> Alignment.Start
                        yearLabelPoints.size - 1 -> Alignment.End
                        else -> Alignment.CenterHorizontally
                    }

                    val elapsedTime = earliest - time
                    val elapsedProportion = elapsedTime / totalBarDuration

                    val labelWidth = 42.dp

                    var xOffset = barSizeDp.first * elapsedProportion

                    // Reduce the offset, to centre the vertical bar on the specific point
                    if (i > 0) {
                        xOffset -= labelWidth / 2
                    }
                    if (i == yearLabelPoints.size-1) {
                        xOffset -= labelWidth / 2
                    }

                    val deltaWeight = lastTime?.minus(time)?.div(totalBarDuration) ?: 1f

                    Column(
                        horizontalAlignment = alignment,
                        modifier = Modifier
                            .width(labelWidth)
                            .offset(x = xOffset)
                            .alpha(0.5f)
                    ) {
                        // The height of the marker line pointing at the time-bar
                        // This is doubled if this marker is close to the previous one
                        val vBarHeight =
                            if (deltaWeight < 0.1f) 32.dp else 12.dp

                        Box(
                            modifier = Modifier
                                .background(Color.White)
                                .width(1.dp)
                                .height(vBarHeight)
                        )
                        Text(
                            "%.1f".format(time),
                            fontSize = 12.sp
                        )
                    }
                    lastTime = time
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(0.6f),
                horizontalArrangement = Arrangement.Center
            ) {
                Text("million years ago (mya)", fontSize = 12.sp)
            }
        }
    }
}



@Composable
fun TimeChronologyBar(
    marker: ITimeInterval,
    modifier: Modifier = Modifier
) {
    // Get the first era which includes this marker
    val era = Eras.getList().firstOrNull {
        it.overlapsWith(marker)
    }

    era?.let {
        HorizontalChronologyBar(
            period = era,
            markers = listOf(marker),
            modifier = modifier
        )
    }
}



@Preview
@Composable
fun TimePeriodBarPreview() {

    val periods: List<IDisplayableTimePeriod> = listOf(
        Eras.Mesozoic,
        Eras.Paleozoic,
        MesozoicEpochs.Cretaceous
    )

    DinoDataTheme (darkTheme=true) {
        Column (
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            periods.forEach {
                HorizontalChronologyBar(
                    period = it,
                    markers = listOf(
                        TimeInterval(175f, 101f)
                    )
                )
            }
//            GenericChronologyBar(
//                period = ModifiedEpoch(MesozoicEpoch.Jurassic, Subepoch.Late),
//                markers = emptyList()
//            )
        }
    }
}