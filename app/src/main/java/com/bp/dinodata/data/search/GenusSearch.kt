package com.bp.dinodata.data.search

import com.bp.dinodata.data.DietConverter
import com.bp.dinodata.data.TimePeriodConverter
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
    private var terms: List<ISearchTerm<in IGenus>> = emptyList(),
    private val currentTerm: ISearchTerm<in IGenus> = BasicSearchTerm(""),
    private var capSensitive: Boolean = false,
    private val locations: List<String> = emptyList(),
    private val taxa: List<String> = emptyList(),
    private val possibleDiets: List<String> = DietConverter.getListOfOptions(),
    private val possibleTimePeriods: List<String> = TimePeriodConverter.getListOfOptions()
): IMutableSearch<IGenus> {
    override fun getFullQuery(): String {
        return getCompletedTerms().joinToString(" ") {
            it.toOriginalText()
        } + " " + getQuery()
    }

    override fun getQuery(): String = currentTerm.toOriginalText()
    override fun isQueryEmpty(): Boolean = getQuery().isEmpty()
    override fun getSuggestedSuffixes(): List<String> {
        // If we have at least one search-term, attempt to generate
        // the predicted suffixes for the last term
        val suggestions = currentTerm.generateSearchSuggestions()
        return suggestions.filter { it.isNotEmpty() }
    }

    override fun getAutofillSuggestion(): String {
        return getQuery() + getSuggestedSuffixes().getOrElse(0) { "" }
    }

    override fun getCompletedTerms(): List<ISearchTerm<in IGenus>> = terms
    override fun removeTerm(term: ISearchTerm<in IGenus>): GenusSearch {
        val newTerms = terms.toMutableList()
        newTerms.remove(term)
        return this.copy(terms = newTerms)
    }

    override fun toFilter(): IFilter<IGenus> {
        val filters = terms.map { it.toFilter() }.toMutableList()
        filters.add(currentTerm.toFilter())
        return FilterBuilderImpl(filters).build()
    }

    override fun acceptsItem(item: IGenus): Boolean {
        return this.toFilter().acceptsItem(item)
    }
}

