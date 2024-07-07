package com.bp.dinodata.presentation.utils

import com.bp.dinodata.R
import com.bp.dinodata.data.CreatureType

fun convertCreatureTypeToSilhouette(type: CreatureType): Int {
    return when (type) {
        CreatureType.Ceratopsian        -> R.drawable.styraco
        CreatureType.LargeTheropod      -> R.drawable.acrocanth
        CreatureType.Sauropod           -> R.drawable.cetio
        CreatureType.Hadrosaur          -> R.drawable.edmonto
        CreatureType.Avian              -> R.drawable.ptero
        CreatureType.Armoured           -> R.drawable.ankylo
        CreatureType.SmallTheropod      -> R.drawable.raptor
        CreatureType.Stegosaur          -> R.drawable.stego
        CreatureType.Spinosaur          -> R.drawable.spino
        else                            -> R.drawable.unkn
    }
}