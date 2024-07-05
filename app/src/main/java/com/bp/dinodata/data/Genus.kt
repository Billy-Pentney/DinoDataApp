package com.bp.dinodata.data

data class Genus(
    val name: String,
    val diet: Diet? = null,
    val type: CreatureType = CreatureType.Other,
    val yearsLived: String? = null,
    val timePeriod: String? = null,
    private val nameMeaning: String? = null,
    val namePronunciation: String? = null,
    private val length: String?,
    private val weight: String?,
    val taxonomy: List<String>
) {
    fun getTaxonomy(): String = taxonomy.joinToString("\n")

    fun getTaxonomicList(): List<String> = taxonomy

    fun getTaxonomicTree(): String {
        var tree = taxonomy[0]
        var indent = "└"
        for (taxon in taxonomy.drop(1)) {
            tree += "\n$indent $taxon"
            indent = "   $indent"
        }
        tree += "\n$indent $name"
        return tree
    }

    fun getLength(): String? = length?.let { "$it metres" }

    fun getWeight(): String? = weight

    fun getNameMeaning(): String? = nameMeaning?.let { "\'$it\'" }
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
