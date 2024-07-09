package com.bp.dinodata.data

import com.bp.dinodata.data.quantities.IDescribesLength
import com.bp.dinodata.data.quantities.IDescribesWeight

interface IGenus {
    fun getTaxonomy(): String
    fun getListOfTaxonomy(): List<String>
    fun getTaxonomyAsPrintableTree(): String

    fun getLength(): String?
    fun getWeight(): String?

    fun getNameMeaning(): String?
    fun getNamePronunciation(): String?
}


data class Genus(
    val name: String,
    val diet: Diet? = null,
    val type: CreatureType = CreatureType.Other,
    val yearsLived: String? = null,
    val timePeriod: String? = null,
    private val nameMeaning: String? = null,
    private val namePronunciation: String? = null,
    private val length: IDescribesLength?,
    private val weight: IDescribesWeight?,
    private val taxonomy: List<String>,
    private val imageUrlDataMap: Map<String, MultiImageUrlData>? = null
): IGenus {
    override fun getTaxonomy(): String = taxonomy.joinToString("\n")

    override fun getListOfTaxonomy(): List<String> = taxonomy

    override fun getTaxonomyAsPrintableTree(): String {
        var tree = taxonomy[0]
        var indent = "└"
        for (taxon in taxonomy.drop(1)) {
            tree += "\n$indent $taxon"
            indent = "   $indent"
        }
        tree += "\n$indent $name"
        return tree
    }

    override fun getLength(): String? = length?.toString()
    override fun getWeight(): String? = weight?.toString()
    override fun getNameMeaning(): String? = nameMeaning?.let { "\'$it\'" }
    override fun getNamePronunciation(): String? = namePronunciation?.let { "\'$it\'" }

    fun getImageUrl(): String? {
        val firstSpeciesData = this.imageUrlDataMap?.values?.firstOrNull()
        val firstImageData = firstSpeciesData?.getFirstUrlData()
        val firstImageUrl = firstImageData?.getImageUrl(0)
        return firstImageUrl
    }

    fun getThumbnailUrl(): String? {
        val firstSpeciesData = this.imageUrlDataMap?.values?.firstOrNull()
        val firstImageData = firstSpeciesData?.getFirstUrlData()
        val firstImageUrl = firstImageData?.getThumbnailUrl(0)
        return firstImageUrl
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
