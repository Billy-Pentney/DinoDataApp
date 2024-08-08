package com.bp.dinodata.data.time_period.epochs

import androidx.compose.ui.graphics.Color
import com.bp.dinodata.R
import com.bp.dinodata.data.time_period.Epoch
import com.bp.dinodata.data.time_period.EraId
import com.bp.dinodata.data.time_period.IEpochId
import com.bp.dinodata.data.time_period.IModifiableEpoch
import com.bp.dinodata.data.time_period.ITimeInterval
import com.bp.dinodata.data.time_period.ITimeModifierKey
import com.bp.dinodata.data.time_period.SubEpochModifier
import com.bp.dinodata.data.time_period.TimeInterval
import com.bp.dinodata.data.time_period.modifiers.TimeModifier
import com.bp.dinodata.theme.Cretaceous300
import com.bp.dinodata.theme.Cretaceous600
import com.bp.dinodata.theme.Jurassic300
import com.bp.dinodata.theme.Jurassic700
import com.bp.dinodata.theme.Triassic200
import com.bp.dinodata.theme.Triassic800


object MesozoicEpochs: IEpochManager<MesozoicEpochs.MesozoicEpochId> {
    enum class MesozoicEpochId: IEpochId {
        Cretaceous, Jurassic, Triassic
    }

    override fun getAll(): List<IModifiableEpoch> {
        return MesozoicEpochId.entries.map { getEpoch(it) }
    }

    override fun getEnumFromString(text: String): IEpochId? {
        return stringToEnumMap[text]
    }

    val stringToEnumMap = MesozoicEpochId.entries.associateBy { it.name.lowercase() }
    val stringToEpochMap = stringToEnumMap.mapValues { getEpoch(it.value) }

    private const val TRIASSIC_JURASSIC_SPLIT = 201.4f
    private const val JURASSIC_CRETACEOUS_SPLIT = 145f

    private const val START_TRIASSIC = 251.9f
    private const val END_TRIASSIC = TRIASSIC_JURASSIC_SPLIT
    private const val START_JURASSIC = TRIASSIC_JURASSIC_SPLIT
    private const val END_JURASSIC = JURASSIC_CRETACEOUS_SPLIT
    private const val START_CRETACEOUS = JURASSIC_CRETACEOUS_SPLIT
    private const val END_CRETACEOUS = 65.5f

    sealed class MesozoicEpoch(
        key: MesozoicEpochId,
        nameResId: Int,
        timePeriodRange: ITimeInterval,
        colorLight: Color,
        colorDark: Color = colorLight,
        subIntervals: Map<ITimeModifierKey, TimeInterval> = emptyMap()
    ): Epoch(
        era = EraId.Mesozoic,
        epochKey = key,
        nameResId = nameResId,
        timePeriodRange = timePeriodRange,
        colorLight = colorLight,
        colorDark = colorDark,
        subIntervalMap = subIntervals.mapValues { TimeModifier(it.key, it.value) }
    ), IEpochRetriever<MesozoicEpochId> {
        override fun getEpoch(id: MesozoicEpochId): Epoch {
            return MesozoicEpochs.getEpoch(id)
        }
    }

    data object Triassic: MesozoicEpoch(
        key = MesozoicEpochId.Triassic,
        nameResId = R.string.time_period_triassic,
        timePeriodRange = TimeInterval(START_TRIASSIC, END_TRIASSIC),
        colorLight = Triassic200,
        colorDark = Triassic800,
        subIntervals = mapOf(
            SubEpochModifier.Early to TimeInterval(START_TRIASSIC, 247.2f),
            SubEpochModifier.Middle to TimeInterval(247.2f, 237f),
            SubEpochModifier.Late to TimeInterval(237f, END_TRIASSIC)
        )
    )

    data object Jurassic: MesozoicEpoch(
        MesozoicEpochId.Jurassic,
        timePeriodRange = TimeInterval(START_JURASSIC, END_JURASSIC),
        nameResId = R.string.time_period_jurassic,
        colorLight = Jurassic300,
        colorDark = Jurassic700,
        subIntervals = mapOf(
            SubEpochModifier.Early to TimeInterval(START_JURASSIC, 174.7f),
            SubEpochModifier.Middle to TimeInterval(174.7f, 161.5f),
            SubEpochModifier.Late to TimeInterval(161.5f, END_JURASSIC)
        )
    )

    data object Cretaceous: MesozoicEpoch(
        MesozoicEpochId.Cretaceous,
        timePeriodRange = TimeInterval(START_CRETACEOUS, END_CRETACEOUS),
        nameResId = R.string.time_period_cretaceous,
        colorLight = Cretaceous300,
        colorDark = Cretaceous600,
        subIntervals = mapOf(
            SubEpochModifier.Early to TimeInterval(START_CRETACEOUS, 100.5f),
            // No middle for Cretaceous
            SubEpochModifier.Late to TimeInterval(100.5f, END_CRETACEOUS)
        )
    )

    override fun getEpoch(id: MesozoicEpochId): MesozoicEpoch {
        return when (id) {
            MesozoicEpochId.Cretaceous -> Cretaceous
            MesozoicEpochId.Jurassic -> Jurassic
            MesozoicEpochId.Triassic -> Triassic
        }
    }
}