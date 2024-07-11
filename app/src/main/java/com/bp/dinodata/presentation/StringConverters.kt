package com.bp.dinodata.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.bp.dinodata.R
import com.bp.dinodata.data.CreatureType

@Composable
fun convertCreatureTypeToString(type: CreatureType): String? {
    return when (type) {
        CreatureType.LargeTheropod -> stringResource(R.string.type_large_theropod)
        CreatureType.SmallTheropod -> stringResource(R.string.type_small_theropod)
        CreatureType.Ceratopsian -> stringResource(R.string.type_ceratopsian)
        CreatureType.Armoured -> stringResource(R.string.type_armoured)
        CreatureType.Stegosaur -> stringResource(R.string.type_stegosaurid)
        CreatureType.Sauropod -> stringResource(R.string.type_sauropod)
        CreatureType.Hadrosaur -> stringResource(R.string.type_hadrosaur)
        CreatureType.Avian -> stringResource(R.string.type_avian)
        CreatureType.Aquatic -> stringResource(R.string.type_aquatic)
        CreatureType.Cenezoic -> stringResource(R.string.type_cenezoic)
        CreatureType.Spinosaur -> stringResource(R.string.type_spinosaur)
        CreatureType.Other -> stringResource(R.string.type_other)
    }
}