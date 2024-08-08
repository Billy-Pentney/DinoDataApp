package com.bp.dinodata.data.time_period

import androidx.compose.ui.graphics.Color

/**
 * Represents a geological time-period known as an 'Era'.
 * An Era is a sub-unit of an Eon, and an Epoch is a sub-unit of an Era.
 * */
interface ITimeEra: IDisplayableTimePeriod, IPartitionedTimePeriod, IProvidesEra, ITimePeriodProvidesParent {
    override fun getSubdivisions(): List<IModifiableEpoch>
}


abstract class TimeEra(
    private val key: EraId,
    nameResId: Int,
    colorLight: Color,
    colorDark: Color = colorLight,
    epochs: List<IModifiableEpoch>
): DisplayableTimePeriod(key.toString(), nameResId, colorLight, colorDark),
    ITimeEra {
    private val orderedSubPeriods = epochs.sortedByDescending { it.getStartTimeInMYA() }
    private val timeInterval = TimeInterval.fromList(epochs)

    override fun getTimeInterval(): ITimeInterval = timeInterval

    // Order from oldest (earliest) to youngest (latest)
    override fun getSubdivisions(): List<IModifiableEpoch> = orderedSubPeriods

    override fun getEraId(): EraId = key

    override fun getParentPeriod(): IDisplayableTimePeriod {
        return EraRetriever.getEra(key)
    }
}