package com.bp.dinodata.data

import android.util.Log

object DataParsing {

    private val CreatureTypesMap = mapOf(
        "abelisaurid" to CreatureType.Abelisaurid,
        "ankylosaur" to CreatureType.Ankylosaur,
        "ankylosaurid" to CreatureType.Ankylosaur,
        "aquatic" to CreatureType.Aquatic,
        "ceratopsian" to CreatureType.Ceratopsian,
        "cenezoic" to CreatureType.Cenezoic,
        "carcharodontosaurid" to CreatureType.Carcharodontosaurid,
        "dromaeosaurid" to CreatureType.Dromaeosaurid,
        "euornithopod" to CreatureType.Euornithopod,
        "hadrosaur" to CreatureType.Hadrosaur,
        "pachycephalosaurid" to CreatureType.Pachycephalosaur,
        "pterosaur" to CreatureType.Pterosaur,
        "sauropod" to CreatureType.Sauropod,
        "spinosaurid" to CreatureType.Spinosaur,
        "stegosaurid" to CreatureType.Stegosaur,
        "tyrannosaurid" to CreatureType.Tyrannosaurid,
        "small_theropod" to CreatureType.SmallTheropod,
        "medium_theropod" to CreatureType.MediumTheropod,
        "large_theropod" to CreatureType.LargeTheropod,
        "ornithomimid" to CreatureType.Ornithomimid,
        "plesiosaur" to CreatureType.Plesiosaur,
        "therizinosaurid" to CreatureType.Therizinosaurid,
        "other" to CreatureType.Other
    )

    private val DietTypesMap = mapOf(
        "carnivore" to Diet.Carnivore,
        "herbivore" to Diet.Herbivore,
        "omnivore" to Diet.Omnivore,
        "piscivore" to Diet.Piscivore
    )

    private val TimePeriodMap = mapOf(
        "cretaceous" to Epoch.Cretaceous,
        "jurassic" to Epoch.Jurassic,
        "triassic" to Epoch.Triassic
    )

    fun matchCreatureType(text: String): CreatureType? {
        val cleanText = text.lowercase().replace(" ","_")
        return if (cleanText in CreatureTypesMap.keys) {
            CreatureTypesMap[cleanText]
        }
        else {
            Log.d("CreatureTypeParser", "Saw unfamiliar creature type $text")
            null
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

    fun suggestTimePeriodSuffixes(text: String, takeTop: Int = 2): List<String> {
        val keysByCommonPrefix = getLongestNonMatchingSuffixes(text, TimePeriodMap.keys)
        return keysByCommonPrefix.take(takeTop.coerceAtLeast(1))
    }

    private fun getLongestNonMatchingSuffixes(text: String, strings: Iterable<String>): List<String> {
        return strings.filter { it.startsWith(text) }
            .sortedByDescending { it.length }
            .map { it.removePrefix(text) }
    }
}