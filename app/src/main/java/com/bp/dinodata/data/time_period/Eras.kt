package com.bp.dinodata.data.time_period

import com.bp.dinodata.R
import com.bp.dinodata.data.time_period.epochs.CenozoicEpochs
import com.bp.dinodata.data.time_period.epochs.MesozoicEpochs
import com.bp.dinodata.data.time_period.epochs.PaleozoicEpochs
import com.bp.dinodata.theme.Jurassic300
import com.bp.dinodata.theme.Jurassic700
import com.bp.dinodata.theme.cambrianDark
import com.bp.dinodata.theme.cambrianLight
import com.bp.dinodata.theme.neogeneDark
import com.bp.dinodata.theme.neogeneLight


object Eras {

    val Mesozoic = TimeEra(
        EraId.Mesozoic,
        R.string.time_era_mesozoic,
        epochs = MesozoicEpochs.getAll(),
        colorLight = Jurassic300,
        colorDark = Jurassic700
    )

    val Paleozoic = TimeEra(
        EraId.Paleozoic,
        nameResId = R.string.time_era_paleozoic,
        epochs = PaleozoicEpochs.getAll(),
        colorLight = cambrianLight,
        colorDark = cambrianDark
    )

    val Cenozoic = TimeEra(
        EraId.Cenozoic,
        nameResId = R.string.time_era_cenozoic,
        epochs = CenozoicEpochs.getAll(),
        colorLight = neogeneLight,
        colorDark = neogeneDark
    )

    private val enumToEraMap = mapOf(
        EraId.Mesozoic to Mesozoic,
        EraId.Paleozoic to Paleozoic,
        EraId.Cenozoic to Cenozoic
    )

    fun getList(): List<ITimeEra> {
        return enumToEraMap.values.toList()
    }

    fun getEra(key: EraId): ITimeEra {
        return enumToEraMap[key]!!
    }

    fun getStringToTypeMapping(): Map<String, ITimeEra> {
        return enumToEraMap.mapKeys { it.key.name.lowercase() }
    }

    fun getStringToEnumMapping(): Map<String, EraId> {
        return EraId.entries.associateBy { it.name.lowercase() }
    }
}

interface IEraId

enum class EraId: IEraId {
    Mesozoic, Paleozoic, Cenozoic
}