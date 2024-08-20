package com.bp.dinodata.presentation.taxonomy_screen

import com.bp.dinodata.data.taxon.ITaxon

sealed class TaxonomyScreenUiEvent {
    data class UpdateTaxonExpansion(val taxon: ITaxon, val expanded: Boolean): TaxonomyScreenUiEvent()
}
