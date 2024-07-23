package com.bp.dinodata.data

import android.util.Log
import com.bp.dinodata.data.CreatureTypeConverter.CreatureTypesMap
import com.bp.dinodata.data.DataParsing.getLongestPotentialSuffixes
import com.bp.dinodata.data.search.ISearchTypeConverter

object CreatureTypeConverter: ISearchTypeConverter<CreatureType> {

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
        "synapsid" to CreatureType.Synapsid,
        "other" to CreatureType.Other
    )

    override fun matchType(text: String): CreatureType? {
        val cleanText = text.lowercase().replace(" ","_")
        return if (cleanText in CreatureTypesMap.keys) {
            CreatureTypesMap[cleanText]
        }
        else {
            Log.d("CreatureTypeParser", "Saw unfamiliar creature type $text")
            null
        }
    }

    override fun suggestSearchSuffixes(text: String, takeTop: Int): List<String> {
        val keysByCommonPrefix = getLongestPotentialSuffixes(text, CreatureTypesMap.keys)
        return keysByCommonPrefix.take(takeTop.coerceAtLeast(1))
    }

    override fun getListOfOptions(): List<String> {
        return CreatureTypesMap.keys.toList()
    }
}

object TimePeriodConverter: ISearchTypeConverter<TimePeriod> {
    private val TimePeriodMap = mapOf(
        "cretaceous" to Epoch.Cretaceous,
        "jurassic" to Epoch.Jurassic,
        "triassic" to Epoch.Triassic
    )

    override fun matchType(text: String): TimePeriod? {
        val splits = text.split(" ")
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

    override fun suggestSearchSuffixes(text: String, takeTop: Int): List<String> {
        val keysByCommonPrefix = getLongestPotentialSuffixes(text, TimePeriodMap.keys)
        return keysByCommonPrefix.take(takeTop.coerceAtLeast(1))
    }

    override fun getListOfOptions(): List<String> {
        return TimePeriodMap.keys.toList()
    }
}


object DietConverter: ISearchTypeConverter<Diet> {

    private val DietTypesMap = mapOf(
        "carnivore" to Diet.Carnivore,
        "herbivore" to Diet.Herbivore,
        "omnivore" to Diet.Omnivore,
        "piscivore" to Diet.Piscivore
    )

    override fun matchType(text: String): Diet? {
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

    override fun suggestSearchSuffixes(text: String, takeTop: Int): List<String> {
        val keysByCommonPrefix = getLongestPotentialSuffixes(text, DietTypesMap.keys)
        return keysByCommonPrefix.take(takeTop.coerceAtLeast(1))
    }

    override fun getListOfOptions(): List<String> {
        return DietTypesMap.keys.toList()
    }
}



object DataParsing {
    fun getLongestPotentialSuffixes(matchText: String, targetStrings: Iterable<String>): List<String> {
        return targetStrings
            .filter { it.startsWith(matchText) }
            .sortedByDescending { it.length }
            .map { it.removePrefix(matchText) }
    }
}