package com.bp.dinodata.data.filters

import com.bp.dinodata.data.genus.IHasTimePeriodInfo
import com.bp.dinodata.data.time_period.IDisplayableTimePeriod
import com.bp.dinodata.data.time_period.modifiers.IModifiedTimePeriod

class TimePeriodFilter(
    private val acceptedTimePeriods: List<IDisplayableTimePeriod>
): IFilter<IHasTimePeriodInfo> {
    override fun acceptsItem(item: IHasTimePeriodInfo): Boolean {
        val itemPeriod = item.getTimePeriod()
//        val itemInterval = item.getTimeIntervalLived()

        if (itemPeriod != null) {
            // If this epoch is modified (e.g. "Early Cretaceous"), then
            // we can just compare against the unmodified version
            val basePeriod = if (itemPeriod is IModifiedTimePeriod<*>) {
                itemPeriod.getBase()
            }
            else {
                itemPeriod
            }

            return itemPeriod in acceptedTimePeriods || basePeriod in acceptedTimePeriods
        }

        return false
    }
}
