package com.bp.dinodata.data.genus

import com.bp.dinodata.data.CreatureType
import com.bp.dinodata.data.Diet
import com.bp.dinodata.data.IFormation
import com.bp.dinodata.data.ImageUrlData
import com.bp.dinodata.data.quantities.IDescribesLength
import com.bp.dinodata.data.quantities.IDescribesMass
import com.bp.dinodata.data.quantities.IQuantity
import com.bp.dinodata.data.taxon.ITaxon
import com.bp.dinodata.data.time_period.IDisplayableTimePeriod
import com.bp.dinodata.data.time_period.intervals.ITimeInterval

interface IHasListOfFormations {
    fun getFormations(): List<IFormation>
}

interface IDetailedGenus: IGenusWithImages, ILocalPrefs, IHasCurrentSelectedImage, IHasListOfFormations {
    fun getLocalPrefs(): ILocalPrefs
}

/**
 * Describes a genus, along with its locally-stored preferences information.
 */
class DetailedGenus(
    private val genus: IGenusWithImages,
    prefs: ILocalPrefs? = null,
    private val formations: List<IFormation> = emptyList()
): IDetailedGenus {
    private val _prefs = prefs ?: LocalPrefs()
    override fun getLocalPrefs(): ILocalPrefs = _prefs

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

    override fun getLocations(): List<String> = genus.getLocations()
    override fun getFormationNames(): List<String> = genus.getFormationNames()
    override fun getFormations(): List<IFormation> = formations

    override fun getSpeciesList(): List<ISpecies> = genus.getSpeciesList()
    override fun hasSpeciesInfo(): Boolean = genus.hasSpeciesInfo()

    override fun getTimePeriod(): IDisplayableTimePeriod? = genus.getTimePeriod()
    override fun getTimePeriods(): List<IDisplayableTimePeriod> = genus.getTimePeriods()
    override fun getYearsLived(): String? = genus.getYearsLived()
    override fun getStartMya(): Float? = genus.getStartMya()
    override fun getEndMya(): Float? = genus.getEndMya()
    override fun getTimeIntervalLived(): ITimeInterval? = genus.getTimeIntervalLived()

    override fun getImageUrl(index: Int): String? = genus.getImageUrl(index)
    override fun getThumbnailUrl(index: Int): String? = genus.getThumbnailUrl(index)
    override fun getNumDistinctImages(): Int = genus.getNumDistinctImages()

    override fun getImageData(index: Int): ImageUrlData? = genus.getImageData(index)
    override fun getCurrentImageData(): ImageUrlData? {
        val currentImageIndex = _prefs.getPreferredImageIndex()
        val imageData = genus.getImageData(currentImageIndex)
        return imageData
    }

    override fun getSelectedColorName(): String? = _prefs.getSelectedColorName()
    override fun isUserFavourite(): Boolean = _prefs.isUserFavourite()
    override fun getPreferredImageIndex(): Int = _prefs.getPreferredImageIndex()

    override fun getChildrenTaxa(): List<ITaxon> = genus.getChildrenTaxa()
    override fun containsText(searchText: String): Boolean = genus.containsText(searchText)

    override fun getParentTaxonName(): String? = genus.getParentTaxonName()
}