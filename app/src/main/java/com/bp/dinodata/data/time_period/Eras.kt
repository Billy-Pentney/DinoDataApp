package com.bp.dinodata.data.time_period

import com.bp.dinodata.R


object Eras {
    fun getList(): List<TimeEra> {
        return listOf(
            Paleozoic,
            Mesozoic,
            Cenozoic
        )
    }

    val Mesozoic = TimeEra(
        R.string.time_era_mesozoic,
        periods = listOf(
            MesozoicEpoch.Triassic,
            MesozoicEpoch.Jurassic,
            MesozoicEpoch.Cretaceous
        )
    )

    val Paleozoic = TimeEra(
        nameResId = R.string.time_era_paleozoic,
        periods = listOf(
            PaleozoicEpoch.Cambrian,
            PaleozoicEpoch.Ordovician,
            PaleozoicEpoch.Silurian,
            PaleozoicEpoch.Devonian,
            PaleozoicEpoch.Carboniferous,
            PaleozoicEpoch.Permian
        )
    )

    val Cenozoic = TimeEra(
        nameResId = R.string.time_era_cenozoic,
        periods = listOf(
            CenozoicEpoch.Paleogene,
            CenozoicEpoch.Neogene,
            CenozoicEpoch.Quaternary
        )
    )
}



object PaleozoicEra {


}