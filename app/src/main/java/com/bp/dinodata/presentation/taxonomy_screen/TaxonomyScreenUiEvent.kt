package com.bp.dinodata.presentation.taxonomy_screen

import com.bp.dinodata.data.taxon.ITaxon

sealed class TaxonomyScreenUiEvent {
    /** Invoked when all taxa should be collapsed */
    data object CloseAllExpandedTaxa: TaxonomyScreenUiEvent()

    /** Expand or collapse the given taxon */
    data class UpdateTaxonExpansion(val taxon: ITaxon, val expanded: Boolean): TaxonomyScreenUiEvent()

    /** Update the text in the Search Bar */
    data class UpdateSearchBoxText(val text: String): TaxonomyScreenUiEvent()

    data object SubmitSearch: TaxonomyScreenUiEvent()

    /** Show or hide the Search Bar*/
    data object ToggleSearchBarVisibility: TaxonomyScreenUiEvent()
}
