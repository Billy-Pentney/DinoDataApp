package com.bp.dinodata.data.search

import android.util.Log
import com.bp.dinodata.data.filters.FilterBuilderImpl
import com.bp.dinodata.data.filters.IFilter
import com.bp.dinodata.data.genus.IGenus

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
                        "taxon" -> searchTerms.add(TaxonNameSearchTerm(value))
                        "type" -> searchTerms.add(CreatureTypeSearchTerm(values))
                        "diet" -> searchTerms.add(DietSearchTerm(values))
                        "period" -> searchTerms.add(TimePeriodSearchTerm(values))
                        "color" -> searchTerms.add(SelectedColorSearchTerm(values))
                        else -> Log.d("SearchConstructor", "Unknown query filter \'$term\'")
                    }
                }
            }

            val suggestions = searchTerms.lastOrNull()?.generateSearchSuggestions()

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