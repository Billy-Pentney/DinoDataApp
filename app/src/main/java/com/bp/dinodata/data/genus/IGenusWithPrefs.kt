package com.bp.dinodata.data.genus

import com.bp.dinodata.data.CreatureType
import com.bp.dinodata.data.Diet
import com.bp.dinodata.data.TimePeriod
import com.bp.dinodata.data.quantities.IDescribesLength
import com.bp.dinodata.data.quantities.IDescribesWeight

interface IGenusWithPrefs: IGenus, IGenusPrefs

class GenusWithPrefs(
    val genus: IGenus,
    prefsIn: IGenusPrefs? = null
): IGenusWithPrefs {

    private val prefs = prefsIn ?: GenusPrefs()

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

    override fun isUserFavourite(): Boolean = prefs.isUserFavourite()
    override fun getSelectedColorName(): String? = prefs.getSelectedColorName()
}