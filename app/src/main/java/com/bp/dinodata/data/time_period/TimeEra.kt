package com.bp.dinodata.data.time_period

import androidx.compose.ui.graphics.Color

interface ITimeEra: IDisplayableTimePeriod, IPartitionedTimePeriod {
    override fun getSubdivisions(): List<IEpoch>
    fun getEnumId(): EraId
}


class TimeEra(
    private val key: EraId,
    nameResId: Int,
    colorLight: Color,
    colorDark: Color = colorLight,
    epochs: List<IEpoch>
): DisplayableTimePeriod(key.toString(), nameResId, colorLight, colorDark), ITimeEra {
    private val orderedSubPeriods = epochs.sortedByDescending { it.getStartTimeInMYA() }
    private val timeInterval = TimeInterval.fromList(epochs)

    override fun getTimeInterval(): ITimeInterval = timeInterval

    // Order from oldest (earliest) to youngest (latest)
    override fun getSubdivisions(): List<IEpoch> = orderedSubPeriods
    override fun getEnumId(): EraId = key
}