package com.bp.dinodata.data

import android.util.Log

object DataParsing {

    private val CreatureTypesMap = mapOf(
        "large_theropod" to CreatureType.LargeTheropod,
        "ceratopsian" to CreatureType.Ceratopsian,
        "ankylosaur" to CreatureType.Ankylosaur,
        "stegosaurid" to CreatureType.Stegosaur,
        "sauropod" to CreatureType.Sauropod,
        "ankylosaurid" to CreatureType.Ankylosaur,
        "spinosaurid" to CreatureType.Spinosaur,
        "small_theropod" to CreatureType.SmallTheropod,
        "ornithomimid" to CreatureType.Ornithomimid,
        "aquatic" to CreatureType.Aquatic,
        "pterosaur" to CreatureType.Pterosaur,
        "pachycephalosaur" to CreatureType.Pachycephalosaur,
        "cenezoic" to CreatureType.Cenezoic,
        "hadrosaur" to CreatureType.Hadrosaur,
        "synapsid" to CreatureType.Synapsid,
        "other" to CreatureType.Other
    )

    private val DietTypesMap = mapOf(
        "carnivore" to Diet.Carnivore,
        "herbivore" to Diet.Herbivore,
        "omnivore" to Diet.Omnivore,
        "piscivore" to Diet.Piscivore
    )

    fun matchCreatureType(text: String): CreatureType? {
        return when (text.lowercase().replace(" ","_")) {
            "large_theropod"        -> CreatureType.LargeTheropod
            "small_theropod"        -> CreatureType.SmallTheropod
            "ceratopsian"           -> CreatureType.Ceratopsian
            "ankylosaurid",
            "armoured dinosaur"     -> CreatureType.Ankylosaur
            "stegosaurid"           -> CreatureType.Stegosaur
            "pachycephalosaurid"    -> CreatureType.Pachycephalosaur
            "sauropod"              -> CreatureType.Sauropod
            "spinosaur"             -> CreatureType.Spinosaur
            "hadrosaur"             -> CreatureType.Hadrosaur
            "ornithomimid"          -> CreatureType.Ornithomimid
            "aquatic"               -> CreatureType.Aquatic
            "pterosaur"             -> CreatureType.Pterosaur
            "cenezoic"              -> CreatureType.Cenezoic
            "synapsid"              -> CreatureType.Synapsid
            "other"                 -> CreatureType.Other
            else                    -> {
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

    fun suggestCreatureTypeSuffixes(text: String, takeTop: Int = 3): List<String> {
        val keysByCommonPrefix = getLongestNonMatchingSuffixes(text, CreatureTypesMap.keys)
        return keysByCommonPrefix.take(takeTop.coerceAtLeast(1))
    }

    fun suggestDietSuffixes(text: String, takeTop: Int = 2): List<String> {
        val keysByCommonPrefix = getLongestNonMatchingSuffixes(text, DietTypesMap.keys)
        return keysByCommonPrefix.take(takeTop.coerceAtLeast(1))
    }

    private fun getLongestNonMatchingSuffixes(text: String, strings: Iterable<String>): List<String> {
        return strings.filter { it.startsWith(text) }
            .sortedByDescending { it.length }
            .map { it.removePrefix(text) }
    }
}