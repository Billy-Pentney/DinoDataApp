package com.bp.dinodata.presentation.taxonomy_screen

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bp.dinodata.data.taxon.ITaxon
import com.bp.dinodata.data.taxon.ITaxonCollection
import com.bp.dinodata.presentation.DataState
import com.bp.dinodata.use_cases.GenusUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaxonomyScreenViewModel @Inject constructor(
    @set:Inject var genusUseCases: GenusUseCases
): ViewModel(), ITaxonomyScreenViewModel {

    private val taxonomyFlow: MutableState<DataState<ITaxonCollection>> = mutableStateOf(DataState.Idle())

    init {
        viewModelScope.launch {
            taxonomyFlow.value = DataState.LoadInProgress()
            genusUseCases.getTaxonomyList().take(1).collect {
                taxonomyFlow.value = it
            }
        }
    }

    override fun getTaxonomyList(): State<DataState<ITaxonCollection>> {
        return taxonomyFlow
    }
}