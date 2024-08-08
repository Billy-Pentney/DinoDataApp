package com.bp.dinodata.data.time_period

import androidx.compose.ui.graphics.Color

interface ITimeStage: IDisplayableTimePeriod


class TimeStage(
    name: String,
    nameResId: Int,
    private val interval: ITimeInterval,
    colorLight: Color,
    colorDark: Color = colorLight
): DisplayableTimePeriod(
    name, nameResId, colorLight, colorDark
), ITimeStage {
    override fun getTimeInterval(): ITimeInterval = interval
}
