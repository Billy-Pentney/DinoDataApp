package com.bp.dinodata.data.search

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.room.util.query
import com.bp.dinodata.data.CreatureTypeConverter
import com.bp.dinodata.data.DataParsing
import com.bp.dinodata.data.DietConverter
import com.bp.dinodata.data.TimePeriodConverter
import com.bp.dinodata.data.filters.CreatureTypeFilter
import com.bp.dinodata.data.filters.DietFilter
import com.bp.dinodata.data.filters.FavouriteFilter
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

interface ISearchTerm<T>: IFilter<T> {
    fun getType(): SearchTermType
    fun generateSearchSuggestions(): List<String>
    fun toFilter(): IFilter<in T>
    fun toOriginalText(): String
    fun getIconId(): ImageVector?
}


class BasicSearchTerm(
    private val query: String,
    private val isCapitalSensitive: Boolean = false,
    private val searchKeywords: List<String> = emptyList(),
): ISearchTerm<IHasName> {
    private val filter = this.toFilter()

    override fun getType(): SearchTermType = SearchTermType.Name
    override fun toFilter(): IFilter<IHasName> = NameFilter(query, isCapitalSensitive)

    override fun generateSearchSuggestions(): List<String> {
        return DataParsing.getLongestPotentialSuffixes(query, searchKeywords)
    }
    override fun toString(): String = "Contains text: \"$query\""
    override fun toOriginalText(): String = query
    override fun getIconId(): ImageVector? {
        return null
    }

    override fun acceptsItem(item: IHasName): Boolean = filter.acceptsItem(item)
}


abstract class ListBasedSearchTerm(
    private val originalText: String,
    private val termType: SearchTermType,
    private val allPossibleValues: List<String> = emptyList(),
    private val imageIconVector: ImageVector? = null
): ISearchTerm<IGenus> {
    protected var queryArguments = listOf<String>()
    protected val filter = this.toFilter()

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
        val args = queryArguments.map { "\'$it\'" }
        return "$type: ${args.joinToString(" or ")}"
    }

    override fun generateSearchSuggestions(): List<String> {
        if (queryArguments.isNotEmpty()) {
            val lastArgument = queryArguments.last()
            val unusedValues = allPossibleValues.minus(queryArguments.toSet())

            return if (lastArgument in allPossibleValues) {
                // Encourages to add additional argument
                listOf("+")
            } else {
                DataParsing.getLongestPotentialSuffixes(lastArgument, unusedValues)
            }
        }
        return emptyList()
    }
    override fun toOriginalText(): String = originalText
    override fun getType(): SearchTermType = termType
    override fun getIconId(): ImageVector? = imageIconVector

    override fun acceptsItem(item: IGenus): Boolean = this.filter.acceptsItem(item)
}

class CreatureTypeSearchTerm(
    originalText: String,
    possibleTypes: List<String> = CreatureTypeConverter.getListOfOptions()
): ListBasedSearchTerm(
    originalText,
    termType = SearchTermType.CreatureType,
    allPossibleValues = possibleTypes,
    imageIconVector = Icons.Filled.LocalOffer
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
    allPossibleValues = possibleDiets,
    imageIconVector = Icons.Filled.Restaurant
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
    imageIconVector = Icons.Filled.AccessTime
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
    allPossibleValues = possibleTaxa.map { it.lowercase() },
    imageIconVector = Icons.Filled.AccountTree
) {
    override fun toFilter(): IFilter<in IHasTaxonomy> = TaxonFilter(queryArguments)
}


class SelectedColorSearchTerm(
    originalText: String,
    possibleValues: List<String> = ThemeConverter.getListOfOptions()
): ListBasedSearchTerm(
    originalText = originalText,
    termType = SearchTermType.Color,
    allPossibleValues = possibleValues.map { it.lowercase() },
    imageIconVector = Icons.Filled.ColorLens
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
    imageIconVector = Icons.Filled.LocationOn
) {
    init {
        Log.d("LocationSearchTerm", "Got locations length ${queryArguments.size}")
    }

    override fun toFilter(): IFilter<in IGenus> {
        return LocationFilter(queryArguments)
    }
}

class FavouriteSearchTerm(
    private val originalText: String
): ISearchTerm<IGenus> {
    private var queryValue: String = ""
    private var acceptFavourites: Boolean? = null

    init {
        // Isolate the key from the values
        val splits = originalText.split(":")

        if (splits.size > 1) {
            queryValue = splits[1]
            acceptFavourites = queryValue == "true"
        }
        else {
            Log.e("ListBasedSearchTerm", "No values found in text \'$originalText\'")
        }
    }

    override fun acceptsItem(item: IGenus): Boolean {
        return this.toFilter().acceptsItem(item)
    }

    override fun getType(): SearchTermType {
        return SearchTermType.Favourite
    }

    override fun generateSearchSuggestions(): List<String> {
        return DataParsing.getLongestPotentialSuffixes(
            queryValue, listOf("true", "false")
        )
    }

    override fun toFilter(): IFilter<in IGenus> {
        return FavouriteFilter(acceptFavourites)
    }

    override fun toOriginalText(): String = originalText
    override fun getIconId(): ImageVector = Icons.Filled.Star

    override fun toString(): String {
        return "IsFavourite: $acceptFavourites"
    }
}


enum class SearchTermType {
    Name,
    CreatureType,
    Diet,
    TimePeriod,
    Taxon,
    Color,
    Location,
    Favourite
}