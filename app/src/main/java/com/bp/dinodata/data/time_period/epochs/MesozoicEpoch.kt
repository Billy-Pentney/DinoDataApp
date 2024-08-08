package com.bp.dinodata.data.time_period.epochs

import androidx.compose.ui.graphics.Color
import com.bp.dinodata.R
import com.bp.dinodata.data.time_period.Epoch
import com.bp.dinodata.data.time_period.EraId
import com.bp.dinodata.data.time_period.IEpoch
import com.bp.dinodata.data.time_period.IEpochId
import com.bp.dinodata.data.time_period.ITimeInterval
import com.bp.dinodata.data.time_period.ITimeModifierKey
import com.bp.dinodata.data.time_period.Subepoch
import com.bp.dinodata.data.time_period.TimeInterval
import com.bp.dinodata.data.time_period.TimeModifier
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

    override fun getAll(): List<IEpoch> {
        return MesozoicEpochId.entries.map { MesozoicEpochs.enumToEpoch(it) }
    }

    override fun getEnumFromString(text: String): IEpochId? {
        return stringToEnumMap[text]
    }

    val stringToEnumMap = mapOf(
        "cretaceous" to MesozoicEpochId.Cretaceous,
        "jurassic" to MesozoicEpochId.Jurassic,
        "triassic" to MesozoicEpochId.Triassic,
    )

    val stringToEpochMap = stringToEnumMap.mapValues { enumToEpoch(it.value) }

    private val TRIASSIC_JURASSIC_SPLIT = 201.4f
    private val JURASSIC_CRETACEOUS_SPLIT = 145f

    private val START_TRIASSIC = 251.9f
    private val END_TRIASSIC = TRIASSIC_JURASSIC_SPLIT
    private val START_JURASSIC = TRIASSIC_JURASSIC_SPLIT
    private val END_JURASSIC = JURASSIC_CRETACEOUS_SPLIT
    private val START_CRETACEOUS = JURASSIC_CRETACEOUS_SPLIT
    private val END_CRETACEOUS = 65.5f

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
    )

    data object Triassic: MesozoicEpoch(
        key = MesozoicEpochId.Triassic,
        nameResId = R.string.time_period_triassic,
        timePeriodRange = TimeInterval(START_TRIASSIC, END_TRIASSIC),
        colorLight = Triassic200,
        colorDark = Triassic800,
        subIntervals = mapOf(
            Subepoch.Early to TimeInterval(START_TRIASSIC, 247.2f),
            Subepoch.Middle to TimeInterval(247.2f, 237f),
            Subepoch.Late to TimeInterval(237f, END_TRIASSIC)
        )
    )

    data object Jurassic: MesozoicEpoch(
        MesozoicEpochId.Jurassic,
        timePeriodRange = TimeInterval(START_JURASSIC, END_JURASSIC),
        nameResId = R.string.time_period_jurassic,
        colorLight = Jurassic300,
        colorDark = Jurassic700,
        subIntervals = mapOf(
            Subepoch.Early to TimeInterval(START_JURASSIC, 174.7f),
            Subepoch.Middle to TimeInterval(174.7f, 161.5f),
            Subepoch.Late to TimeInterval(161.5f, END_JURASSIC)
        )
    )

    data object Cretaceous: MesozoicEpoch(
        MesozoicEpochId.Cretaceous,
        timePeriodRange = TimeInterval(START_CRETACEOUS, END_CRETACEOUS),
        nameResId = R.string.time_period_cretaceous,
        colorLight = Cretaceous300,
        colorDark = Cretaceous600,
        subIntervals = mapOf(
            Subepoch.Early to TimeInterval(START_CRETACEOUS, 100.5f),
            // No middle for Cretaceous
            Subepoch.Late to TimeInterval(100.5f, END_CRETACEOUS)
        )
    )

    override fun enumToEpoch(key: MesozoicEpochId): MesozoicEpoch {
        return when(key) {
            MesozoicEpochId.Cretaceous -> Cretaceous
            MesozoicEpochId.Jurassic -> Jurassic
            MesozoicEpochId.Triassic -> Triassic
        }
    }
}