package com.bp.dinodata.data.search.terms

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ColorLens
import com.bp.dinodata.data.filters.IFilter
import com.bp.dinodata.data.filters.SelectedColorFilter
import com.bp.dinodata.data.genus.IGenus
import com.bp.dinodata.presentation.utils.ThemeConverter

class SelectedColorSearchTerm(
    originalText: String,
    possibleValues: List<String> = ThemeConverter.getListOfOptions()
): ListBasedSearchTerm(
    originalText = originalText,
    termType = SearchTermType.Color,
    allPossibleValues = possibleValues.map { it.lowercase() },
    imageIconVector = Icons.Filled.ColorLens
) {
    override fun toFilter(): IFilter<in IGenus> {
        return SelectedColorFilter(queryArguments.map { it.uppercase() })
    }
}