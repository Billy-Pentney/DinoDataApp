package com.bp.dinodata.data

import android.util.Log

object DataParsing {
    fun matchCreatureType(text: String): CreatureType? {
        return when (text.lowercase().replace("_"," ")) {
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
            "other"             -> CreatureType.Other
            else                -> {
                Log.d("CreatureTypeParser", "Saw unfamiliar creature type $text")
                null
            }
        }
    }

    fun matchDiet(text: String): Diet? {
        val cleanText = text.trim().lowercase()
        return when {
            cleanText.startsWith("herbivor") -> Diet.Herbivore
            cleanText.startsWith("carnivor") -> Diet.Carnivore
            cleanText.startsWith("piscivor") -> Diet.Piscivore
            cleanText.startsWith("omnivor") -> Diet.Omnivore
            cleanText == "unknown" -> Diet.Unknown
            else -> {
                Log.d("DietParser", "Saw unfamiliar diet $text")
                null
            }
        }
    }

    fun matchTimePeriod(timePeriod: String): TimePeriod? {
        val splits = timePeriod.split(" ")
        var subepoch: Subepoch? = null
        var epoch: Epoch? = null

        for (split in splits) {
            when (split.lowercase()) {
                "cretaceous" -> { epoch = Epoch.Cretaceous }
                "jurassic" -> { epoch = Epoch.Jurassic }
                "triassic" -> { epoch = Epoch.Triassic }
                "early" -> { subepoch = Subepoch.Early }
                "middle" -> { subepoch = Subepoch.Middle }
                "late" -> { subepoch = Subepoch.Late }
            }
        }
        return epoch?.let { TimePeriod(it, subepoch) }
    }
}