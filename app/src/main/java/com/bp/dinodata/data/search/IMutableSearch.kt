package com.bp.dinodata.data.search

import com.bp.dinodata.data.search.terms.ISearchTerm

interface IMutableSearch<T>: ISearch<T> {
    fun removeTerm(term: ISearchTerm<in T>): IMutableSearch<T>
    fun getCompletedTerms(): List<ISearchTerm<in T>>

//    fun updateQuery(
//        textContent: String,
//        terms: List<ISearchTerm<T>>,
//        locations: List<String>,
//        taxa: List<String>
//    ): IMutableSearch<T>
}