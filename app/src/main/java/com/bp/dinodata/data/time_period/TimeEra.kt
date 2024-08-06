package com.bp.dinodata.data.time_period

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.bp.dinodata.presentation.icons.chronology.ITimeInterval
import com.bp.dinodata.presentation.icons.chronology.IDisplayableTimePeriod

interface ITimeEra: ITimeInterval {
    @Composable
    fun getName(): String
    fun getTimePeriods(): List<IAtomicDisplayableTimePeriod>
}

class TimeEra(
    private val nameResId: Int,
    periods: List<IAtomicDisplayableTimePeriod>
): ITimeEra {
    // Order from oldest (earliest) to youngest (latest)
    private val _periods = periods.sortedByDescending { it.getStartTimeInMYA() }

    @Composable
    override fun getName(): String = stringResource(nameResId)
    override fun getTimePeriods(): List<IAtomicDisplayableTimePeriod> = _periods
    override fun getStartTimeInMYA(): Float {
        return _periods.firstOrNull()?.getStartTimeInMYA() ?: 0f
    }
    override fun getEndTimeInMYA(): Float {
        return _periods.lastOrNull()?.getEndTimeInMYA() ?: 0f
    }
}