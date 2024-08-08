package com.bp.dinodata.data.search

import android.util.Log
import com.bp.dinodata.data.genus.IGenus
import com.bp.dinodata.data.search.terms.BasicSearchTerm
import com.bp.dinodata.data.search.terms.CreatureTypeSearchTerm
import com.bp.dinodata.data.search.terms.DietSearchTerm
import com.bp.dinodata.data.search.terms.FavouriteSearchTerm
import com.bp.dinodata.data.search.terms.ISearchTerm
import com.bp.dinodata.data.search.terms.LocationSearchTerm
import com.bp.dinodata.data.search.terms.SelectedColorSearchTerm
import com.bp.dinodata.data.search.terms.TaxonNameSearchTerm
import com.bp.dinodata.data.search.terms.TimeEraSearchTerm
import com.bp.dinodata.data.search.terms.TimePeriodSearchTerm

class SearchTermBuilder(
    private val taxaList: List<String>,
    private val possibleDiets: List<String>,
    private val possibleTimePeriods: List<String>,
    val locations: List<String>
) {
    companion object {
        private const val TAXON_PREFIX = "taxon"
        private const val TYPE_PREFIX = "type"
        private const val PERIOD_PREFIX = "period"
        private const val COLOR_PREFIX = "color"
        private const val LOCATION_PREFIX = "location"
        private const val DIET_PREFIX = "diet"
        private const val FAVOURITE_PREFIX = "fave"
        private const val ERA_PREFIX = "era"

        private val PREFIX_LIST = listOf(
            TAXON_PREFIX, TYPE_PREFIX, PERIOD_PREFIX, COLOR_PREFIX,
            LOCATION_PREFIX, DIET_PREFIX, FAVOURITE_PREFIX, ERA_PREFIX
        )

        private val PREFIX_COLON_LIST = PREFIX_LIST.map { "$it:" }
    }

    fun fromText(termText: String): ISearchTerm<in IGenus> {
        val splits = termText.trim().split(":")
        if (splits.size != 2) {
            return BasicSearchTerm(termText, searchKeywords = PREFIX_COLON_LIST)
        }
        return fromKeyValuePair(splits[0], splits[1])
    }

    fun fromKeyValuePair(key: String, values: String): ISearchTerm<in IGenus> {
        // If here, then we have a term of the form "X:Y"
        val termText = "$key:$values"
        return when (key) {
            TAXON_PREFIX     -> TaxonNameSearchTerm(termText, taxaList)
            TYPE_PREFIX      -> CreatureTypeSearchTerm(termText)
            DIET_PREFIX      -> DietSearchTerm(termText)
            PERIOD_PREFIX    -> TimePeriodSearchTerm(termText)
            COLOR_PREFIX     -> SelectedColorSearchTerm(termText)
            LOCATION_PREFIX  -> LocationSearchTerm(termText, locations)
            FAVOURITE_PREFIX -> FavouriteSearchTerm(termText)
            ERA_PREFIX       -> TimeEraSearchTerm(termText)
            else -> {
                Log.d("SearchConstructor", "Unknown query filter \'$termText\'")
                BasicSearchTerm(termText)
            }
        }
    }
}