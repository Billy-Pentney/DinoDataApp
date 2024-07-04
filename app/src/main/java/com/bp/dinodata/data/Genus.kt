package com.bp.dinodata.data

data class Genus(
    val name: String,
    val diet: Diet? = null,
    val type: CreatureType = CreatureType.Other,
    val yearsLived: String? = null,
    val timePeriod: String? = null,
    val nameMeaning: String? = null,
    val namePronunciation: String? = null,
    private val length: String?,
    private val weight: String?,
    val taxonomy: List<String>
) {
    fun getTaxonomy(): String {
        return taxonomy.joinToString(", ")
    }

    fun getLength(): String? {
        return length?.let { "$it metres" }
    }

    fun getWeight(): String? {
        return weight
    }
}


enum class CreatureType {
    LargeTheropod,
    SmallTheropod,
    Ceratopsian,
    Armoured,
    Stegosaur,
    Sauropod,
    Hadrosaur,
    Avian,
    Aquatic,
    Cenezoic,
    Other,
    Spinosaur
}
