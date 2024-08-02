package com.bp.dinodata.data.enum_readers

import android.util.Log
import com.bp.dinodata.data.CreatureType
import com.bp.dinodata.data.DataParsing
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
        "crocodilian" to CreatureType.Crocodilian,
        "iguanodont" to CreatureType.Iguanodontid,
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
        "spined_synapsid" to CreatureType.SpinedSynapsid,
        "serpent" to CreatureType.Serpent,
        "ichthyosaur" to CreatureType.Ichthyosaur,
        "mosasaur" to CreatureType.Mosasaur,
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
        val keysByCommonPrefix =
            DataParsing.getLongestPotentialSuffixes(text, CreatureTypesMap.keys)
        return keysByCommonPrefix.take(takeTop.coerceAtLeast(1))
    }

    override fun getListOfOptions(): List<String> {
        return CreatureTypesMap.keys.toList()
    }
}