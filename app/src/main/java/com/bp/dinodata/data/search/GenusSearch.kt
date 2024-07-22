package com.bp.dinodata.data.search

import android.util.Log
import com.bp.dinodata.data.filters.FilterBuilderImpl
import com.bp.dinodata.data.filters.IFilter
import com.bp.dinodata.data.genus.IGenus

/**
 *
 * @param locations A list of all unique locations across all genus data; this list is used
 * to generate suggested suffixes if the search filters by location.
 * @param taxa A list of all unique taxa found across the genus data; this list is used
 * to generate suggested suffixes if the search filters by taxon.
 */
data class GenusSearch(
    private var query: String = "",
    private var capSensitive: Boolean = false,
    private val locations: List<String> = emptyList(),
    private val taxa: List<String> = emptyList()
): ISearch<IGenus> {
    private var terms: List<ISearchTerm<in IGenus>>
        = parseSearchTerms(query, capSensitive)

    companion object {
        private const val TAXON_PREFIX = "taxon"
        private const val TYPE_PREFIX = "type"
        private const val PERIOD_PREFIX = "period"
        private const val COLOR_PREFIX = "color"
        private const val LOCATION_PREFIX = "location"
        private const val DIET_PREFIX = "diet"

        private val PREFIX_LIST = listOf(
            TAXON_PREFIX, TYPE_PREFIX, PERIOD_PREFIX, COLOR_PREFIX, LOCATION_PREFIX, DIET_PREFIX
        )

        private val PREFIX_COLON_LIST = PREFIX_LIST.map { "$it:" }
    }

    /**
     * Build a search from the raw query text as received from the UI.
     * @param query The raw string which is visible via the Search Bar.
     * @param capSensitive If true then this search will compare strings with respect to capitalisation;
     * otherwise, capitalisation will be ignored (false by default).
     */
    private fun parseSearchTerms(
        query: String,
        capSensitive: Boolean = false
    ): List<ISearchTerm<in IGenus>> {

        // Start by splitting the query at spaces
        val queryTerms = query.split(" ")
        val searchTerms = mutableListOf<ISearchTerm<in IGenus>>()

        for (i in queryTerms.indices) {
            val term = queryTerms[i]

            // Attempt to split this term into a prefix and its target, e.g. "diet:carnivore"
            val splits = term.split(":")
            if (splits.size != 2) {
                // If not possible or invalid number of splits, treat it as a name
                searchTerms.add(GenusNameSearchTerm(term, capSensitive, PREFIX_COLON_LIST))
            }
            else {
                // If here, then we have a term of the form "X:Y"
                val key = splits[0]
                val value = splits[1]

                // We may have multiple accepted values e.g. "X:a+b+c", so we split
                val values = value.split("+")

                // Identify the type of term from the prefix
                val searchTerm: ISearchTerm<IGenus>? =
                    when (key) {
                        TAXON_PREFIX    -> TaxonNameSearchTerm(value, taxa)
                        TYPE_PREFIX     -> CreatureTypeSearchTerm(values)
                        DIET_PREFIX     -> DietSearchTerm(values)
                        PERIOD_PREFIX   -> TimePeriodSearchTerm(values)
                        COLOR_PREFIX    -> SelectedColorSearchTerm(values)
                        LOCATION_PREFIX -> LocationSearchTerm(values, locations)
                        else -> {
                            Log.d("SearchConstructor", "Unknown query filter \'$term\'")
                            null
                        }
                    }

                searchTerm?.let {
                    searchTerms.add(it)
                }
            }
        }

        return searchTerms
    }

    override fun getQuery(): String = query

    override fun isQueryEmpty(): Boolean = query.isEmpty()
    override fun getSuggestedSuffixes(): List<String> {
        val lastSearchTerm = this.terms.lastOrNull()

        // If we have at least one search-term, attempt to generate
        // the predicted suffixes for the last term
        val suggestions = lastSearchTerm?.generateSearchSuggestions() ?: emptyList()
        return suggestions
    }

    override fun toFilter(): IFilter<IGenus> {
        val filters = terms.map { it.toFilter() }
        return FilterBuilderImpl(filters.toMutableList()).build()
    }
}