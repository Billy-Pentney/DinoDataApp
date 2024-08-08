package com.bp.dinodata.data.search.terms

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import com.bp.dinodata.data.enum_readers.EraConverter
import com.bp.dinodata.data.filters.IFilter
import com.bp.dinodata.data.filters.TimePeriodFilter
import com.bp.dinodata.data.genus.IHasTimePeriodInfo

class TimeEraSearchTerm(
    originalText: String,
    validOptions: List<String> = EraConverter.getListOfOptions()
): ListBasedSearchTerm(
    originalText = originalText,
    allPossibleValues = validOptions,
    termType = SearchTermType.TimePeriod,
    imageIconVector = Icons.Filled.AccessTime
) {
    override fun toFilter(): IFilter<in IHasTimePeriodInfo> {
        val periods = queryArguments.mapNotNull { EraConverter.matchType(it) }
        return TimePeriodFilter(periods)
    }
}