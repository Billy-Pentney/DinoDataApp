package com.bp.dinodata.data.search.terms

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import com.bp.dinodata.data.filters.IFilter
import com.bp.dinodata.data.filters.LocationFilter
import com.bp.dinodata.data.genus.IGenus

class LocationSearchTerm(
    originalText: String,
    allLocations: List<String> = emptyList()
): ListBasedSearchTerm(
    originalText = originalText,
    termType = SearchTermType.Location,
    allPossibleValues = allLocations.map { it.lowercase().replace(" ", "_") },
    imageIconVector = Icons.Filled.LocationOn
) {
    init {
        Log.d("LocationSearchTerm", "Got locations length ${queryArguments.size}")
    }

    override fun toFilter(): IFilter<in IGenus> {
        return LocationFilter(queryArguments)
    }
}