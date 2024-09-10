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
import com.bp.dinodata.presentation.list_genus.TextFieldState
import com.bp.dinodata.presentation.map
import com.bp.dinodata.use_cases.GenusUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TaxonomyScreenUiState(
    val searchBoxTextState: TextFieldState = TextFieldState(),
    val isSearchBarVisible: Boolean = false
)


@HiltViewModel
class TaxonomyScreenViewModel @Inject constructor(
    @set:Inject var genusUseCases: GenusUseCases
): ViewModel(), ITaxonomyScreenViewModel {

    private val taxonomyFlow: MutableState<DataState<out TaxonCollection>> = mutableStateOf(DataState.Idle())

    private val toastFlow: MutableSharedFlow<String> = MutableSharedFlow()

    private val uiState = mutableStateOf(TaxonomyScreenUiState())

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

    override fun getUiState(): State<TaxonomyScreenUiState> = uiState

    override fun onEvent(event: TaxonomyScreenUiEvent) {

        when (event) {
            is TaxonomyScreenUiEvent.UpdateTaxonExpansion -> {
                // Set whether this taxon is expanded
                taxonomyFlow.value = taxonomyFlow.value.map {
                    it.setExpansionState(
                        event.taxon.getName() to event.expanded
                    )
                }
            }

            TaxonomyScreenUiEvent.CloseAllExpandedTaxa -> {
                val numExpandedBefore = getNumExpandedTaxa()
                // Set whether this taxon is expanded
                taxonomyFlow.value = taxonomyFlow.value.map {
                    it.collapseAllTaxa()
                }
                val numExpandedAfter = getNumExpandedTaxa()
                val numClosed = numExpandedBefore - numExpandedAfter
                emitToastMessage("Collapsed $numClosed taxa")
                Log.d("TaxonomyScreenViewModel", "Showing $numExpandedAfter taxa expanded")
            }

            is TaxonomyScreenUiEvent.UpdateSearchBoxText -> {
                uiState.value = uiState.value.copy(
                    searchBoxTextState = uiState.value.searchBoxTextState.copy(
                        text = event.text
                    )
                )
                // Clear any highlighted genera
                taxonomyFlow.value = taxonomyFlow.value.map {
                    it.copy(highlighted = emptySet())
                }
            }

            TaxonomyScreenUiEvent.ToggleSearchBarVisibility -> {
                uiState.value = uiState.value.copy(
                    isSearchBarVisible = !uiState.value.isSearchBarVisible
                )
            }

            TaxonomyScreenUiEvent.SubmitSearch -> {
                val currText = uiState.value.searchBoxTextState.getTextContent()

                taxonomyFlow.value = taxonomyFlow.value.map {
                    // Get the line of names to the given node
                    val taxaToExpand = it.getPathOfTaxaToName(currText)

                    Log.d("TaxonomyScreenVM", "Got ${taxaToExpand.size} taxa in lineage: ${taxaToExpand.joinToString()}")

                    if (taxaToExpand.isEmpty()) {
                        emitToastMessage("Taxon \'${currText}\' not found!")
                        it
                    }
                    else {
                        emitToastMessage("Expanded ${taxaToExpand.size} taxa to show \'${currText}\'")
                        // Mark all taxa on this line as expanded
                        it.copy(
                            expanded = taxaToExpand.toSet(),
                            highlighted = mutableSetOf(currText.lowercase().trim())
                        )
                    }
                }
            }
        }
    }

    private fun emitToastMessage(message: String) {
        viewModelScope.launch {
            toastFlow.emit(message)
        }
    }

    override fun getTaxonomyList(): State<DataState<out ITaxonCollection>> = taxonomyFlow
}