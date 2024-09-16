package com.bp.dinodata.presentation.utils

import com.bp.dinodata.R
import com.bp.dinodata.data.CreatureType


fun convertCreatureTypeToSilhouette(type: CreatureType): Int {
    return when (type) {
        CreatureType.Ceratopsian        -> R.drawable.type_ljust_ceratopsian
        CreatureType.LargeTheropod      -> R.drawable.type_ljust_carcharodontosaur
        CreatureType.Sauropod           -> R.drawable.type_ljust_sauropod
        CreatureType.Hadrosaur          -> R.drawable.type_ljust_hadrosaur
        CreatureType.Pterosaur          -> R.drawable.type_ljust_pterosaur
        CreatureType.Ankylosaur         -> R.drawable.type_ljust_ankylosaur
        CreatureType.Pachycephalosaur   -> R.drawable.type_ljust_pachycephalosaur
        CreatureType.Ornithomimid       -> R.drawable.type_ljust_ornithomimid
        CreatureType.SmallTheropod      -> R.drawable.type_ljust_heterodontosaur
        CreatureType.Stegosaur          -> R.drawable.type_ljust_stegosaur
        CreatureType.Spinosaur          -> R.drawable.type_ljust_spinosaur
        CreatureType.Dromaeosaurid      -> R.drawable.type_ljust_dromaeosaur
        CreatureType.Carcharodontosaurid -> R.drawable.type_ljust_carcharodontosaur
        CreatureType.Abelisaurid        -> R.drawable.type_ljust_abelisaurid
        CreatureType.Therizinosaurid    -> R.drawable.type_ljust_therizinosaur
        CreatureType.Tyrannosaurid      -> R.drawable.type_ljust_tyrannosaur
        CreatureType.MediumTheropod     -> R.drawable.type_ljust_allosaur
        CreatureType.Plesiosaur         -> R.drawable.type_ljust_plesiosaur
        CreatureType.Iguanodontian       -> R.drawable.type_ljust_iguanodont
        CreatureType.Crocodilian        -> R.drawable.type_ljust_crocodilian
        CreatureType.Serpent            -> R.drawable.type_ljust_serpent
        CreatureType.Mosasaur           -> R.drawable.type_ljust_mosasaur
        CreatureType.Ichthyosaur        -> R.drawable.type_ljust_ichthyosaur
        CreatureType.Synapsid           -> R.drawable.type_ljust_synapsid
        CreatureType.SynapsidWithSail     -> R.drawable.type_ljust_synapsid_sail
        CreatureType.Aquatic,
        CreatureType.Other              -> R.drawable.type_ljust_unknown
    }
}



fun convertCreatureTypeToSilhouetteCentered(type: CreatureType): Int {
    return when (type) {
        CreatureType.Ceratopsian        -> R.drawable.type_centred_ceratopsian
        CreatureType.LargeTheropod      -> R.drawable.type_centred_carcharodontosaur
        CreatureType.Sauropod           -> R.drawable.type_centred_sauropod
        CreatureType.Hadrosaur          -> R.drawable.type_centred_hadrosaur
        CreatureType.Pterosaur          -> R.drawable.type_centred_pterosaur
        CreatureType.Ankylosaur         -> R.drawable.type_centred_ankylosaur
        CreatureType.Pachycephalosaur   -> R.drawable.type_centred_pachycephalosaur
        CreatureType.Ornithomimid       -> R.drawable.type_centred_ornithomimid
        CreatureType.SmallTheropod      -> R.drawable.type_centred_heterodontosaur
        CreatureType.Stegosaur          -> R.drawable.type_centred_stegosaur
        CreatureType.Spinosaur          -> R.drawable.type_centred_spinosaur_sail
        CreatureType.Dromaeosaurid      -> R.drawable.type_centred_dromaeosaur
        CreatureType.Carcharodontosaurid -> R.drawable.type_centred_carcharodontosaur
        CreatureType.Abelisaurid        -> R.drawable.type_centred_abelisaurid
        CreatureType.Therizinosaurid    -> R.drawable.type_centred_therizinosaur
        CreatureType.Tyrannosaurid      -> R.drawable.type_centred_tyrannosaur
        CreatureType.MediumTheropod     -> R.drawable.type_centred_allosaur
        CreatureType.Plesiosaur         -> R.drawable.type_centred_plesiosaur
        CreatureType.Iguanodontian       -> R.drawable.type_centred_iguanodont
        CreatureType.Crocodilian        -> R.drawable.type_centred_crocodilian
        CreatureType.Serpent            -> R.drawable.type_centred_serpent
        CreatureType.Mosasaur           -> R.drawable.type_centred_mosasaur
        CreatureType.Ichthyosaur        -> R.drawable.type_centred_ichthyosaur
        CreatureType.Synapsid           -> R.drawable.type_centred_synapsid
        CreatureType.SynapsidWithSail   -> R.drawable.type_centred_synapsid_sail
        CreatureType.Aquatic,
        CreatureType.Other              -> R.drawable.unkn
    }
}