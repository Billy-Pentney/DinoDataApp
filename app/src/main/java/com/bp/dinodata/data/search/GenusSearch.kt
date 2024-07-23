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
    private var capSensitive: Boolean = false,
    private val locations: List<String> = emptyList(),
    private val taxa: List<String> = emptyList(),
    private val possibleDiets: List<String> = DietConverter.getListOfOptions(),
    private val possibleTimePeriods: List<String> = TimePeriodConverter.getListOfOptions()
): ISearch<IGenus> {

    override fun getQuery(): String = terms.joinToString(" ") { it.toOriginalText() }
    override fun isQueryEmpty(): Boolean = getQuery().isEmpty()
    override fun getSuggestedSuffixes(): List<String> {
        val lastSearchTerm = this.terms.lastOrNull()

        // If we have at least one search-term, attempt to generate
        // the predicted suffixes for the last term
        val suggestions = lastSearchTerm?.generateSearchSuggestions() ?: emptyList()
        return suggestions.filter { it.isNotEmpty() }
    }

    override fun getAllTerms(): List<ISearchTerm<in IGenus>> = terms
    override fun getCompletedTerms(): List<ISearchTerm<in IGenus>> {
        return terms.dropLast(1)
    }

    override fun getLastTermText(): String {
        return terms.lastOrNull()?.toOriginalText() ?: ""
    }

    override fun removeTerm(term: ISearchTerm<in IGenus>): GenusSearch {
        val newTerms = terms.toMutableList()
        newTerms.remove(term)
        return this.copy(
            terms = newTerms
        )
    }

    override fun toFilter(): IFilter<IGenus> {
        val filters = terms.map { it.toFilter() }
        return FilterBuilderImpl(filters.toMutableList()).build()
    }

    override fun acceptsItem(item: IGenus): Boolean {
        return this.toFilter().acceptsItem(item)
    }
}

