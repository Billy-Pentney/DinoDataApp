package com.bp.dinodata.data.genus

import com.bp.dinodata.data.CreatureType
import com.bp.dinodata.data.Diet
import com.bp.dinodata.data.MultiImageUrlData
import com.bp.dinodata.data.SingleImageUrlData
import com.bp.dinodata.data.TimePeriod
import com.bp.dinodata.data.quantities.IDescribesLength
import com.bp.dinodata.data.quantities.IDescribesWeight

interface IHasImageData {
    fun getImageUrl(index: Int = 0): String?
    fun getThumbnailUrl(index: Int = 0): String?
    fun getNumDistinctImages(): Int
}

interface IGenusWithImages: IGenus, IHasImageData

class GenusWithImages(
    private val genus: IGenus,
    private val imageUrlDataMap: Map<String, MultiImageUrlData>? = null
): IGenusWithImages {
    private val allImagesUrlData: List<SingleImageUrlData>?
            = imageUrlDataMap?.flatMap { it.value.getAllImageData() }

    override fun getTaxonomy(): String = genus.getTaxonomy()
    override fun getListOfTaxonomy(): List<String> = genus.getListOfTaxonomy()
    override fun getTaxonomyAsPrintableTree(): String = genus.getTaxonomyAsPrintableTree()
    override fun getLength(): IDescribesLength? = genus.getLength()
    override fun getWeight(): IDescribesWeight? = genus.getWeight()
    override fun getNameMeaning(): String? = genus.getNameMeaning()
    override fun getNamePronunciation(): String? = genus.getNamePronunciation()
    override fun getName(): String = genus.getName()
    override fun getDiet(): Diet = genus.getDiet()
    override fun getCreatureType(): CreatureType = genus.getCreatureType()
    override fun getTimePeriod(): TimePeriod? = genus.getTimePeriod()
    override fun getYearsLived(): String? = genus.getYearsLived()
    override fun getLocations(): List<String> = genus.getLocations()
    override fun getSpeciesList(): List<ISpecies> = genus.getSpeciesList()
    override fun hasSpeciesInfo(): Boolean = genus.hasSpeciesInfo()

    override fun getImageUrl(index: Int): String? {
        val imageGroup = allImagesUrlData?.elementAtOrNull(index)
        return imageGroup?.getImageUrl(0)
    }

    override fun getThumbnailUrl(index: Int): String? {
        val imageGroup = allImagesUrlData?.elementAtOrNull(index)
        return imageGroup?.getThumbnailUrl(0)
    }

    override fun getNumDistinctImages(): Int = allImagesUrlData?.size ?: 0
}