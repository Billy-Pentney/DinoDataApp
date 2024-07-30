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
        CreatureType.Ankylosaur -> stringResource(R.string.type_ankylosaur)
        CreatureType.Stegosaur -> stringResource(R.string.type_stegosaurid)
        CreatureType.Sauropod -> stringResource(R.string.type_sauropod)
        CreatureType.Hadrosaur -> stringResource(R.string.type_hadrosaur)
        CreatureType.Pterosaur -> stringResource(R.string.type_pterosaur)
        CreatureType.Aquatic -> stringResource(R.string.type_aquatic)
        CreatureType.Cenezoic -> stringResource(R.string.type_cenezoic)
        CreatureType.Spinosaur -> stringResource(R.string.type_spinosaur)
        CreatureType.Pachycephalosaur -> stringResource(R.string.type_pachycephalosaur)
        CreatureType.Ornithomimid -> stringResource(R.string.type_ornithomimid)
        CreatureType.Synapsid -> stringResource(R.string.type_synapsid)
        CreatureType.Other -> stringResource(R.string.type_other)
        CreatureType.Dromaeosaurid -> stringResource(R.string.type_dromaeosaurid)
        CreatureType.Carcharodontosaurid -> stringResource(R.string.type_carcharodontosaurid)
        CreatureType.Abelisaurid -> stringResource(R.string.type_abelisaurid)
        CreatureType.Euornithopod -> stringResource(R.string.type_euornithopod)
        CreatureType.Tyrannosaurid -> stringResource(R.string.type_tyrannosaurid)
        CreatureType.MediumTheropod -> stringResource(R.string.type_medium_theropod)
        CreatureType.Plesiosaur -> stringResource(R.string.type_plesiosaur)
        CreatureType.Therizinosaurid -> stringResource(R.string.type_therizinosaurid)
        CreatureType.Iguanodontid -> stringResource(R.string.type_iguanodontid)
        CreatureType.Crocodilian -> stringResource(R.string.type_crocodilian)
        CreatureType.Serpent -> stringResource(R.string.type_serpent)
    }
}