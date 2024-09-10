package com.bp.dinodata.presentation.list_genus

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.bp.dinodata.R
import com.bp.dinodata.data.search.terms.SearchTermType

@Composable
fun convertSearchTermTypeToString(type: SearchTermType): String {
    return when (type) {
        SearchTermType.Text -> stringResource(R.string.search_term_text)
        SearchTermType.CreatureType -> stringResource(R.string.search_term_creature_type)
        SearchTermType.Diet -> stringResource(R.string.search_term_diet)
        SearchTermType.TimePeriod -> stringResource(R.string.search_term_time_period)
        SearchTermType.Taxon -> stringResource(id = R.string.search_term_taxon)
        SearchTermType.Color -> stringResource(id = R.string.search_term_color)
        SearchTermType.Location -> stringResource(id = R.string.search_term_location)
        SearchTermType.Favourite -> stringResource(id = R.string.search_term_favourite)
    }
}