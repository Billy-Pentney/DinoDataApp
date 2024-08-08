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

    fun getList(): List<ITimeEra> {
        return EraId.entries.map { enumToEra(it) }
    }

    fun enumToEra(key: EraId): ITimeEra {
        return when (key) {
            EraId.Mesozoic -> Mesozoic
            EraId.Paleozoic -> Paleozoic
            EraId.Cenozoic -> Cenozoic
        }
    }

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
}

enum class EraId {
    Mesozoic, Paleozoic, Cenozoic
}