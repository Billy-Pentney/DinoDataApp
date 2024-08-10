package com.bp.dinodata.data.genus

import com.bp.dinodata.data.CreatureType
import com.bp.dinodata.data.Diet
import com.bp.dinodata.data.quantities.IDescribesLength
import com.bp.dinodata.data.quantities.IDescribesWeight
import com.bp.dinodata.data.time_period.IDisplayableTimePeriod
import com.bp.dinodata.data.time_period.intervals.ITimeInterval

interface IGenusWithPrefs: IGenus, ILocalPrefs

class GenusWithPrefs(
    val genus: IGenus,
    prefsIn: ILocalPrefs? = null
): IGenusWithPrefs {

    private val prefs = prefsIn ?: LocalPrefs()

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

    override fun getTimePeriod(): IDisplayableTimePeriod? = genus.getTimePeriod()
    override fun getTimePeriods(): List<IDisplayableTimePeriod> = genus.getTimePeriods()
    override fun getYearsLived(): String? = genus.getYearsLived()
    override fun getStartMya(): Float? = genus.getStartMya()
    override fun getEndMya(): Float? = genus.getEndMya()
    override fun getTimeIntervalLived(): ITimeInterval? = genus.getTimeIntervalLived()

    override fun getLocations(): List<String> = genus.getLocations()
    override fun getSpeciesList(): List<ISpecies> = genus.getSpeciesList()
    override fun hasSpeciesInfo(): Boolean = genus.hasSpeciesInfo()

    override fun isUserFavourite(): Boolean = prefs.isUserFavourite()
    override fun getPreferredImageIndex(): Int = prefs.getPreferredImageIndex()

    override fun getSelectedColorName(): String? = prefs.getSelectedColorName()
}