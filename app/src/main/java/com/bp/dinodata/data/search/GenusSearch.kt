package com.bp.dinodata.data.search

import com.bp.dinodata.data.filters.BlankFilter
import com.bp.dinodata.data.filters.FilterBuilderImpl
import com.bp.dinodata.data.filters.IFilter
import com.bp.dinodata.data.genus.IGenus
import com.bp.dinodata.data.search.terms.BasicSearchTerm
import com.bp.dinodata.data.search.terms.ISearchTerm

/**
 * Describes a collection of SearchTerms (filters) which can be applied to a list of Genus objects.
 */
data class GenusSearch(
    private var terms: List<ISearchTerm<in IGenus>> = emptyList(),
    private val currentTerm: ISearchTerm<in IGenus> = BasicSearchTerm("")
): IMutableSearch<IGenus> {

    private val filter = this.toFilter()

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
        val suggestedSuffix = getSuggestedSuffixes().firstOrNull()
        // If there are no suggestions, return the empty string
        return suggestedSuffix?.let { getQuery() + it } ?: ""
    }

    override fun getCompletedTerms(): List<ISearchTerm<in IGenus>> = terms
    override fun withoutTerm(term: ISearchTerm<in IGenus>): GenusSearch {
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
        return this.filter.acceptsItem(item)
    }

//    override fun updateQuery(
//        textContent: String,
//        terms: List<ISearchTerm<IGenus>>,
//        locations: List<String>,
//        taxa: List<String>
//    ): IMutableSearch<IGenus> {
//        val termBuilder = SearchTermBuilder(
//            taxaList = taxa,
//            locations = locations,
//            possibleDiets = DietConverter.getListOfOptions(),
//            possibleTimePeriods = EpochConverter.getListOfOptions()
//        )
//        return this.copy(
//            currentTerm = termBuilder.fromText(textContent),
//            terms = terms
//        )
//    }

    override fun withoutQuery(): ISearch<IGenus> {
        return GenusSearch(terms)
    }
}


/**
 * A generic search with no query and no terms. It accepts all items of the given type.
 * */
class BlankSearch<T>: IMutableSearch<T> {
    override fun getFullQuery(): String = ""
    override fun getQuery(): String = ""
    override fun getSuggestedSuffixes(): List<String> = emptyList()
    override fun getAutofillSuggestion(): String = ""
    override fun isQueryEmpty(): Boolean = true
    override fun withoutTerm(term: ISearchTerm<in T>): IMutableSearch<T> = this
    override fun getCompletedTerms(): List<ISearchTerm<in T>> = emptyList()

    override fun toFilter(): IFilter<T> = BlankFilter()
    override fun acceptsItem(item: T): Boolean {
        // Vacuously true, we accept all items
        return true
    }

    override fun <R:T> applyTo(list: Iterable<R>): List<T> {
        // Copy the list
        return list.toList()
    }

    override fun withoutQuery(): ISearch<T> = this
}

