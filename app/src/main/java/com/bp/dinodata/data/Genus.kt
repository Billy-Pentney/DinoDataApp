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

    private val allImagesUrlData: List<SingleImageUrlData>?
        = imageUrlDataMap?.flatMap { it.value.getAllImageData() }

    val mainThumbnailUrl = getThumbnailUrl()
    val mainImageUrl = getImageUrl()

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

    fun getImageUrl(index: Int = 0): String? {
        val imageGroup = allImagesUrlData?.elementAtOrNull(index)
        return imageGroup?.getImageUrl(0)
    }

    fun getThumbnailUrl(index: Int = 0): String? {
        val imageGroup = allImagesUrlData?.elementAtOrNull(index)
        return imageGroup?.getThumbnailUrl(0)
    }

    fun getNumDistinctImages(): Int {
        return allImagesUrlData?.size ?: 0
    }
}


