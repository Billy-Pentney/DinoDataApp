package com.bp.dinodata.presentation.taxonomy_screen

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bp.dinodata.data.taxon.ITaxonCollection
import com.bp.dinodata.data.taxon.TaxonCollection
import com.bp.dinodata.presentation.DataState
import com.bp.dinodata.presentation.map
import com.bp.dinodata.use_cases.GenusUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaxonomyScreenViewModel @Inject constructor(
    @set:Inject var genusUseCases: GenusUseCases
): ViewModel(), ITaxonomyScreenViewModel {

    private val taxonomyFlow: MutableState<DataState<out TaxonCollection>> = mutableStateOf(DataState.Idle())

    private val toastFlow: MutableSharedFlow<String> = MutableSharedFlow()

    init {
        viewModelScope.launch {
            taxonomyFlow.value = DataState.LoadInProgress()
            genusUseCases.getTaxonomyList().take(1).collect {
                taxonomyFlow.value = it
            }
        }
    }

    override fun getToastFlow(): MutableSharedFlow<String> = toastFlow

    private fun getNumExpandedTaxa(): Int {
        val state = taxonomyFlow.value
        return if (state is DataState.Success) {
            state.data.getNumExpandedTaxa()
        } else {
            0
        }
    }

    override fun onEvent(event: TaxonomyScreenUiEvent) {
        when (event) {
            is TaxonomyScreenUiEvent.UpdateTaxonExpansion -> {
                // Set whether this taxon is expanded
                taxonomyFlow.value = taxonomyFlow.value.map {
                    it.copy(
                        event.taxon.getName() to event.expanded
                    )
                }
            }

            TaxonomyScreenUiEvent.CloseAllExpandedTaxa -> {
                val numExpandedBefore = getNumExpandedTaxa()
                // Set whether this taxon is expanded
                taxonomyFlow.value = taxonomyFlow.value.map {
                    it.closeAllTaxa()
                }
                val numExpandedAfter = getNumExpandedTaxa()
                val numClosed = numExpandedBefore - numExpandedAfter
                viewModelScope.launch {
                    toastFlow.emit("Closed $numClosed taxa")
                }
                Log.d("TaxonomyScreenViewModel", "Showing $numExpandedAfter taxa expanded")
            }
        }
    }


    override fun getTaxonomyList(): State<DataState<out ITaxonCollection>> = taxonomyFlow
}