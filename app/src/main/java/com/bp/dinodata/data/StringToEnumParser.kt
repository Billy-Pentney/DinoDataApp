package com.bp.dinodata.data

import android.util.Log

object DataParsing {
    fun matchCreatureType(text: String): CreatureType {
        return when (text.lowercase()) {
            "large theropod"    -> CreatureType.LargeTheropod
            "small theropod"    -> CreatureType.SmallTheropod
            "ceratopsian"       -> CreatureType.Ceratopsian
            "ankylosaurid",
            "armoured dinosaur" -> CreatureType.Ankylosaur
            "stegosaur"         -> CreatureType.Stegosaur
            "pachycephalosaurid"  -> CreatureType.Pachycephalosaur
            "sauropod"          -> CreatureType.Sauropod
            "spinosaur"         -> CreatureType.Spinosaur
            "hadrosaur"         -> CreatureType.Hadrosaur
            "ornithomimid"      -> CreatureType.Ornithomimid
            "aquatic"           -> CreatureType.Aquatic
            "pterosaur"         -> CreatureType.Pterosaur
            "cenezoic"          -> CreatureType.Cenezoic
            "synapsid"          -> CreatureType.Synapsid
            else                -> CreatureType.Other
        }
    }

    fun matchDiet(text: String): Diet {
        return when(text.trim().lowercase()) {
            "herbivorous",
            "herbivore"
                -> Diet.Herbivore
            "carnivorous",
            "carnivore"
                -> Diet.Carnivore
            "piscivorous",
            "piscivore"
                -> Diet.Piscivore
            "omnivorous",
            "omnivore"
                -> Diet.Omnivore
            else -> {
                Log.d("GenusBuilder", "Saw unknown diet value \'${text}\'")
                Diet.Unknown
            }
        }
    }
}