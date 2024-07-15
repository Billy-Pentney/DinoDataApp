package com.bp.dinodata.presentation.utils

import com.bp.dinodata.R
import com.bp.dinodata.data.CreatureType

fun convertCreatureTypeToSilhouette(type: CreatureType): Int? {
    return when (type) {
        CreatureType.Ceratopsian        -> R.drawable.type_ceratopsian
        CreatureType.LargeTheropod      -> R.drawable.type_carcharo
        CreatureType.Sauropod           -> R.drawable.type_sauropod
        CreatureType.Hadrosaur          -> R.drawable.type_hadrosaur
        CreatureType.Pterosaur          -> R.drawable.type_pterosaur
        CreatureType.Ankylosaur         -> R.drawable.type_ankylosaur
        CreatureType.Synapsid           -> R.drawable.type_synapsid
        CreatureType.Pachycephalosaur   -> R.drawable.type_pachycephalosaur
        CreatureType.Ornithomimid       -> R.drawable.type_ornithomimid
        CreatureType.SmallTheropod      -> R.drawable.type_raptor
        CreatureType.Stegosaur          -> R.drawable.type_stegosaur
        CreatureType.Spinosaur          -> R.drawable.type_spinosaur
        else                            -> null
    }
}