package com.bp.dinodata.data.search

import com.bp.dinodata.data.DietConverter
import com.bp.dinodata.data.TimePeriodConverter
import com.bp.dinodata.data.genus.IGenus

class GenusSearchBuilder(
    query: String,
    private var capSensitive: Boolean = false,
    private val locations: List<String> = emptyList(),
    private val taxa: List<String> = emptyList(),
    private val possibleDiets: List<String> = DietConverter.getListOfOptions(),
    private val possibleTimePeriods: List<String> = TimePeriodConverter.getListOfOptions(),
    terms: List<ISearchTerm<in IGenus>> = emptyList()
) {
    private val _terms = terms.toMutableList()

    init {
        _terms.addAll(parseSearchTerms(query))
    }

    /**
     * Build a search from the raw query text as received from the UI.
     * @param query The raw string which is visible via the Search Bar.
     */
    private fun parseSearchTerms(query: String): List<ISearchTerm<in IGenus>> {

        // Start by splitting the query at spaces
        val queryTerms = query.split(" +".toRegex())

        val searchTermBuilder = SearchTermBuilder(
            taxaList = taxa,
            possibleDiets = possibleDiets,
            locations = locations,
            possibleTimePeriods = possibleTimePeriods
        )

        val searchTerms = queryTerms.mapNotNull { searchTermBuilder.fromText(it) }
        return searchTerms
    }

    fun build(): GenusSearch {
        return GenusSearch(_terms)
    }
}