package com.bp.dinodata.data.genus

import androidx.compose.runtime.Immutable
import com.bp.dinodata.data.CreatureType
import com.bp.dinodata.data.Diet
import com.bp.dinodata.data.MultiImageUrlData
import com.bp.dinodata.data.SingleImageUrlData
import com.bp.dinodata.data.quantities.IDescribesLength
import com.bp.dinodata.data.quantities.IDescribesWeight

interface IHasTaxonomy {
    fun getTaxonomy(): String
    fun getListOfTaxonomy(): List<String>
    fun getTaxonomyAsPrintableTree(): String
}

interface IHasMeasurements {
    fun getLength(): IDescribesLength?
    fun getWeight(): IDescribesWeight?
}

interface IHasName {
    fun getName(): String
}

interface IHasDiet {
    fun getDiet(): Diet?
}

interface IHasCreatureType {
    fun getCreatureType(): CreatureType
}

interface IHasNameInfo {
    fun getNameMeaning(): String?
    fun getNamePronunciation(): String?
}

interface IHasTimePeriodInfo {
    fun getTimePeriod(): String?
    fun getYearsLived(): String?
}

interface IDisplayInList: IHasName, IHasDiet, IHasCreatureType

interface IGenus: IHasTaxonomy, IHasMeasurements, IHasNameInfo, IDisplayInList, IHasTimePeriodInfo



@Immutable
data class Genus(
    private val name: String,
    private val diet: Diet? = null,
    private val type: CreatureType = CreatureType.Other,
    private val yearsLived: String? = null,
    private val timePeriod: String? = null,
    private val nameMeaning: String? = null,
    private val namePronunciation: String? = null,
    private val length: IDescribesLength?,
    private val weight: IDescribesWeight?,
    private val taxonomy: List<String>,
    private val imageUrlDataMap: Map<String, MultiImageUrlData>? = null
): IGenus {

    private val allImagesUrlData: List<SingleImageUrlData>?
        = imageUrlDataMap?.flatMap { it.value.getAllImageData() }

    override fun getTaxonomy(): String = taxonomy.joinToString("\n")
    override fun getListOfTaxonomy(): List<String> = taxonomy

    override fun getTimePeriod(): String? = timePeriod
    override fun getYearsLived(): String? = yearsLived

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


    override fun getName(): String = name
    override fun getCreatureType(): CreatureType = type
    override fun getDiet(): Diet? = diet
    override fun getLength(): IDescribesLength? = length
    override fun getWeight(): IDescribesWeight? = weight
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

    fun getNumDistinctImages(): Int = allImagesUrlData?.size ?: 0

}


