package com.bp.dinodata.data.time_period.era

import androidx.compose.ui.graphics.Color
import com.bp.dinodata.data.time_period.DisplayableTimePeriod
import com.bp.dinodata.data.time_period.EraRetriever
import com.bp.dinodata.data.time_period.IDisplayableTimePeriod
import com.bp.dinodata.data.time_period.epochs.IModifiableEpoch
import com.bp.dinodata.data.time_period.IPartitionedTimePeriod
import com.bp.dinodata.data.time_period.ITimePeriodProvidesParent
import com.bp.dinodata.data.time_period.intervals.ITimeInterval
import com.bp.dinodata.data.time_period.intervals.TimeInterval

/**
 * Represents a geological time-period known as an 'Era'.
 * An Era is a sub-unit of an Eon, and an Epoch is a sub-unit of an Era.
 * */
interface ITimeEra: IDisplayableTimePeriod, IPartitionedTimePeriod, IProvidesEra,
    ITimePeriodProvidesParent {
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