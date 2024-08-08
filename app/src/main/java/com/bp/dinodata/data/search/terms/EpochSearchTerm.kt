package com.bp.dinodata.data.search.terms

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import com.bp.dinodata.data.enum_readers.EpochConverter
import com.bp.dinodata.data.filters.IFilter
import com.bp.dinodata.data.filters.EpochFilter
import com.bp.dinodata.data.genus.IHasTimePeriodInfo

class EpochSearchTerm(
    originalText: String,
    validOptions: List<String> = EpochConverter.getListOfOptions()
): ListBasedSearchTerm(
    originalText,
    allPossibleValues = validOptions,
    termType = SearchTermType.TimePeriod,
    imageIconVector = Icons.Filled.AccessTime
) {
    override fun toFilter(): IFilter<in IHasTimePeriodInfo> {
        val periods = queryArguments.mapNotNull { EpochConverter.matchType(it) }
        return EpochFilter(periods.map { it.getEpochId() })
    }
}