package com.bp.dinodata.data.genus

import androidx.compose.runtime.Immutable
import com.bp.dinodata.data.CreatureType
import com.bp.dinodata.data.Diet
import com.bp.dinodata.data.quantities.IDescribesLength
import com.bp.dinodata.data.quantities.IDescribesWeight
import com.bp.dinodata.data.time_period.IDisplayableTimePeriod
import com.bp.dinodata.data.time_period.ITimeInterval

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
    fun getDiet(): Diet
}

interface IHasCreatureType {
    fun getCreatureType(): CreatureType
}

interface IHasSpeciesInfo {
    fun getSpeciesList(): List<ISpecies>
    fun hasSpeciesInfo(): Boolean
}

interface IAdditionalNameInfo {
    fun getNameMeaning(): String?
    fun getNamePronunciation(): String?
}

interface IHasTimePeriodInfo {
    fun getTimePeriod(): IDisplayableTimePeriod?
    fun getYearsLived(): String?
    fun getTimeIntervalLived(): ITimeInterval?
    fun getStartMya(): Float?
    fun getEndMya(): Float?
}

interface IHasLocationInfo {
    fun getLocations(): List<String>
}

interface IDisplayInList: IHasName, IHasDiet, IHasCreatureType

interface IGenus: IHasTaxonomy, IHasMeasurements, IAdditionalNameInfo,
    IDisplayInList, IHasTimePeriodInfo, IHasLocationInfo, IHasSpeciesInfo {
}

@Immutable
data class Genus(
    private val name: String,
    private val diet: Diet = Diet.Unknown,
    private val type: CreatureType = CreatureType.Other,
    private val timeInterval: ITimeInterval? = null,
    private val timePeriod: IDisplayableTimePeriod? = null,
    private val nameMeaning: String? = null,
    private val namePronunciation: String? = null,
    private val length: IDescribesLength? = null,
    private val weight: IDescribesWeight? = null,
    private val taxonomy: List<String> = emptyList(),
    private val locations: List<String> = emptyList(),
    private val species: List<ISpecies> = emptyList()
): IGenus {
    override fun getTaxonomy(): String = taxonomy.joinToString("\n")
    override fun getListOfTaxonomy(): List<String> = taxonomy

    override fun getTimePeriod(): IDisplayableTimePeriod? = timePeriod
    override fun getYearsLived(): String? = timeInterval?.toString()

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
    override fun getDiet(): Diet = diet
    override fun getLength(): IDescribesLength? = length
    override fun getWeight(): IDescribesWeight? = weight
    override fun getNameMeaning(): String? = nameMeaning?.let { "\'$it\'" }
    override fun getNamePronunciation(): String? = namePronunciation?.let { "\'$it\'" }

    override fun getStartMya(): Float? = timeInterval?.getStartTimeInMYA()
    override fun getEndMya(): Float? = timeInterval?.getEndTimeInMYA()
    override fun getTimeIntervalLived(): ITimeInterval? = timeInterval

    override fun getLocations(): List<String> = locations
    override fun getSpeciesList(): List<ISpecies> = species
    override fun hasSpeciesInfo(): Boolean = species.isNotEmpty()

}


