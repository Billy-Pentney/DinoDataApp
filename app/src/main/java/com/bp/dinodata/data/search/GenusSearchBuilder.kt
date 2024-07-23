package com.bp.dinodata.data.search

import com.bp.dinodata.data.DietConverter
import com.bp.dinodata.data.TimePeriodConverter
import com.bp.dinodata.data.genus.IGenus

class GenusSearchBuilder(
    query: String,
    terms: List<ISearchTerm<in IGenus>> = emptyList(),
    private var capSensitive: Boolean = false,
    private val locations: List<String> = emptyList(),
    private val taxa: List<String> = emptyList(),
    private val possibleDiets: List<String> = DietConverter.getListOfOptions(),
    private val possibleTimePeriods: List<String> = TimePeriodConverter.getListOfOptions()
) {
    private val searchTermBuilder = SearchTermBuilder(
        taxaList = taxa,
        possibleDiets = possibleDiets,
        locations = locations,
        possibleTimePeriods = possibleTimePeriods
    )

    private var currentTerm: ISearchTerm<in IGenus> = GenusNameSearchTerm(query)
    private var searchTerms: MutableList<ISearchTerm<in IGenus>> = terms.toMutableList()

    init {
        parseSearchTerms(query)
    }

    /**
     * Build a search from the raw query text as received from the UI.
     * @param query The raw string which is visible via the Search Bar.
     */
    private fun parseSearchTerms(query: String) {

        // Start by splitting the query at spaces
        val querySplits = query.split(" +".toRegex())

        val lastSplit = querySplits.lastOrNull() ?: return

        // Convert the strings to Term objects.
        // Separate the last term, so we can decide whether it is "complete".
        val lastTerm = searchTermBuilder.fromText(lastSplit)
        val newTerms = querySplits.dropLast(1)
            .filter { it.isNotBlank() }
            .mapNotNull { searchTermBuilder.fromText(it) }

        searchTerms.addAll(newTerms)

        currentTerm =
            if (lastTerm != null && lastSplit.trim().isNotEmpty()) {
                // This term is complete, it has at least one non-space character
                lastTerm
            } else {
                GenusNameSearchTerm(lastSplit)
            }
    }

    fun build(): GenusSearch {
        return GenusSearch(
            currentTerm = currentTerm,
            terms = searchTerms,
            locations = locations,
            taxa = taxa,
            possibleTimePeriods = possibleTimePeriods,
            possibleDiets = possibleDiets
        )
    }
}