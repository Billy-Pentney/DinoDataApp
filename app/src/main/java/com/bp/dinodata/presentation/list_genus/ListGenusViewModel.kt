package com.bp.dinodata.presentation.list_genus

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.TextRange
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bp.dinodata.data.IResultsByLetter
import com.bp.dinodata.data.genus.IGenusWithPrefs
import com.bp.dinodata.presentation.DataState
import com.bp.dinodata.use_cases.GenusUseCases
import com.bp.dinodata.use_cases.ListGenusScreenUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListGenusViewModel @Inject constructor(
    @set:Inject var genusUseCases: GenusUseCases,
    @set:Inject var listGenusUseCases: ListGenusScreenUseCases
): ViewModel(), IListGenusViewModel {

    private val _listOfGeneraByLetter: StateFlow<DataState<IResultsByLetter<IGenusWithPrefs>>>
        = genusUseCases.getGenusWithPrefsByLetterFlow()
        .stateIn(viewModelScope, SharingStarted.Lazily, DataState.LoadInProgress())
    private val _locationsFlow: StateFlow<List<String>> = genusUseCases.getGenusLocationsFlow()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    private val _taxaFlow: StateFlow<List<String>> = genusUseCases.getGenusTaxaFlow()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _uiState: MutableState<ListGenusUiState> = mutableStateOf(ListGenusUiState())

    private val eventFlow: MutableSharedFlow<ListGenusPageUiEvent> = MutableSharedFlow()
    private val toastFlow: MutableSharedFlow<String> = MutableSharedFlow()

    private var searchQuery: MutableSharedFlow<String> = MutableSharedFlow()

    init {
        viewModelScope.launch {
            _listOfGeneraByLetter.collectLatest {
                _uiState.value = _uiState.value.copy(
                    allPageData = it
                )
                runSearch()

                if (it is DataState.Success) {
                    toastFlow.emit("Loaded ${it.data.getSize()} genera!")
                }
                else if (it is DataState.Failed) {
                    toastFlow.emit("Failed to retrieve genus data. Please check your internet connection")
                }
            }
        }

        viewModelScope.launch {
            eventFlow.collect {
                handleEvent(it)
            }
        }

        viewModelScope.launch {
            searchQuery.collectLatest {
                _uiState.value = _uiState.value.runSearch()
            }
        }
    }

    override fun getUiState(): State<ListGenusUiState> = _uiState

    override fun onUiEvent(event: ListGenusPageUiEvent) {
        viewModelScope.launch {
            eventFlow.emit(event)
        }
    }

    private fun handleEvent(event: ListGenusPageUiEvent) {
        when(event) {
            is ListGenusPageUiEvent.UpdateSearchQuery -> {
                makeSearch(event.state.text, event.state.selection)
                runSearch()
            }
            ListGenusPageUiEvent.RunSearch -> runSearch()
            ListGenusPageUiEvent.ClearSearchQueryOrHideBar -> clearOrHideSearch()
            ListGenusPageUiEvent.RefreshFeed -> refreshFeed()

            is ListGenusPageUiEvent.ToggleSearchBar -> {
                _uiState.value = _uiState.value.copy(searchBarVisible = event.visible)
            }
            is ListGenusPageUiEvent.SwitchToPage -> {
                _uiState.value = _uiState.value.copy(selectedPageIndex = event.pageIndex)
            }
            is ListGenusPageUiEvent.AcceptSearchSuggestion -> {
                val suggestedText = _uiState.value.getAutofillSuggestion()
                makeSearch(suggestedText, TextRange(suggestedText.length))
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

            ListGenusPageUiEvent.NavigateUp -> {
                if (_uiState.value.getFullQuery().isNotEmpty()) {
                    // If there's any text
                    handleEvent(ListGenusPageUiEvent.ClearSearchQueryOrHideBar)
                }
                else {
                    // Close the Search Bar when navigating up
                    handleEvent(ListGenusPageUiEvent.ToggleSearchBar(false))
                }
            }
        }
    }

    private fun makeSearch(newQuery: String, newTextSelection: TextRange) {
        _uiState.value = _uiState.value
            .updateSearchQuery(newQuery, newTextSelection)
            .makeNewSearch(
                locations = _locationsFlow.value,
                taxa = _taxaFlow.value
            )
    }

    private fun runSearch() {
        viewModelScope.launch {
            searchQuery.emit(_uiState.value.getQuery())
        }
    }

    private fun refreshFeed() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                allPageData = _listOfGeneraByLetter.value
            )
            runSearch()
            toastFlow.emit("Refreshed feed!")
        }
    }

    override fun getToastFlow(): Flow<String> = toastFlow

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
