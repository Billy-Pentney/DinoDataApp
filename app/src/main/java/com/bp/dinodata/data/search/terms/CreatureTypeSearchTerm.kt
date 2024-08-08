package com.bp.dinodata.data.search.terms

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalOffer
import com.bp.dinodata.data.enum_readers.CreatureTypeConverter
import com.bp.dinodata.data.filters.CreatureTypeFilter
import com.bp.dinodata.data.filters.IFilter
import com.bp.dinodata.data.genus.IHasCreatureType

class CreatureTypeSearchTerm(
    originalText: String,
    possibleTypes: List<String> = CreatureTypeConverter.getListOfOptions()
): ListBasedSearchTerm(
    originalText,
    termType = SearchTermType.CreatureType,
    allPossibleValues = possibleTypes,
    imageIconVector = Icons.Filled.LocalOffer
) {
    override fun toFilter(): IFilter<in IHasCreatureType> {
        val creatureTypes = queryArguments.mapNotNull { CreatureTypeConverter.matchType(it) }
        return CreatureTypeFilter(creatureTypes)
    }
}