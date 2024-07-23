package com.bp.dinodata.data.search

import android.util.Log
import com.bp.dinodata.data.CreatureTypeConverter
import com.bp.dinodata.data.DataParsing
import com.bp.dinodata.data.DietConverter
import com.bp.dinodata.data.TimePeriodConverter
import com.bp.dinodata.data.filters.CreatureTypeFilter
import com.bp.dinodata.data.filters.DietFilter
import com.bp.dinodata.data.filters.IFilter
import com.bp.dinodata.data.filters.LocationFilter
import com.bp.dinodata.data.filters.NameFilter
import com.bp.dinodata.data.filters.SelectedColorFilter
import com.bp.dinodata.data.filters.TaxonFilter
import com.bp.dinodata.data.filters.TimePeriodFilter
import com.bp.dinodata.data.genus.IGenus
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
    fun toOriginalText(): String
}


class GenusNameSearchTerm(
    private val query: String,
    private val isCapitalSensitive: Boolean = false,
    private val searchKeywords: List<String> = emptyList(),
): ISearchTerm<IHasName> {
    override fun getType(): SearchTermType = SearchTermType.Name
    override fun toFilter(): IFilter<IHasName> = NameFilter(query, isCapitalSensitive)

    override fun generateSearchSuggestions(): List<String> {
        return DataParsing.getLongestPotentialSuffixes(query, searchKeywords)
    }
    override fun toString(): String = query
    override fun toOriginalText(): String = query
}


abstract class ListBasedSearchTerm(
    private val originalText: String,
    private val termType: SearchTermType,
    private val allPossibleValues: List<String> = emptyList(),
): ISearchTerm<IGenus> {
    protected var queryArguments = listOf<String>()

    init {
        // Isolate the key from the values
        val splits = originalText.split(":")

        if (splits.size > 1) {
            val values = splits[1]
            // We may have multiple accepted values e.g. "KEY:a+b+c", so we split
            queryArguments = values.split("+")
        }
        else {
            Log.e("ListBasedSearchTerm", "No values found in text \'$originalText\'")
        }
    }

    override fun toString(): String {
        val type = getType().toString()
        return "$type: ${queryArguments.joinToString(" OR ")}"
    }

    override fun generateSearchSuggestions(): List<String> {
        if (queryArguments.isNotEmpty()) {
            val lastArgument = queryArguments.last()
            val unusedValues = allPossibleValues.minus(queryArguments.toSet())

            return if (lastArgument in allPossibleValues) {
                // Add an optional argument
                DataParsing.getLongestPotentialSuffixes("", unusedValues.map { "+$it" })
            } else {
                DataParsing.getLongestPotentialSuffixes(lastArgument, unusedValues)
            }
        }
        return emptyList()
    }
    override fun toOriginalText(): String = originalText
    override fun getType(): SearchTermType = termType
}

class CreatureTypeSearchTerm(
    originalText: String,
    possibleTypes: List<String> = CreatureTypeConverter.getListOfOptions()
): ListBasedSearchTerm(
    originalText,
    termType = SearchTermType.CreatureType,
    allPossibleValues = possibleTypes
) {
    override fun toFilter(): IFilter<in IHasCreatureType> {
        val creatureTypes = queryArguments.mapNotNull { CreatureTypeConverter.matchType(it) }
        return CreatureTypeFilter(creatureTypes)
    }
}

class DietSearchTerm(
    originalText: String,
    possibleDiets: List<String> = DietConverter.getListOfOptions()
): ListBasedSearchTerm(
    originalText,
    termType = SearchTermType.Diet,
    allPossibleValues = possibleDiets
) {
    override fun toFilter(): IFilter<in IHasDiet> {
        val diets = queryArguments.mapNotNull { DietConverter.matchType(it) }
        return DietFilter(diets)
    }
}

class TimePeriodSearchTerm(
    originalText: String,
    allPossibleValues: List<String> = TimePeriodConverter.getListOfOptions()
): ListBasedSearchTerm(
    originalText,
    allPossibleValues = allPossibleValues,
    termType = SearchTermType.TimePeriod,
) {
    override fun toFilter(): IFilter<in IHasTimePeriodInfo> {
        val periods = queryArguments.mapNotNull { TimePeriodConverter.matchType(it) }
        return TimePeriodFilter(periods)
    }
}

class TaxonNameSearchTerm(
    originalText: String,
    possibleTaxa: List<String> = emptyList()
): ListBasedSearchTerm(
    originalText = originalText,
    termType = SearchTermType.Taxon,
    allPossibleValues = possibleTaxa.map { it.lowercase() }
) {
    override fun toFilter(): IFilter<in IHasTaxonomy> = TaxonFilter(queryArguments)
}


class SelectedColorSearchTerm(
    originalText: String,
    possibleValues: List<String> = ThemeConverter.getListOfOptions()
): ListBasedSearchTerm(
    originalText = originalText,
    termType = SearchTermType.Color,
    allPossibleValues = possibleValues.map { it.lowercase() }
) {
    override fun toFilter(): IFilter<in IGenus> {
        return SelectedColorFilter(queryArguments.map { it.uppercase() })
    }
}

class LocationSearchTerm(
    originalText: String,
    allLocations: List<String> = emptyList()
): ListBasedSearchTerm(
    originalText = originalText,
    termType = SearchTermType.Location,
    allPossibleValues = allLocations.map { it.lowercase().replace(" ", "_") },
) {
    init {
        Log.d("LocationSearchTerm", "Got locations length ${queryArguments.size}")
    }

    override fun toFilter(): IFilter<in IGenus> {
        return LocationFilter(queryArguments)
    }
}

enum class SearchTermType {
    Name,
    CreatureType,
    Diet,
    TimePeriod,
    Taxon,
    Color,
    Location
}