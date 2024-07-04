package com.bp.dinodata.presentation.utils

import com.bp.dinodata.R
import com.bp.dinodata.data.CreatureType

fun ConvertCreatureTypeToSilhouette(type: CreatureType): Int {
    return when (type) {
        CreatureType.Ceratopsian        -> R.drawable.ceratopsian
        CreatureType.LargeTheropod      -> R.drawable.acrocanth
        CreatureType.Sauropod           -> R.drawable.camara
        CreatureType.Hadrosaur          -> R.drawable.edmonto
        CreatureType.Avian              -> R.drawable.ptero
        CreatureType.Armoured           -> R.drawable.ankylo
        CreatureType.SmallTheropod      -> R.drawable.raptor
        CreatureType.Stegosaur          -> R.drawable.stego
        CreatureType.Spinosaur          -> R.drawable.spino
        else                            -> R.drawable.unkn
    }
}