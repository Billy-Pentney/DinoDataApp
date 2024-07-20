package com.bp.dinodata.data.search

import com.bp.dinodata.data.CreatureType
import com.bp.dinodata.data.CreatureTypeConverter
import com.bp.dinodata.data.DataParsing
import com.bp.dinodata.data.DietConverter
import com.bp.dinodata.data.TimePeriodConverter
import com.bp.dinodata.data.filters.CreatureTypeFilter
import com.bp.dinodata.data.filters.DietFilter
import com.bp.dinodata.data.filters.IFilter
import com.bp.dinodata.data.filters.NameFilter
import com.bp.dinodata.data.filters.SelectedColorFilter
import com.bp.dinodata.data.filters.TaxonFilter
import com.bp.dinodata.data.filters.TimePeriodFilter
import com.bp.dinodata.data.genus.IGenus
import com.bp.dinodata.data.genus.IHasColor
import com.bp.dinodata.data.genus.IHasCreatureType
import com.bp.dinodata.data.genus.IHasDiet
import com.bp.dinodata.data.genus.IHasName
import com.bp.dinodata.data.genus.IHasTaxonomy
import com.bp.dinodata.data.genus.IHasTimePeriodInfo
import com.bp.dinodata.presentation.utils.ThemeConverter

interface ISearchTerm<T> {
    fun getType(): SearchTermType
    fun generateSearchSuggestions(): List<String>
    fun toFilter(): IFilter<in T>
}

class GenusNameSearchTerm(
    private val query: String,
    private val isCapitalSensitive: Boolean = false
): ISearchTerm<IHasName> {
    override fun getType(): SearchTermType = SearchTermType.Name
    override fun toFilter(): IFilter<IHasName> {
        return NameFilter(query, isCapitalSensitive)
    }

    override fun generateSearchSuggestions(): List<String> = emptyList()
}


abstract class ConversionBasedSearchTerm<T>(
    private val queryArguments: List<String>,
    private val converter: ISearchTypeConverter<T>,
    private val type: SearchTermType,
    private val filterGenerator: (List<T>) -> IFilter<in T>
): ISearchTerm<T> {
    private val typedItems: List<T> = queryArguments.mapNotNull { converter.matchType(it) }

    override fun getType(): SearchTermType = SearchTermType.Taxon
    override fun toFilter(): IFilter<in T> = filterGenerator(typedItems)
    override fun generateSearchSuggestions(): List<String> {
        val lastArg = queryArguments.lastOrNull()
        if (lastArg != null) {
            return converter.suggestSearchSuffixes(lastArg)
        }
        return emptyList()
    }
}


class TaxonNameSearchTerm(
    private val taxonName: String
): ISearchTerm<IHasTaxonomy> {
    override fun getType(): SearchTermType = SearchTermType.Taxon
    override fun toFilter(): IFilter<in IHasTaxonomy> = TaxonFilter(taxonName)
    override fun generateSearchSuggestions(): List<String> = emptyList()
}

//class CreatureTypeSearchTermTwo(
//    queryArguments: List<String>
//): ConversionBasedSearchTerm<CreatureType>(
//    queryArguments,
//    CreatureTypeConverter,
//    SearchTermType.CreatureType,
//    { CreatureTypeFilter(it) }
//)

class CreatureTypeSearchTerm(
    private val queryArguments: List<String>
): ISearchTerm<IHasCreatureType> {
    private val creatureTypes = queryArguments.mapNotNull { CreatureTypeConverter.matchType(it) }
    override fun getType(): SearchTermType = SearchTermType.CreatureType
    override fun toFilter(): IFilter<in IHasCreatureType> = CreatureTypeFilter(creatureTypes)
    override fun generateSearchSuggestions(): List<String> {
        if (queryArguments.isNotEmpty()) {
            val lastArgument = queryArguments.last()
            return CreatureTypeConverter.suggestSearchSuffixes(lastArgument)
        }
        return emptyList()
    }
}

class DietSearchTerm(
    private val queryArguments: List<String>
): ISearchTerm<IHasDiet> {
    private val diets = queryArguments.mapNotNull { DietConverter.matchType(it) }
    override fun getType(): SearchTermType = SearchTermType.Diet
    override fun toFilter(): IFilter<in IHasDiet> = DietFilter(diets)
    override fun generateSearchSuggestions(): List<String> {
        if (queryArguments.isNotEmpty()) {
            val lastArgument = queryArguments.last()
            return DietConverter.suggestSearchSuffixes(lastArgument)
        }
        return emptyList()
    }
}

class TimePeriodSearchTerm(
    private val queryArguments: List<String>
): ISearchTerm<IHasTimePeriodInfo> {
    private val periods = queryArguments.mapNotNull { TimePeriodConverter.matchType(it) }
    override fun getType(): SearchTermType = SearchTermType.TimePeriod
    override fun toFilter(): IFilter<in IHasTimePeriodInfo> = TimePeriodFilter(periods)
    override fun generateSearchSuggestions(): List<String> {
        if (queryArguments.isNotEmpty()) {
            return TimePeriodConverter.suggestSearchSuffixes(queryArguments.last())
        }
        return emptyList()
    }
}

class SelectedColorSearchTerm(
    private val colorNames: List<String?>
): ISearchTerm<IGenus> {
    private val colorNamesUpper = colorNames.map { it?.uppercase() }

    override fun getType(): SearchTermType = SearchTermType.Color
    override fun generateSearchSuggestions(): List<String> {
        val last = colorNamesUpper.lastOrNull()
        if (last != null) {
            return ThemeConverter.suggestSearchSuffixes(last).map { it.lowercase() }
        }
        return emptyList()
    }

    override fun toFilter(): IFilter<in IGenus> {
        return SelectedColorFilter(colorNames.map { it?.uppercase() })
    }
}

enum class SearchTermType {
    Name,
    CreatureType,
    Diet,
    TimePeriod,
    Taxon,
    Color
}