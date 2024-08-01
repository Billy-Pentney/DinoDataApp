package com.bp.dinodata.data.genus

import com.bp.dinodata.data.CreatureType
import com.bp.dinodata.data.Diet
import com.bp.dinodata.data.TimePeriod
import com.bp.dinodata.data.quantities.IDescribesLength
import com.bp.dinodata.data.quantities.IDescribesWeight

interface IDetailedGenus: IGenusWithImages, ILocalPrefs {
    fun getLocalPrefs(): ILocalPrefs
}

class DetailedGenus(
    private val genus: IGenusWithImages,
    prefs: ILocalPrefs? = null
): IDetailedGenus {
    private val _prefs = prefs ?: LocalPrefs()
    override fun getLocalPrefs(): ILocalPrefs = _prefs

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

    override fun getImageUrl(index: Int): String? = genus.getImageUrl(index)
    override fun getThumbnailUrl(index: Int): String? = genus.getThumbnailUrl(index)
    override fun getNumDistinctImages(): Int = genus.getNumDistinctImages()

    override fun getSelectedColorName(): String? = _prefs.getSelectedColorName()
    override fun isUserFavourite(): Boolean = _prefs.isUserFavourite()
    override fun getPreferredImageIndex(): Int = _prefs.getPreferredImageIndex()
}