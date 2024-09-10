package com.bp.dinodata.data.search.terms

import androidx.compose.ui.graphics.vector.ImageVector
import com.bp.dinodata.data.DataParsing
import com.bp.dinodata.data.filters.IFilter
import com.bp.dinodata.data.filters.TextFilter
import com.bp.dinodata.data.filters.TextFilterWithRegex
import com.bp.dinodata.data.genus.IGenus
import com.bp.dinodata.data.genus.IHasName

class BasicSearchTerm(
    private val query: String,
    private val isCapitalSensitive: Boolean = false,
    private val searchKeywords: List<String> = emptyList(),
): ISearchTerm<IHasName> {
    private val filter = this.toFilter()

    override fun getType(): SearchTermType = SearchTermType.Text
    override fun toFilter(): IFilter<IHasName> = TextFilterWithRegex(query)

    override fun generateSearchSuggestions(): List<String> {
        return DataParsing.getLongestPotentialSuffixes(query, searchKeywords)
    }
    override fun toString(): String = "Contains text: \"$query\""
    override fun toOriginalText(): String = query
    override fun getIconId(): ImageVector? = null

    override fun acceptsItem(item: IHasName): Boolean = filter.acceptsItem(item)

    override fun getQueries(): List<String> = listOf(query)
}