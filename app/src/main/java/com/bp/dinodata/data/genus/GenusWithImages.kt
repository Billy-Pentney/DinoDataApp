package com.bp.dinodata.data.genus

import com.bp.dinodata.data.CreatureType
import com.bp.dinodata.data.Diet
import com.bp.dinodata.data.ImageUrlData
import com.bp.dinodata.data.MultiImageUrlData
import com.bp.dinodata.data.SingleImageUrlData
import com.bp.dinodata.data.quantities.IDescribesLength
import com.bp.dinodata.data.quantities.IDescribesMass
import com.bp.dinodata.data.quantities.IQuantity
import com.bp.dinodata.data.taxon.ITaxon
import com.bp.dinodata.data.time_period.IDisplayableTimePeriod
import com.bp.dinodata.data.time_period.intervals.ITimeInterval

interface IHasImageData {
    fun getImageUrl(index: Int = 0): String?
    fun getThumbnailUrl(index: Int = 0): String?
    fun getNumDistinctImages(): Int
    fun getImageData(index: Int): ImageUrlData?
}

interface IHasCurrentSelectedImage {
    fun getCurrentImageData(): ImageUrlData?
}

interface IGenusWithImages: IGenus, IHasImageData

class GenusWithImages(
    private val genus: IGenus,
    imageUrlDataMap: Map<String, MultiImageUrlData>? = null
): IGenusWithImages {
    private val allImagesUrlData: List<SingleImageUrlData>?
            = imageUrlDataMap?.flatMap { it.value.getAllImageData() }

    override fun getTaxonomy(): String = genus.getTaxonomy()
    override fun getListOfTaxonomy(): List<String> = genus.getListOfTaxonomy()
    override fun getTaxonomyAsPrintableTree(): String = genus.getTaxonomyAsPrintableTree()
    override fun getLength(): IDescribesLength? = genus.getLength()
    override fun getWeight(): IDescribesMass? = genus.getWeight()
    override fun getAllMeasurements(): List<IQuantity> = genus.getAllMeasurements()

    override fun getNameMeaning(): String? = genus.getNameMeaning()
    override fun getNamePronunciation(): String? = genus.getNamePronunciation()
    override fun getName(): String = genus.getName()
    override fun getDiet(): Diet = genus.getDiet()
    override fun getCreatureType(): CreatureType = genus.getCreatureType()

    override fun getTimePeriod(): IDisplayableTimePeriod? = genus.getTimePeriod()
    override fun getTimePeriods(): List<IDisplayableTimePeriod> = genus.getTimePeriods()
    override fun getYearsLived(): String? = genus.getYearsLived()
    override fun getTimeIntervalLived(): ITimeInterval? = genus.getTimeIntervalLived()

    override fun getStartMya(): Float? = genus.getStartMya()
    override fun getEndMya(): Float? = genus.getEndMya()

    override fun getLocations(): List<String> = genus.getLocations()
    override fun getFormationNames(): List<String> = genus.getFormationNames()

    override fun getSpeciesList(): List<ISpecies> = genus.getSpeciesList()
    override fun hasSpeciesInfo(): Boolean = genus.hasSpeciesInfo()

    override fun getImageUrl(index: Int): String? {
        val imageGroup = allImagesUrlData?.elementAtOrNull(index)
        return imageGroup?.getSmallestImageUrl(0)
    }

    override fun getThumbnailUrl(index: Int): String? {
        val imageGroup = allImagesUrlData?.elementAtOrNull(index)
        return imageGroup?.getSmallestThumbnailUrl(0)
    }

    override fun getImageData(index: Int): ImageUrlData? {
        return allImagesUrlData?.elementAtOrNull(index)
    }

    override fun getNumDistinctImages(): Int = allImagesUrlData?.size ?: 0

    override fun getChildrenTaxa(): List<ITaxon> = genus.getChildrenTaxa()

    override fun containsText(searchText: String): Boolean = genus.containsText(searchText)

    override fun getParentTaxonName(): String? = genus.getParentTaxonName()
}