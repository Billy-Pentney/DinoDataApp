package com.bp.dinodata.data

data class Genus(
    val name: String,
    val diet: Diet? = null,
    val type: CreatureType = CreatureType.Other,
    val yearsLived: String? = null,
    val timePeriod: String? = null,
    val nameMeaning: String? = null,
    val namePronunciation: String? = null,
    val length: String?,
    val weight: String?,
) {
}


enum class CreatureType {
    Theropod_Large,
    Theropod_Small,
    Ceratopsian,
    Armoured,
    Sauropod,
    Hadrosaur,
    Avian,
    Aquatic,
    Cenezoic,
    Other
}
