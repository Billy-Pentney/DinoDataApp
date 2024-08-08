package com.bp.dinodata.presentation.icons.chronology

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.res.stringResource
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
import com.bp.dinodata.R
import com.bp.dinodata.data.time_period.Eras
import com.bp.dinodata.data.time_period.IDisplayableTimePeriod
import com.bp.dinodata.data.time_period.IPartitionedTimePeriod
import com.bp.dinodata.data.time_period.ITimeInterval
import com.bp.dinodata.data.time_period.TimeInterval
import com.bp.dinodata.data.time_period.epochs.MesozoicEpochs
import com.bp.dinodata.theme.DinoDataTheme

/**
 * Displays a horizontal bar, consisting of the given labelled
 * time-periods, drawn to scale. Markers can optionally be provided
 * to highlight specific points on the bar
 */
@Composable
fun VerticalChronologyBar(
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
    val yearLabelPoints = mutableListOf(earliest)

    var barSize by remember { mutableStateOf(IntSize.Zero) }
    var barSizeDp by remember { mutableStateOf(Pair(0.dp, 0.dp)) }

    with(LocalDensity.current) {
        val xDp = barSize.width.toDp()
        val yDp = barSize.height.toDp()
        barSizeDp = Pair(xDp, yDp)
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        modifier = modifier,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .padding(top=16.dp, end=16.dp, start=16.dp, bottom=16.dp)
                .height(350.dp)
                .fillMaxWidth()
        ) {
            Text(
                eraName,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .alpha(0.7f)
                    .align(Alignment.CenterHorizontally)
            )
            Spacer(Modifier.height(8.dp))
            Row (
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                Column (modifier= Modifier
                    .fillMaxHeight()
                    .weight(0.4f)
                ) {
                    // Time Period labels
                    barPeriods.forEach { subPeriod ->
                        val elapsedTime = earliest - subPeriod.getStartTimeInMYA()
                        val heightProportion = subPeriod.getDurationInMYA() / totalBarDuration
                        val height = barSizeDp.second * heightProportion

                        val offsetProportion = elapsedTime / totalBarDuration
                        val xOffset = barSizeDp.second * minOf(offsetProportion, 0.85f)

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier
                                .height(height)
                                .fillMaxWidth()
                        ) {
                            Text(
                                subPeriod.getName().uppercase(),
                                overflow = TextOverflow.Ellipsis,
                                fontSize = fontSize,
                                lineHeight = 14.sp,
                                textAlign = TextAlign.End,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                        yearLabelPoints.add(subPeriod.getEndTimeInMYA())
                    }
                }

                Spacer(Modifier.width(8.dp))

                // Time Period Bars
                Box(Modifier.fillMaxHeight()) {
                    Box(
                        Modifier
                            .fillMaxHeight()
                            .padding(horizontal = barHeight / 2)
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
                                topEnd = leftCorners,
                                bottomStart = rightCorners,
                                bottomEnd = rightCorners
                            )

                            val elapsed = earliest - subPeriod.getStartTimeInMYA()
                            val elapsedWidth = barSizeDp.second * elapsed / totalBarDuration
                            val duration = subPeriod.getDurationInMYA()
                            val proportion = barSizeDp.second * duration / totalBarDuration
                            Box(
                                Modifier
                                    .height(proportion)
                                    .offset(y = elapsedWidth)
                                    .width(barHeight)
                                    .background(subPeriod.getBrushDarkToBright(), shape)
                            )
                        }
                    }
                    // Display only the markers which appear in this era
                    markers
                        .filter { period.contains(it) }
                        .forEach { timeMarker ->
                            Column(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .zIndex(1f),
                            ) {
                                val startMarker = timeMarker.getStartTimeInMYA()
                                val weightToStart = (earliest - startMarker) / totalBarDuration

                                var weightToEnd = weightToStart

                                Spacer(modifier = Modifier.weight(weightToStart))

                                TimePeriodMarker(barHeight * 2, markerWidth)
                                val endMarker = timeMarker.getEndTimeInMYA()
                                if (endMarker != startMarker) {
                                    // Draw a shaded region between the two markers
                                    weightToEnd = (earliest - endMarker) / totalBarDuration
                                    // The interval as a fraction of the total bar
                                    val intervalWeight = weightToEnd - weightToStart
                                    Box(
                                        modifier = Modifier
                                            .weight(intervalWeight)
                                            .width(barHeight / 2)
                                            .offset(x = barHeight * 0.75f)
                                            .background(Color(0x88FFFFFF))
                                    )
                                    TimePeriodMarker(barHeight * 2, markerWidth)
                                }

                                if (weightToEnd < 1f) {
                                    Spacer(Modifier.weight(1f - weightToEnd))
                                }
                            }
                        }
                }

                // Display labels indicating year-number of each transition point
                Column(modifier = Modifier.fillMaxHeight()) {
                    yearLabelPoints.forEachIndexed { i, time ->
                        val elapsedTime = earliest - time
                        val elapsedProportion = elapsedTime / totalBarDuration

                        val labelHeight = 22.dp

                        val xOffset = barSizeDp.second * elapsedProportion - labelHeight.times(i) - labelHeight/2

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier
                                .offset(y = xOffset)
                                .height(labelHeight)
                                .alpha(0.5f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(Color.White)
                                    .height(1.dp)
                                    .width(12.dp)
                            )
                            Text(
                                "%.1f".format(time),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
                Spacer(modifier=Modifier.width(8.dp))
                Row(
                    modifier = Modifier
                        .weight(0.4f)
                        .fillMaxHeight()
                        .alpha(0.6f),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.label_mya),
                        fontSize = 12.sp,
                        lineHeight = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}





@Preview
@Composable
fun VerticalTimePeriodBarPreview() {

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
                VerticalChronologyBar(
                    period = it,
                    markers = listOf(
                        TimeInterval(175f, 101f)
                    )
                )
            }
        }
    }
}