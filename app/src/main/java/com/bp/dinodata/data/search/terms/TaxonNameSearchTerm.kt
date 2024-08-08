package com.bp.dinodata.data.search.terms

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountTree
import com.bp.dinodata.data.filters.IFilter
import com.bp.dinodata.data.filters.TaxonFilter
import com.bp.dinodata.data.genus.IHasTaxonomy

class TaxonNameSearchTerm(
    originalText: String,
    possibleTaxa: List<String> = emptyList()
): ListBasedSearchTerm(
    originalText = originalText,
    termType = SearchTermType.Taxon,
    allPossibleValues = possibleTaxa.map { it.lowercase() },
    imageIconVector = Icons.Filled.AccountTree
) {
    override fun toFilter(): IFilter<in IHasTaxonomy> = TaxonFilter(queryArguments)
}