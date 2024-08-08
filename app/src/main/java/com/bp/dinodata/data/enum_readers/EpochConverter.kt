package com.bp.dinodata.data.enum_readers

import com.bp.dinodata.data.time_period.Eras
import com.bp.dinodata.data.time_period.IEpoch
import com.bp.dinodata.data.time_period.ITimeEra
import com.bp.dinodata.data.time_period.ITimeInterval
import com.bp.dinodata.data.time_period.ITimeModifierKey
import com.bp.dinodata.data.time_period.Subepoch
import com.bp.dinodata.data.time_period.epochs.CenozoicEpochs
import com.bp.dinodata.data.time_period.epochs.MesozoicEpochs
import com.bp.dinodata.data.time_period.epochs.PaleozoicEpochs

object TimePeriods {
    val EpochMap: Map<String, IEpoch> =
            MesozoicEpochs.stringToEpochMap +
            CenozoicEpochs.stringToEpochMap +
            PaleozoicEpochs.stringToEpochMap

    val EpochModifierMap: Map<String, ITimeModifierKey>
        = Subepoch.entries.associateBy { it.name.lowercase() }

    val EraMap = Eras.getList().associateBy { it.getEnumId().toString().lowercase() }
}

object EraConverter: TypeConverter<ITimeEra>(TimePeriods.EraMap, "EraConverter")

object EpochConverter: TypeConverter<IEpoch>(TimePeriods.EpochMap, "EpochConverter") {

    override fun matchType(text: String): IEpoch? {
        val splits = text.split(" ", "_")
        var epoch: IEpoch? = null
        var modifier: ITimeModifierKey? = null

        for (split in splits) {
            val lowerSplit = split.lowercase()

            val foundEpoch = this.dataMap[lowerSplit]

            // If we recognise the string as an epoch
            if (foundEpoch != null) {
                epoch = foundEpoch
                continue
            }

            // Otherwise, check for a modifier (such as "Early")
            val foundModifier = TimePeriods.EpochModifierMap[lowerSplit]
            // If the given term is not found, keep the old modifier
            modifier = foundModifier ?: modifier
        }

        if (epoch == null) {
            return null
        }

        return modifier?.let { epoch.with(modifier) } ?: epoch
    }

    /**
     * Get the first (earliest) known epoch which contains the given interval.
     */
    fun getEpochFor(timeInterval: ITimeInterval): IEpoch? {
        val eras = Eras.getList().filter {
            it.overlapsWith(timeInterval)
        }

        if (eras.isEmpty()) {
            return null
        }

        return eras.first().getSubdivisions().firstOrNull {
            it.overlapsWith(timeInterval)
        }
    }
}