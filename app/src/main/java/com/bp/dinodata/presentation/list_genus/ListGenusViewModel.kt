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

    init {
        viewModelScope.launch {
            _listOfGeneraByLetter.collect {
                val searchInitial = it.map { data -> data.toList() }
                _uiState.value = _uiState.value.copy(
                    allPageData = it,
                    searchResults = searchInitial
                )
            }
        }
    }

    fun getUiState(): State<ListGenusUiState> = _uiState

    fun onEvent(event: ListGenusPageUiEvent) {
        when(event) {
            is ListGenusPageUiEvent.UpdateSearchQuery -> {
                runSearch(event.state.text)
            }
            ListGenusPageUiEvent.ClearSearchQueryOrHideBar
                -> clearSearchQueryOrHideSearchBar()
            is ListGenusPageUiEvent.ToggleSearchBar -> {
                _uiState.value = _uiState.value.copy(searchBarVisible = event.visible)
            }
            is ListGenusPageUiEvent.SwitchToPage -> {
                _uiState.value = _uiState.value.copy(selectedPageIndex = event.pageIndex)
            }
            is ListGenusPageUiEvent.AcceptSearchSuggestion -> {
                runSearch(_uiState.value.getAutofillSuggestion())
            }
            is ListGenusPageUiEvent.RemoveSearchTerm -> {
                _uiState.value = _uiState.value.removeSearchTerm(event.term)
                runSearch()
            }
        }
    }

    private fun runSearch(query: String = _uiState.value.getQuery()) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.applySearch(
                query = query,
                locations = _locationsFlow.value,
                taxa = _taxaFlow.value
            )
        }
    }

    private fun clearSearchQueryOrHideSearchBar() {
        if (!_uiState.value.search.isQueryEmpty()) {
            // If any text is present, clear it, but leave the bar open
            _uiState.value = _uiState.value
                .clearSearch()
                .applySearch()
        }
        else {
            // Otherwise, hide the search bar
            _uiState.value = _uiState.value.copy(searchBarVisible = false)
        }
    }

}
