package com.bp.dinodata.data

import android.util.Log
import com.bp.dinodata.data.filters.CreatureTypeFilter
import com.bp.dinodata.data.filters.DietFilter
import com.bp.dinodata.data.filters.FilterBuilderImpl
import com.bp.dinodata.data.filters.IFilter
import com.bp.dinodata.data.filters.NameFilter
import com.bp.dinodata.data.filters.TaxonFilter
import com.bp.dinodata.data.filters.TimePeriodFilter
import com.bp.dinodata.data.genus.IGenus
import com.bp.dinodata.data.genus.IHasCreatureType
import com.bp.dinodata.data.genus.IHasDiet
import com.bp.dinodata.data.genus.IHasName
import com.bp.dinodata.data.genus.IHasTaxonomy
import com.bp.dinodata.data.genus.IHasTimePeriodInfo

interface IConvertToFilter<T> {
    fun toFilter(): IFilter<T>
}

interface ISearch<T>: IConvertToFilter<T> {
    fun getQuery(): String
    fun isQueryEmpty(): Boolean
    fun getSuggestedSuffixes(): List<String>
}

fun emptySearch(): ISearch<IGenus> = GenusSearch()

data class GenusSearch(
    private val query: String = "",
    private val terms: List<ISearchTerm<in IGenus>> = emptyList(),
    private val suggestedSuffixes: List<String> = emptyList()
): ISearch<IGenus> {
    companion object {
        fun constructFromQuery(
            query: String,
            capSensitive: Boolean=false
        ): GenusSearch {

            // Start by splitting the query at spaces
            val queryTerms = query.split(" ")

            val searchTerms = mutableListOf<ISearchTerm<in IGenus>>()

            for (i in queryTerms.indices) {
                val term = queryTerms[i]

                // Attempt to split this term into a prefix and its target, e.g. "diet:carnivore"
                val splits = term.split(":")
                if (splits.size != 2) {
                    // If not possible or invalid number of splits, treat it as a name
                    searchTerms.add(GenusNameSearchTerm(term, capSensitive))
                }
                else {
                    // If here, then we know we have a term of the form "X:Y"
                    val key = splits[0]
                    val value = splits[1]

                    // We may have multiple accepted values e.g. "X:a+b+c", so we split
                    val values = value.split("+")

                    when (key) {
                        "taxon" -> { searchTerms.add(TaxonNameSearchTerm(value)) }
                        "type" -> {
                            searchTerms.add(CreatureTypeSearchTerm(values))
                        }
                        "diet" -> {
                            searchTerms.add(DietSearchTerm(values))
                        }
                        "period" -> {
                            searchTerms.add(TimePeriodSearchTerm(values))
                        }
                        else -> {
                            Log.d("SearchConstructor", "Unknown query filter \'$term\'")
                        }
                    }
                }
            }

            val suggestions = searchTerms.lastOrNull()
                ?.generateSearchSuggestions()

            return GenusSearch(
                query,
                searchTerms.toList(),
                suggestions ?: emptyList()
            )
        }
    }

    override fun getQuery(): String = query

    override fun isQueryEmpty(): Boolean = query.isEmpty()
    override fun getSuggestedSuffixes(): List<String> = suggestedSuffixes

    override fun toFilter(): IFilter<IGenus> {
        val filters = terms.map { it.toFilter() }
        return FilterBuilderImpl(filters.toMutableList()).build()
    }
}

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

class TaxonNameSearchTerm(
    private val taxonName: String
): ISearchTerm<IHasTaxonomy> {
    override fun getType(): SearchTermType = SearchTermType.Taxon
    override fun toFilter(): IFilter<in IHasTaxonomy> = TaxonFilter(taxonName)
    override fun generateSearchSuggestions(): List<String> = emptyList()
}

class CreatureTypeSearchTerm(
    private val queryArguments: List<String>
): ISearchTerm<IHasCreatureType> {
    private val creatureTypes = queryArguments.mapNotNull { DataParsing.matchCreatureType(it) }
    override fun getType(): SearchTermType = SearchTermType.CreatureType
    override fun toFilter(): IFilter<in IHasCreatureType> = CreatureTypeFilter(creatureTypes)
    override fun generateSearchSuggestions(): List<String> {
        if (queryArguments.isNotEmpty()) {
            val lastArgument = queryArguments.last()
            return DataParsing.suggestCreatureTypeSuffixes(lastArgument)
        }
        return emptyList()
    }
}

class DietSearchTerm(
    private val queryArguments: List<String>
): ISearchTerm<IHasDiet> {
    private val diets = queryArguments.mapNotNull { DataParsing.matchDiet(it) }
    override fun getType(): SearchTermType = SearchTermType.Diet
    override fun toFilter(): IFilter<in IHasDiet> = DietFilter(diets)
    override fun generateSearchSuggestions(): List<String> {
        if (queryArguments.isNotEmpty()) {
            val lastArgument = queryArguments.last()
            return DataParsing.suggestDietSuffixes(lastArgument)
        }
        return emptyList()
    }
}

class TimePeriodSearchTerm(
    private val queryArguments: List<String>
): ISearchTerm<IHasTimePeriodInfo> {
    private val periods = queryArguments.mapNotNull { DataParsing.matchTimePeriod(it) }
    override fun getType(): SearchTermType = SearchTermType.TimePeriod
    override fun toFilter(): IFilter<in IHasTimePeriodInfo> = TimePeriodFilter(periods)
    override fun generateSearchSuggestions(): List<String> {
        if (queryArguments.isNotEmpty()) {
            return DataParsing.suggestTimePeriodSuffixes(queryArguments.last())
        }
        return emptyList()
    }
}

enum class SearchTermType {
    Name,
    CreatureType,
    Diet,
    TimePeriod,
    Taxon
}
