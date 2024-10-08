package com.bp.dinodata.data.time_period.epochs

import androidx.compose.ui.graphics.Color
import com.bp.dinodata.R
import com.bp.dinodata.data.time_period.era.EraId
import com.bp.dinodata.data.time_period.intervals.ITimeInterval
import com.bp.dinodata.data.time_period.intervals.TimeInterval
import com.bp.dinodata.theme.cambrianDark
import com.bp.dinodata.theme.cambrianLight
import com.bp.dinodata.theme.carboniferousDark
import com.bp.dinodata.theme.carboniferousLight
import com.bp.dinodata.theme.devonianDark
import com.bp.dinodata.theme.devonianLight
import com.bp.dinodata.theme.ordovicianDark
import com.bp.dinodata.theme.ordovicianLight
import com.bp.dinodata.theme.permianDark
import com.bp.dinodata.theme.permianLight
import com.bp.dinodata.theme.silurianDark
import com.bp.dinodata.theme.silurianLight


object PaleozoicEpochs: IEpochManager<PaleozoicEpochs.PaleozoicEpochId> {
    enum class PaleozoicEpochId: IEpochId {
        Cambrian, Ordovician, Carboniferous, Devonian, Permian, Silurian
    }

    override fun getAll(): List<IModifiableEpoch> {
        return PaleozoicEpochId.entries.map { getEpoch(it) }
    }

    override fun getEnumFromString(text: String): PaleozoicEpochId? {
        return stringToEnumMap[text]
    }

    val stringToEnumMap = PaleozoicEpochId.entries.associateBy { it.toString().lowercase() }
    val stringToEpochMap = stringToEnumMap.mapValues { getEpoch(it.value) }

    sealed class PaleozoicEpoch(
        key: PaleozoicEpochId,
        nameResId: Int,
        timePeriodRange: ITimeInterval,
        colorLight: Color,
        colorDark: Color = colorLight
    ): Epoch(
        era = EraId.Paleozoic,
        epochKey = key,
        nameResId = nameResId,
        timePeriodRange = timePeriodRange,
        colorLight = colorLight,
        colorDark = colorDark
    ), IEpochRetriever<PaleozoicEpochId> {
        override fun getEpoch(id: PaleozoicEpochId): Epoch {
            return PaleozoicEpochs.getEpoch(id)
        }
    }

    override fun getEpoch(id: PaleozoicEpochId): PaleozoicEpoch {
        return when (id) {
            PaleozoicEpochId.Cambrian -> Cambrian
            PaleozoicEpochId.Ordovician -> Ordovician
            PaleozoicEpochId.Carboniferous -> Carboniferous
            PaleozoicEpochId.Devonian -> Devonian
            PaleozoicEpochId.Permian -> Permian
            PaleozoicEpochId.Silurian -> Silurian
        }
    }

    /** Defining the Epochs */

    data object Ordovician: PaleozoicEpoch(
        PaleozoicEpochId.Ordovician,
        nameResId = R.string.time_period_ordovician,
        timePeriodRange = TimeInterval(485.4f, 443.8f),
        colorLight = ordovicianLight,
        colorDark = ordovicianDark
    )
    data object Cambrian: PaleozoicEpoch(
        PaleozoicEpochId.Cambrian,
        nameResId = R.string.time_period_cambrian,
        timePeriodRange = TimeInterval(538.8f, 485.4f),
        colorLight = cambrianLight,
        colorDark = cambrianDark
    )
    data object Permian: PaleozoicEpoch(
        PaleozoicEpochId.Permian,
        nameResId = R.string.time_period_permian,
        timePeriodRange = TimeInterval(298.9f, 251.9f),
        colorLight = permianLight,
        colorDark = permianDark
    )

    data object Carboniferous: PaleozoicEpoch(
        PaleozoicEpochId.Carboniferous,
        nameResId = R.string.time_period_carboniferous,
        timePeriodRange = TimeInterval(358.9f, 298.9f),
        colorLight = carboniferousLight,
        colorDark = carboniferousDark
    )

    data object Devonian: PaleozoicEpoch(
        PaleozoicEpochId.Devonian,
        nameResId = R.string.time_period_devonian,
        timePeriodRange = TimeInterval(419.2f, 358.9f),
        colorLight = devonianLight,
        colorDark = devonianDark
    )

    data object Silurian: PaleozoicEpoch(
        PaleozoicEpochId.Silurian,
        nameResId = R.string.time_period_silurian,
        timePeriodRange = TimeInterval(443.8f, 419.2f),
        colorLight = silurianLight,
        colorDark = silurianDark
    )
}