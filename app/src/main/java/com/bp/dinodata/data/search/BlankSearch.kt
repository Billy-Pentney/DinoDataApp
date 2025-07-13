package com.bp.dinodata.data.search

import android.util.Log
import com.bp.dinodata.data.filters.BlankFilter
import com.bp.dinodata.data.filters.IFilter
import com.bp.dinodata.data.search.terms.ISearchTerm
import com.bp.dinodata.presentation.list_genus.TextFieldState


/**
 * A generic search with no query and no terms. It accepts all items of the given type.
 * */
class BlankSearch<T>: IMutableSearch<T> {
    override fun getFullQuery(): String = ""
    override fun getQuery(): String = ""
    override fun getSuggestedSuffixes(): List<String> = emptyList()
    override fun getAutofillSuggestion(): String = TextFieldState.DEFAULT_HINT
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

    override fun withoutQuery(): IMutableSearch<T> = this
    override fun toMutableSearch(): IMutableSearch<T> = this
    override fun updateQuery(
        query: String,
        locations: List<String>,
        generaNames: List<String>,
        taxa: List<String>
    ): IMutableSearch<T> {
        Log.w("BlankSearch", "Attempt to update a Blank Search - not supported!")
        return this
    }
}

