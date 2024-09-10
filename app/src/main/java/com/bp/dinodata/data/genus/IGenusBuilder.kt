package com.bp.dinodata.data.genus

import com.bp.dinodata.data.CreatureType
import com.bp.dinodata.data.Diet
import com.bp.dinodata.data.IBuilder
import com.bp.dinodata.data.IDictParser


interface IGenusBuilder: IDictParser<IGenus>, IBuilder<IGenus> {
    fun setDiet(dietStr: String?): IGenusBuilder
    fun setDiet(diet: Diet): IGenusBuilder
    fun setYearsLived(timeRange: String?): IGenusBuilder
    fun setTimePeriod(period: String?): IGenusBuilder
    fun setNamePronunciation(pronunciation: String?): IGenusBuilder
    fun setNameMeaning(meaning: String?): IGenusBuilder
    fun setLength(length: String?): IGenusBuilder
    fun setWeight(weight: String?): IGenusBuilder
    fun setCreatureType(type: CreatureType): IGenusBuilder
    fun setCreatureType(type: String?): IGenusBuilder
    fun splitTimePeriodAndYears(periodAndYears: String?): IGenusBuilder
    fun setTaxonomy(taxonomy: Any): IGenusBuilder
    fun setStartMya(startMya: String?): IGenusBuilder
    fun setEndMya(endMya: String?): IGenusBuilder

    fun setLocations(locations: List<String>): IGenusBuilder
    fun setSpecies(speciesData: Any): IGenusBuilder

    fun setTimePeriods(periodStrings: List<String>): IGenusBuilder

    fun setFormations(formations: List<String>): IGenusBuilder
}






