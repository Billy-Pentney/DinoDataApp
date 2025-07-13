package com.bp.dinodata.data.search

import com.bp.dinodata.data.search.terms.ISearchTerm

interface IMutableSearch<T>: ISearch<T> {

    fun updateQuery(
        query: String,
        locations: List<String>,
        generaNames: List<String>,
        taxa: List<String>
    ): IMutableSearch<T>

    override fun withoutQuery(): IMutableSearch<T>
    override fun withoutTerm(term: ISearchTerm<in T>): IMutableSearch<T>
}