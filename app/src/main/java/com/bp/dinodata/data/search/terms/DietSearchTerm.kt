package com.bp.dinodata.data.search.terms

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restaurant
import com.bp.dinodata.data.enum_readers.DietConverter
import com.bp.dinodata.data.filters.DietFilter
import com.bp.dinodata.data.filters.IFilter
import com.bp.dinodata.data.genus.IHasDiet

class DietSearchTerm(
    originalText: String,
    possibleDiets: List<String> = DietConverter.getListOfOptions()
): ListBasedSearchTerm(
    originalText,
    termType = SearchTermType.Diet,
    allPossibleValues = possibleDiets,
    imageIconVector = Icons.Filled.Restaurant
) {
    override fun toFilter(): IFilter<in IHasDiet> {
        val diets = queryArguments.mapNotNull { DietConverter.matchType(it) }
        return DietFilter(diets)
    }
}