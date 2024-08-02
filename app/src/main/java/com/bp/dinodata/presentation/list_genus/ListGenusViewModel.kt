package com.bp.dinodata.presentation.list_genus

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bp.dinodata.data.IResultsByLetter
import com.bp.dinodata.data.genus.IGenusWithPrefs
import com.bp.dinodata.presentation.DataState
import com.bp.dinodata.presentation.map
import com.bp.dinodata.use_cases.GenusUseCases
import com.bp.dinodata.use_cases.ListGenusScreenUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListGenusViewModel @Inject constructor(
    @set:Inject var genusUseCases: GenusUseCases,
    @set:Inject var listGenusUseCases: ListGenusScreenUseCases
): ViewModel() {

    private val _listOfGeneraByLetter: StateFlow<DataState<IResultsByLetter<IGenusWithPrefs>>>
        = genusUseCases.getGenusWithPrefsByLetterFlow()
        .stateIn(viewModelScope, SharingStarted.Lazily, DataState.LoadInProgress())
    private val _locationsFlow: StateFlow<List<String>> = genusUseCases.getGenusLocationsFlow()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    private val _taxaFlow: StateFlow<List<String>> = genusUseCases.getGenusTaxaFlow()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _uiState: MutableState<ListGenusUiState> = mutableStateOf(ListGenusUiState())

    private val eventFlow: MutableSharedFlow<ListGenusPageUiEvent> = MutableSharedFlow()

    init {
        viewModelScope.launch {
            _listOfGeneraByLetter.collect {
                _uiState.value = _uiState.value.copy(
                    allPageData = it
                )
                runSearch()
            }
        }

        viewModelScope.launch {
            eventFlow.collect {
                handleEvent(it)
            }
        }
    }

    fun getUiState(): State<ListGenusUiState> = _uiState

    fun onEvent(event: ListGenusPageUiEvent) {
        viewModelScope.launch {
            eventFlow.emit(event)
        }
    }

    private fun handleEvent(event: ListGenusPageUiEvent) {
        when(event) {
            is ListGenusPageUiEvent.UpdateSearchQuery -> {
                val numTermsBefore = _uiState.value.getCompletedSearchTerms().size
                makeSearch(event.state.text)
                val numTermsAfter = _uiState.value.getCompletedSearchTerms().size

                if (numTermsAfter != numTermsBefore || event.alsoRunSearch) {
                    runSearch()
                }
            }
            ListGenusPageUiEvent.RunSearch -> runSearch()
            ListGenusPageUiEvent.ClearSearchQueryOrHideBar -> clearOrHideSearch()

            is ListGenusPageUiEvent.ToggleSearchBar -> {
                _uiState.value = _uiState.value.copy(searchBarVisible = event.visible)
            }
            is ListGenusPageUiEvent.SwitchToPage -> {
                _uiState.value = _uiState.value.copy(selectedPageIndex = event.pageIndex)
            }
            is ListGenusPageUiEvent.AcceptSearchSuggestion -> {
                makeSearch(_uiState.value.getAutofillSuggestion())
                runSearch()
            }
            is ListGenusPageUiEvent.RemoveSearchTerm -> {
                _uiState.value = _uiState.value.removeSearchTerm(event.term)
                    .makeNewSearch(
                        locations = _locationsFlow.value,
                        taxa = _taxaFlow.value
                    )
                runSearch()
            }
            is ListGenusPageUiEvent.UpdateScrollState -> {
                _uiState.value = _uiState.value.copy(
                    firstVisibleItem = event.state.firstVisibleItemIndex,
                    firstVisibleItemOffset = event.state.firstVisibleItemScrollOffset
                )
            }
        }
    }

    private fun makeSearch(newQuery: String) {
        _uiState.value = _uiState.value
            .updateSearchQuery(newQuery)
            .makeNewSearch(
                locations = _locationsFlow.value,
                taxa = _taxaFlow.value
            )
    }

    private fun runSearch() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.runSearch()
        }
    }

    private fun clearOrHideSearch() {
        if (!_uiState.value.isQueryEmpty()) {
            // If any text is present, clear it, but leave the bar open
            _uiState.value = _uiState.value
                .clearSearch()
                .makeNewSearch()
                .runSearch()
        }
        else {
            // Otherwise, hide the search bar
            _uiState.value = _uiState.value.copy(searchBarVisible = false)
        }
    }

}
