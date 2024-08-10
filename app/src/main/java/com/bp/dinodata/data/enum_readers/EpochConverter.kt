package com.bp.dinodata.data.enum_readers

import com.bp.dinodata.data.time_period.era.Eras
import com.bp.dinodata.data.time_period.epochs.IEpoch
import com.bp.dinodata.data.time_period.epochs.IEpochId
import com.bp.dinodata.data.time_period.epochs.IModifiableEpoch
import com.bp.dinodata.data.time_period.era.ITimeEra
import com.bp.dinodata.data.time_period.intervals.ITimeInterval
import com.bp.dinodata.data.time_period.modifiers.ITimeModifierKey
import com.bp.dinodata.data.time_period.modifiers.SubEpochModifier
import com.bp.dinodata.data.time_period.epochs.CenozoicEpochs
import com.bp.dinodata.data.time_period.epochs.MesozoicEpochs
import com.bp.dinodata.data.time_period.epochs.PaleozoicEpochs

object TimePeriods {
    val EpochMap: Map<String, IModifiableEpoch> =
            MesozoicEpochs.stringToEpochMap +
            CenozoicEpochs.stringToEpochMap +
            PaleozoicEpochs.stringToEpochMap

    val EpochModifierMap: Map<String, ITimeModifierKey>
        = SubEpochModifier.entries.associateBy { it.name.lowercase() }

    val EraMap = Eras.getStringToTypeMapping()
}

object EraConverter: TypeConverter<ITimeEra>(TimePeriods.EraMap, "EraConverter")

object EpochConverter: TypeConverter<IEpoch>(
    TimePeriods.EpochMap,
    "EpochConverter"
) {

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

        if (epoch is IModifiableEpoch) {
            return modifier?.let { epoch.with(modifier) } ?: epoch
        }

        return epoch
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

    /**
     * Returns a list of all epochs which have a non-zero overlap with this interval.
     */
    fun getEpochsIdsFor(timeInterval: ITimeInterval): List<IEpochId> {
        val eras = Eras.getList().filter {
            it.overlapsWith(timeInterval)
        }

        if (eras.isEmpty()) {
            return emptyList()
        }

        // Get a list of all epochs then check for the overlap
        return eras
                .flatMap { it.getSubdivisions() }
                .filter { it.overlapsWith(timeInterval) }
                .map { it.getEpochId() }
    }
}