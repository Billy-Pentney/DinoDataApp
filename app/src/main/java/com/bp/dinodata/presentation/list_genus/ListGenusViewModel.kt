package com.bp.dinodata.presentation.list_genus

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bp.dinodata.data.search.GenusSearch
import com.bp.dinodata.data.IResultsByLetter
import com.bp.dinodata.data.genus.IGenusWithPrefs
import com.bp.dinodata.presentation.LoadState
import com.bp.dinodata.use_cases.GenusUseCases
import com.bp.dinodata.use_cases.ListGenusScreenUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
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

    private val _listOfGeneraByLetter: Flow<IResultsByLetter<IGenusWithPrefs>?> = genusUseCases.getGenusWithPrefsByLetterFlow()
    private val _locationsFlow: StateFlow<List<String>> = genusUseCases.getGenusLocationsFlow()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    private val _taxaFlow: StateFlow<List<String>> = genusUseCases.getGenusTaxaFlow()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _uiState: MutableState<ListGenusUiState> = mutableStateOf(ListGenusUiState())

    init {
        _uiState.value = _uiState.value.copy(loadState = LoadState.InProgress)

        viewModelScope.launch {
            _listOfGeneraByLetter.collect {
                _uiState.value = _uiState.value.copy(
                    allPageData = it,
                    loadState = LoadState.Loaded,
                    searchResults = it?.toList()
                )

                if (_uiState.value.searchBarVisible) {
                    applySearchQuery(
                        TextFieldValue(
                            _uiState.value.getSearchQuery(),
                            _uiState.value.cursorRange
                        )
                    )
                }
            }
        }
    }

    fun getUiState(): State<ListGenusUiState> = _uiState

    fun onEvent(event: ListGenusPageUiEvent) {
        when(event) {
            is ListGenusPageUiEvent.UpdateSearchQuery -> {
                applySearchQuery(event.state)
            }
            ListGenusPageUiEvent.ClearSearchQueryOrHideBar -> clearSearchQueryOrHideSearchBar()
            is ListGenusPageUiEvent.ToggleSearchBar -> {
                _uiState.value = _uiState.value.copy(searchBarVisible = event.visible)
            }
            is ListGenusPageUiEvent.SwitchToPage -> {
                _uiState.value = _uiState.value.copy(selectedPageIndex = event.pageIndex)
            }
            is ListGenusPageUiEvent.AcceptSearchSuggestion -> {
                _uiState.value = listGenusUseCases
                    .acceptSearchSuggestion(_uiState.value)
                    .applySearch()
            }
        }
    }

    private fun applySearchQuery(textFieldValue: TextFieldValue) {
        _uiState.value = _uiState.value.copy(
            searchQueryText = textFieldValue.text,
            cursorRange = textFieldValue.selection
        )
        viewModelScope.launch {
            _uiState.value = _uiState.value.applySearch(
                search = _uiState.value.search.copy(
                    query = textFieldValue.text,
                    // Update the list parameters
                    locations = _locationsFlow.value,
                    taxa = _taxaFlow.value
                )
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
