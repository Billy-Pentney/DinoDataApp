package com.bp.dinodata.presentation.icons.chronology

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bp.dinodata.data.time_period.era.Eras
import com.bp.dinodata.data.time_period.IDisplayableTimePeriod
import com.bp.dinodata.data.time_period.intervals.ITimeInterval
import com.bp.dinodata.data.time_period.ITimePeriodProvidesParent
import com.bp.dinodata.data.time_period.intervals.TimeInterval
import com.bp.dinodata.data.time_period.epochs.MesozoicEpochs
import com.bp.dinodata.data.time_period.modifiers.IModifiedTimePeriod
import com.bp.dinodata.data.time_period.modifiers.ModifiedEpoch
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

@Composable
fun TimeChronologyBar(
    period: IDisplayableTimePeriod,
    timeInterval: ITimeInterval?,
    modifier: Modifier = Modifier,
    orientation: ChronologyOrientation = ChronologyOrientation.Horizontal,
) {
//    if (period == null) {
//        NoDataPlaceholder()
//        return
//    }

     val parent: IDisplayableTimePeriod
     val highlightedInterval: ITimeInterval?

     when (period) {
        is ITimePeriodProvidesParent -> {
            // Attempt to get the period directly one-level above this one.
            // e.g. if period is an epoch, then we get its era.
            parent = period.getParentPeriod()
            highlightedInterval = period
        }

        else -> {
            // Otherwise, fall back to the period itself
            parent = period
            highlightedInterval = timeInterval
        }
    }

    val markers = highlightedInterval?.let {
        listOf(it)
    } ?: emptyList()

    when (orientation) {
        ChronologyOrientation.Horizontal -> {
            HorizontalChronologyBar(
                period = parent,
                markers = markers,
                modifier = modifier
            )
        }
        ChronologyOrientation.Vertical -> {
            VerticalChronologyBar(
                period = parent,
                markers = markers,
                modifier = modifier
            )
        }
    }
}

@Composable
fun TimeChronologyBar(
    periods: List<IDisplayableTimePeriod>,
    timeInterval: ITimeInterval?,
    modifier: Modifier = Modifier,
    orientation: ChronologyOrientation = ChronologyOrientation.Horizontal,
) {
    val parents: MutableList<IDisplayableTimePeriod> = mutableListOf()
    val allPeriodInterval: ITimeInterval = TimeInterval.fromList(periods)

    for (period in periods) {
        when (period) {
            is IModifiedTimePeriod<*> -> {
                // Attempt to get the period directly one-level above this one.
                // e.g. if period is an epoch, then we get its era.
                parents.add(period.getBase())
            }
            else -> {
                // Otherwise, fall back to the period itself
                parents.add(period)
            }
        }
    }

    val markers = timeInterval?.let { listOf(it) }
        ?: listOf(allPeriodInterval)

    when (orientation) {
        ChronologyOrientation.Horizontal -> {
            HorizontalChronologyBar(
                basePeriods = parents,
                markers = markers,
                modifier = modifier
            )
        }
        ChronologyOrientation.Vertical -> {
            VerticalChronologyBar(
                period = periods.first(),
                markers = markers,
                modifier = modifier
            )
        }
    }
}



@Preview (widthDp=800, heightDp=1000)
@Composable
fun TimePeriodBarPreview() {

    val LateJurassic = ModifiedEpoch(
        "Late",
        TimeInterval(170f, 145f),
        MesozoicEpochs.Jurassic
    )
    val EarlyCretaceous = ModifiedEpoch("Early",
        TimeInterval(145f, 110f),
        MesozoicEpochs.Cretaceous
    )

    val periods: List<IDisplayableTimePeriod> = listOf(
        Eras.Mesozoic,
        Eras.Paleozoic,
        MesozoicEpochs.Cretaceous,
        EarlyCretaceous
    )

    DinoDataTheme (darkTheme=true) {
        LazyVerticalGrid (
            columns = GridCells.Fixed(2),
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(periods) {
                TimeChronologyBar(
                    period = it,
                    timeInterval = TimeInterval(175f, 101f),
                    orientation = ChronologyOrientation.Horizontal
                )
            }
            item {
                TimeChronologyBar(
                    periods = listOf(
                        LateJurassic,
                        EarlyCretaceous
                    ),
                    timeInterval = TimeInterval(155f,135f)
                )
            }
        }
    }
}