package com.bp.dinodata.data.time_period

import androidx.compose.ui.graphics.Color

interface IEpoch: IDisplayableTimePeriod, IPartitionedTimePeriod, IEpochId {
    fun getEraId(): EraId
    fun getEpochId(): IEpochId
    fun with(modifierKey: ITimeModifierKey): IEpoch?

    fun getSubepochs(): List<IEpoch>
}

interface IEpochId


abstract class Epoch(
    private val era: EraId,
    private val epochKey: IEpochId,
    nameResId: Int,
    private val timePeriodRange: ITimeInterval,
    colorLight: Color,
    colorDark: Color = colorLight,
    private val subIntervalMap: Map<ITimeModifierKey, ITimeModifier> = emptyMap(),
    private val stages: List<TimeStage> = emptyList()
): DisplayableTimePeriod(epochKey.toString(), nameResId, colorLight, colorDark), IEpoch {

    override fun getSubepochs(): List<IEpoch> {
        return subIntervalMap.values.map { it.applyTo(this) }
    }
    override fun getSubdivisions(): List<IDisplayableTimePeriod> {
        return stages.ifEmpty {
            subIntervalMap.values.map { it.applyTo(this) }
        }.ifEmpty {
            listOf(this)
        }
    }

    override fun getTimeInterval(): ITimeInterval = timePeriodRange
    override fun getEraId(): EraId = era
    override fun getEpochId(): IEpochId = epochKey

    override fun equals(other: Any?): Boolean {
        if (other is IEpoch) {
            return this.era == other.getEraId() && this.epochKey == other.getEpochId()
        }
        return false
    }

    /** Auto-generated */
    override fun hashCode(): Int {
        var result = era.hashCode()
        result = 31 * result + epochKey.hashCode()
        result = 31 * result + timePeriodRange.hashCode()
        return result
    }

    override fun with(modifierKey: ITimeModifierKey): IEpoch? {
        val modifier = subIntervalMap[modifierKey]
        return modifier?.let { modifier.applyTo(this) }
    }
}



