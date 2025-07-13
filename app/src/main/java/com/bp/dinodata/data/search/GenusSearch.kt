package com.bp.dinodata.data.search

import com.bp.dinodata.data.enum_readers.DietConverter
import com.bp.dinodata.data.enum_readers.EpochConverter
import com.bp.dinodata.data.filters.FilterBuilderImpl
import com.bp.dinodata.data.filters.IFilter
import com.bp.dinodata.data.genus.IGenus
import com.bp.dinodata.data.search.terms.BasicSearchTerm
import com.bp.dinodata.data.search.terms.ISearchTerm

class GenusSearch(
    query: String,
    terms: List<ISearchTerm<in IGenus>> = emptyList(),
    possibleGeneraNames: List<String> = emptyList(),
    possibleLocations: List<String> = emptyList(),
    possibleTaxa: List<String> = emptyList(),
    possibleDiets: List<String> = DietConverter.getListOfOptions(),
    possibleTimePeriods: List<String> = EpochConverter.getListOfOptions(),
    private var caseSensitive: Boolean = false
): IMutableSearch<IGenus> {
    private val searchTermBuilder = SearchTermBuilder(
        validGenera = possibleGeneraNames,
        validTaxa = possibleTaxa,
        possibleDiets = possibleDiets,
        validLocations = possibleLocations,
        possibleTimePeriods = possibleTimePeriods,
        caseSensitive = caseSensitive
    )

    private var searchTerms: MutableList<ISearchTerm<in IGenus>> = terms.toMutableList()
    private var currentTerm: ISearchTerm<in IGenus> = BasicSearchTerm(query)

    private var filter: IFilter<IGenus>? = null

    init {
        parseSearchTerms(query)
    }


    /**
     * Build a search from the raw query text as received from the UI.
     * Completed search terms which are separated by whitespace are parsed into ISearchTerm
     * objects.
     * @param query The raw string which should be split into search terms.
     */
    private fun parseSearchTerms(query: String) {

        // Check if this string ends in a whitespace
        // so we can determine whether the last term is "complete"
        val endsInWhitespace = query.lastOrNull()?.isWhitespace() ?: false

        val splits = query.split(" ")
                          .filter { it.trim().isNotEmpty() }

        val terms = splits.map {
            searchTermBuilder.fromText(it)
        }

        if (endsInWhitespace || terms.isEmpty()) {
            currentTerm = searchTermBuilder.fromText("")
            searchTerms.addAll(terms)
        }
        else {
            // Keep the last term open for editing
            currentTerm = terms.last()
            searchTerms.addAll(terms.dropLast(1))
        }
    }

    override fun getFullQuery(): String {
        return getCompletedTerms()
            .joinToString(" ") { it.toOriginalText() } + " " + getQuery()
    }

    override fun getQuery(): String = currentTerm.toOriginalText()
    override fun isQueryEmpty(): Boolean = getQuery().isEmpty()
    override fun getSuggestedSuffixes(): List<String> {
        // If we have at least one search-term, attempt to generate
        // the predicted suffixes for the last term
        return currentTerm
            .generateSearchSuggestions()
            .filter { it.isNotEmpty() }
    }

    override fun getAutofillSuggestion(): String {
        // If there are no suggestions, return the empty string
        return getSuggestedSuffixes()
            .firstOrNull()
            ?.let { getQuery() + it }
            ?: ""
    }

    override fun getCompletedTerms(): List<ISearchTerm<in IGenus>> = searchTerms

    override fun updateQuery(
        query: String,
        locations: List<String>,
        generaNames: List<String>,
        taxa: List<String>
    ): IMutableSearch<IGenus> {
        this.searchTermBuilder.update(
            locations,
            generaNames,
            taxa
        )
        this.parseSearchTerms(query)
        this.filter = null
        return this
    }

    override fun withoutQuery(): IMutableSearch<IGenus> {
        this.currentTerm = searchTermBuilder.fromText("")
        this.filter = null          // Invalidate the existing filter
        return this
    }

    override fun withoutTerm(term: ISearchTerm<in IGenus>): IMutableSearch<IGenus> {
        if (term == currentTerm) {
            this.currentTerm = searchTermBuilder.fromText("")
        }
        else {
            val newTerms = searchTerms.toMutableList()
            newTerms.remove(term)
            searchTerms = newTerms
        }
        this.filter = null          // Invalidate the existing filter
        return this
    }


    override fun toFilter(): IFilter<IGenus> {
        if (filter == null) {
            val filters = searchTerms.map { it.toFilter() }.toMutableList()
            filters.add(currentTerm.toFilter())
            filter = FilterBuilderImpl(filters).build()
        }
        return filter!!
    }

    override fun acceptsItem(item: IGenus): Boolean {
        return this.toFilter().acceptsItem(item)
    }

    override fun toMutableSearch(): IMutableSearch<IGenus> = this
}