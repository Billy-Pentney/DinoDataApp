package com.bp.dinodata.data.time_period.epochs

import androidx.compose.ui.graphics.Color
import com.bp.dinodata.R
import com.bp.dinodata.data.time_period.era.EraId
import com.bp.dinodata.data.time_period.intervals.ITimeInterval
import com.bp.dinodata.data.time_period.intervals.TimeInterval
import com.bp.dinodata.theme.neogeneDark
import com.bp.dinodata.theme.neogeneLight
import com.bp.dinodata.theme.paleogeneDark
import com.bp.dinodata.theme.paleogeneLight
import com.bp.dinodata.theme.quaternaryDark
import com.bp.dinodata.theme.quaternaryLight


object CenozoicEpochs: IEpochManager<CenozoicEpochs.CenozoicEpochId> {

    enum class CenozoicEpochId: IEpochId {
        Paleogene, Neogene, Quaternary
    }

    override fun getAll(): List<IModifiableEpoch> {
        return CenozoicEpochId.entries.map { getEpoch(it) }
    }

    override fun getEnumFromString(text: String): CenozoicEpochId? {
        return stringToEnumMap[text]
    }

    val stringToEnumMap = CenozoicEpochId.entries.associateBy { it.name.lowercase() }
    val stringToEpochMap = stringToEnumMap.mapValues { getEpoch(it.value) }

    sealed class CenozoicEpoch(
        key: CenozoicEpochId,
        nameResId: Int,
        timePeriodRange: ITimeInterval,
        colorLight: Color,
        colorDark: Color = colorLight
    ): Epoch(
        era = EraId.Cenozoic,
        epochKey = key,
        nameResId = nameResId,
        timePeriodRange = timePeriodRange,
        colorLight = colorLight,
        colorDark = colorDark
    ), IEpochRetriever<CenozoicEpochId> {
        override fun getEpoch(id: CenozoicEpochId): Epoch {
            return CenozoicEpochs.getEpoch(id)
        }
    }




    data object Paleogene: CenozoicEpoch(
        CenozoicEpochId.Paleogene,
        nameResId = R.string.time_period_paleogene,
        timePeriodRange = TimeInterval(66f, 23.03f),
        colorLight = paleogeneLight,
        colorDark = paleogeneDark
    )
    data object Neogene: CenozoicEpoch(
        CenozoicEpochId.Neogene,
        nameResId = R.string.time_period_neogene,
        timePeriodRange = TimeInterval(23.03f, 2.58f),
        colorLight = neogeneLight,
        colorDark = neogeneDark
    )
    data object Quaternary: CenozoicEpoch(
        CenozoicEpochId.Quaternary,
        nameResId = R.string.time_period_quaternary,
        timePeriodRange = TimeInterval(2.58f, 0f),
        colorLight = quaternaryLight,
        colorDark = quaternaryDark
    )

    override fun getEpoch(id: CenozoicEpochId): CenozoicEpoch {
        return when(id) {
            CenozoicEpochId.Paleogene -> Paleogene
            CenozoicEpochId.Neogene -> Neogene
            CenozoicEpochId.Quaternary -> Quaternary
        }
    }
}