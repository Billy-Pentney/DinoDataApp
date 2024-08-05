package com.bp.dinodata.presentation.icons.chronology

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

interface ITimeEra: ITimeInterval {
    @Composable
    fun getName(): String
    fun getTimePeriods(): List<TimePeriodBar>
}

class TimeEra(
    private val nameResId: Int,
    periods: List<TimePeriodBar>
): ITimeEra {
    // Order from oldest (earliest) to youngest (latest)
    private val _periods = periods.sortedByDescending { it.getStartTimeInMYA() }

    @Composable
    override fun getName(): String = stringResource(nameResId)
    override fun getTimePeriods(): List<TimePeriodBar> = _periods
    override fun getStartTimeInMYA(): Float {
        return _periods.firstOrNull()?.getStartTimeInMYA() ?: 0f
    }
    override fun getEndTimeInMYA(): Float {
        return _periods.lastOrNull()?.getEndTimeInMYA() ?: 0f
    }
}