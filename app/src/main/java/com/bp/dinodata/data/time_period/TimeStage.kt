package com.bp.dinodata.data.time_period

import androidx.compose.ui.graphics.Color
import com.bp.dinodata.data.time_period.epochs.EpochRetriever
import com.bp.dinodata.data.time_period.epochs.IEpochId
import com.bp.dinodata.data.time_period.epochs.IProvidesEpoch
import com.bp.dinodata.data.time_period.era.EraId
import com.bp.dinodata.data.time_period.intervals.ITimeInterval


interface ITimeStage: IAtomicDisplayableTimePeriod, IProvidesEpoch, ITimePeriodProvidesParent


/**
 * An implementation of the geological time-period known as a 'Stage'.
 * This is the sub-unit of an Epoch and for this implementation is assumed
 * to be atomic (i.e. cannot be divided further).
 * */
class TimeStage(
    private val eraId: EraId,
    private val epochId: IEpochId,
    name: String,
    nameResId: Int,
    private val interval: ITimeInterval,
    colorLight: Color,
    colorDark: Color = colorLight
): DisplayableTimePeriod(
    name, nameResId, colorLight, colorDark
), ITimeStage {
    override fun getTimeInterval(): ITimeInterval = interval

    override fun getEraId(): EraId = eraId
    override fun getEpochId(): IEpochId = epochId

    override fun getParentPeriod(): IDisplayableTimePeriod = EpochRetriever.getEpoch(epochId)
}
