package com.bp.dinodata.presentation.detail_genus.card_fragments

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTimeFilled
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.bp.dinodata.R
import com.bp.dinodata.data.genus.IHasTimePeriodInfo
import com.bp.dinodata.presentation.detail_genus.LabelAttributeRow
import com.bp.dinodata.presentation.detail_genus.LabelContentRow
import com.bp.dinodata.presentation.icons.TimePeriodIcon
import com.bp.dinodata.presentation.icons.chronology.TimeChronologyBar

@Composable
fun CreatureTimePeriod(
    item: IHasTimePeriodInfo,
    iconModifier: Modifier
) {
    val timePeriods = item.getTimePeriods()
    val yearsLived = item.getYearsLived()
    val timeInterval = item.getTimeIntervalLived()

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
//        LabelContentRow(
//            label = stringResource(R.string.label_time_period),
//            valueContent = {
//                val firstPeriod = timePeriods.firstOrNull()
//                val lastPeriod = timePeriods.lastOrNull()
//
//                if (firstPeriod == lastPeriod) {
//                    // Show the singleton
//                    TimePeriodIcon(firstPeriod)
//                } else {
//                    // Show the range from the first to the last
//                    Row(verticalAlignment = Alignment.CenterVertically) {
//                        TimePeriodIcon(firstPeriod)
//                        Text(" / ")
//                        TimePeriodIcon(lastPeriod)
//                    }
//                }
//            },
//            leadingIcon = {
//                Icon(Icons.Filled.CalendarMonth, null, modifier = iconModifier)
//            }
//        )
        LabelAttributeRow(
            label = stringResource(R.string.label_years_lived),
            value = yearsLived,
            leadingIcon = {
                Icon(Icons.Filled.AccessTimeFilled, null, modifier = iconModifier)
            }
        )

        if (timePeriods.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            TimeChronologyBar(timePeriods, timeInterval)
        }
    }
}