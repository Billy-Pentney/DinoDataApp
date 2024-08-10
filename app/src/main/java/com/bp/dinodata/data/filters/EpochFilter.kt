package com.bp.dinodata.data.filters

import com.bp.dinodata.data.genus.IHasTimePeriodInfo
import com.bp.dinodata.data.time_period.era.EraId
import com.bp.dinodata.data.time_period.epochs.IEpoch
import com.bp.dinodata.data.time_period.epochs.IEpochId
import com.bp.dinodata.data.time_period.era.IProvidesEra

class EraFilter(
    private val acceptedEra: List<EraId>
): IFilter<IHasTimePeriodInfo> {
    override fun acceptsItem(item: IHasTimePeriodInfo): Boolean {
        val itemPeriod = item.getTimePeriod()

        if (itemPeriod is IProvidesEra) {
            val eraId = itemPeriod.getEraId()
            return eraId in acceptedEra
        }

        return false
    }
}

class EpochFilter(
    private val acceptedEpochs: List<IEpochId>
): IFilter<IHasTimePeriodInfo> {
    override fun acceptsItem(item: IHasTimePeriodInfo): Boolean {
        val itemPeriod = item.getTimePeriod()

        if (itemPeriod is IEpoch) {
            val id = itemPeriod.getEpochId()
            return id in acceptedEpochs
        }

        return false
    }
}
