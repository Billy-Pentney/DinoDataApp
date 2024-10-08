package com.bp.dinodata.presentation.utils

import com.bp.dinodata.R
import com.bp.dinodata.data.CreatureType


fun convertCreatureTypeToSilhouette(
    type: CreatureType,
//    quality: SilhouetteQuality = SilhouetteQuality.x64
): Int? {
    return when (type) {
        CreatureType.Ceratopsian        -> R.drawable.type_ceratopsian
        CreatureType.LargeTheropod      -> R.drawable.type_carcharo
        CreatureType.Sauropod           -> R.drawable.type_sauropod
        CreatureType.Hadrosaur          -> R.drawable.type_hadrosaur
        CreatureType.Pterosaur          -> R.drawable.type_pterosaur
        CreatureType.Ankylosaur         -> R.drawable.type_ankylosaur
        CreatureType.Pachycephalosaur   -> R.drawable.type_pachycephalosaur
        CreatureType.Ornithomimid       -> R.drawable.type_ornithomimid
        CreatureType.SmallTheropod      -> R.drawable.type_heterodonto
        CreatureType.Stegosaur          -> R.drawable.type_stegosaur
        CreatureType.Spinosaur          -> R.drawable.type_spinosaur
        CreatureType.Dromaeosaurid      -> R.drawable.type_dromaeosaur
        CreatureType.Carcharodontosaurid -> R.drawable.type_carcharo
        CreatureType.Abelisaurid        -> R.drawable.type_abeli
        CreatureType.Therizinosaurid    -> R.drawable.type_therizinosaur
        CreatureType.Tyrannosaurid      -> R.drawable.type_tyrannosaur
        CreatureType.MediumTheropod     -> R.drawable.type_allosaur
        CreatureType.Plesiosaur         -> R.drawable.type_plesiosaur
        CreatureType.Iguanodontid       -> R.drawable.type_iguanodontid
        CreatureType.Crocodilian        -> R.drawable.type_crocodilian
        CreatureType.Serpent            -> R.drawable.type_serpent
        CreatureType.Mosasaur           -> R.drawable.type_mosasaur
        CreatureType.Ichthyosaur        -> R.drawable.type_ichthyosaur
        CreatureType.Synapsid           -> R.drawable.type_synapsid
        CreatureType.SpinedSynapsid     -> R.drawable.type_synapsid_spined
        //        CreatureType.Aquatic -> TODO()
        //        CreatureType.Cenezoic -> TODO()
        CreatureType.Other              -> R.drawable.type_unknown
        else                            -> null
    }
}