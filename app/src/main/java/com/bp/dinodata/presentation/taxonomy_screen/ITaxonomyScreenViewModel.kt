package com.bp.dinodata.presentation.taxonomy_screen

import androidx.compose.runtime.State
import com.bp.dinodata.data.taxon.ITaxon
import com.bp.dinodata.data.taxon.ITaxonCollection
import com.bp.dinodata.presentation.DataState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow


interface ITaxonomyScreenViewModel {
    fun getTaxonomyList(): State<DataState<ITaxonCollection>>
}
