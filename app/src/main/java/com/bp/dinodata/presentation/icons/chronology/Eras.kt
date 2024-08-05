package com.bp.dinodata.presentation.icons.chronology

import androidx.compose.ui.graphics.Color
import com.bp.dinodata.R
import com.bp.dinodata.theme.Cretaceous300
import com.bp.dinodata.theme.Cretaceous600
import com.bp.dinodata.theme.Jurassic300
import com.bp.dinodata.theme.Jurassic700
import com.bp.dinodata.theme.Triassic200
import com.bp.dinodata.theme.Triassic800


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
            MesozoicEra.Triassic,
            MesozoicEra.Jurassic,
            MesozoicEra.Cretaceous
        )
    )

    val Paleozoic = TimeEra(
        nameResId = R.string.time_era_paleozoic,
        periods = listOf(
            PaleozoicEra.Cambrian,
            PaleozoicEra.Ordovician,
            PaleozoicEra.Silurian,
            PaleozoicEra.Devonian,
            PaleozoicEra.Carboniferous,
            PaleozoicEra.Permian
        )
    )

    val Cenozoic = TimeEra(
        nameResId = R.string.time_era_cenozoic,
        periods = listOf(
            TimePeriodBar(
                TimeInterval(66f, 23.03f),
                R.string.time_period_paleogene,
                Color(0xFFFF9933)
            ),
            TimePeriodBar(
                TimeInterval(23.03f, 2.58f),
                R.string.time_period_neogene,
                Color(0xFFEEDD33)
            ),
            TimePeriodBar(
                TimeInterval(2.58f, 0f),
                R.string.time_period_quaternary,
                Color(0xFFEEFFBB)
            )
        )
    )
}



object PaleozoicEra {

    val Permian = TimePeriodBar(
        TimeInterval(298.9f, 251.9f),
        R.string.time_period_permian,
        Color(0xFFEE5555)
    )

    val Carboniferous = TimePeriodBar(
        TimeInterval(358.9f, 298.9f),
        R.string.time_period_carboniferous,
        Color(0xFF008080)
    )

    val Devonian = TimePeriodBar(
        TimeInterval(419.2f, 358.9f),
        R.string.time_period_devonian,
        Color(0xFFDAA520)
    )

    val Silurian = TimePeriodBar(
        TimeInterval(443.8f, 419.2f),
        R.string.time_period_silurian,
        Color(0xFFAAFBAA)
    )

    val Ordovician = TimePeriodBar(
        TimeInterval(485.4f, 443.8f),
        R.string.time_period_ordovician,
        Color(0xFF800080)
    )

    val Cambrian = TimePeriodBar(
        TimeInterval(538.8f, 485.4f),
        R.string.time_period_cambrian,
        Color(0xFF77BBEE)
    )

    val periods = listOf(
        Cambrian,
        Ordovician,
        Silurian,
        Devonian,
        Carboniferous,
        Permian
    )


}

object MesozoicEra {

    val Triassic = TimePeriodBar(
        timePeriodRange = TimeInterval(251.9f, 201.4f),
        nameResId = R.string.time_period_triassic,
        colorLight = Triassic200,
        colorDark = Triassic800
    )

    val Jurassic = TimePeriodBar(
        timePeriodRange = TimeInterval(201.4f, 145f),
        nameResId = R.string.time_period_jurassic,
        colorLight = Jurassic300,
        colorDark = Jurassic700
    )

    val Cretaceous = TimePeriodBar(
        timePeriodRange = TimeInterval(145f, 65.5f),
        nameResId = R.string.time_period_cretaceous,
        colorLight = Cretaceous300,
        colorDark = Cretaceous600
    )
}