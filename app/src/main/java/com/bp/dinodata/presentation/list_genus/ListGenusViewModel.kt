package com.bp.dinodata.presentation.list_genus

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bp.dinodata.data.IResultsByLetter
import com.bp.dinodata.data.genus.IGenusWithPrefs
import com.bp.dinodata.data.search.BlankSearch
import com.bp.dinodata.presentation.DataState
import com.bp.dinodata.presentation.map
import com.bp.dinodata.use_cases.GenusUseCases
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
): ViewModel(), IListGenusViewModel {

    private val _listOfGeneraByLetter: StateFlow<DataState<IResultsByLetter<IGenusWithPrefs>>>
        = genusUseCases.getGenusWithPrefsByLetterFlow()
        .stateIn(viewModelScope, SharingStarted.Lazily, DataState.LoadInProgress())

    private val _uiState: MutableState<ListGenusUiState> = mutableStateOf(ListGenusUiState())
    private val contentMode: MutableState<ListGenusContentMode> = mutableStateOf(ListGenusContentMode.Pager)
    private val searchUiState: MutableState<ListGenusSearchUiState> = mutableStateOf(
        ListGenusSearchUiState()
    )

    private val _uiEventFlow: MutableSharedFlow<ListGenusPageUiEvent> = MutableSharedFlow()
    private val toastFlow: MutableSharedFlow<String> = MutableSharedFlow()

    private var _applySearchFlow: MutableSharedFlow<Boolean> = MutableSharedFlow()
    private var _searchTextFieldValueFlow: MutableSharedFlow<TextFieldValue> = MutableSharedFlow()

    init {
        viewModelScope.launch {
            _listOfGeneraByLetter.collectLatest {
                _uiState.value = _uiState.value.copy(
                    allPageData = it
                )
                applySearch(resetScroll = false)

                if (it is DataState.Success) {
                    toastFlow.emit("Loaded ${it.data.getSize()} genera!")
                }
                else if (it is DataState.Failed) {
                    toastFlow.emit("Failed to retrieve genus data. Please check your internet connection")
                }
            }
        }

        // Listen for UI-events
        viewModelScope.launch {
            _uiEventFlow.collect {
                handleEvent(it)
            }
        }

        // Listen for changes to the contents of the Search-Box
        viewModelScope.launch {
            _searchTextFieldValueFlow.collect {
                Log.d("ListGenusViewModel", "Got search \'${it.text}\'")
                makeSearch(it.text, it.selection, applyAfter=true)
            }
        }

        // Listen for Search events
        viewModelScope.launch {
            _applySearchFlow.collect { resetScroll ->
                searchUiState.value = searchUiState.value.copy(
                    searchResults = DataState.LoadInProgress()
                )

                val currentSearch = searchUiState.value.getSearch()
                val filteredGenera = genusUseCases.applyGenusSearch(currentSearch)

                val firstVisibleItemIndex =
                    if (resetScroll) 0 else searchUiState.value.getFirstVisibleItemIndex()
                val firstVisibleItemOffset =
                    if (resetScroll) 0 else searchUiState.value.getFirstVisibleItemOffset()

                Log.d("ListGenusViewModel","New search results: ${filteredGenera.map { it.size }}")

                searchUiState.value = searchUiState.value
                    .copy(
                        searchResults = filteredGenera,
                        firstVisibleItem = firstVisibleItemIndex,
                        firstVisibleItemOffset = firstVisibleItemOffset
                    )
            }
        }
    }

    override fun getContentModeState(): State<ListGenusContentMode> = contentMode
    override fun getSearchUiState(): State<IListGenusSearchUiState> = searchUiState

    override fun getUiState(): State<ListGenusUiState> = _uiState

    override fun onUiEvent(event: ListGenusPageUiEvent) {
        viewModelScope.launch {
            _uiEventFlow.emit(event)
        }
    }

    private fun handleEvent(event: ListGenusPageUiEvent) {
        when (event) {
            is ListGenusPageUiEvent.UpdateSearchQuery -> {
                viewModelScope.launch {
                    searchUiState.value = searchUiState.value.updateSearchTextState(event.textValue.text, event.textValue.selection)
                    _searchTextFieldValueFlow.emit(event.textValue)
                }
            }

            ListGenusPageUiEvent.RunSearch -> applySearch()
            ListGenusPageUiEvent.ClearSearchQueryOrHideBar -> clearOrHideSearch()
            ListGenusPageUiEvent.RefreshFeed -> refreshFeed()

            is ListGenusPageUiEvent.ToggleSearchBar -> {
                contentMode.value =
                    when {
                        event.visible -> ListGenusContentMode.Search
                        else          -> ListGenusContentMode.Pager
                    }
            }
            is ListGenusPageUiEvent.SwitchToPage -> {
                _uiState.value = _uiState.value.copy(selectedPageIndex = event.pageIndex)
            }
            is ListGenusPageUiEvent.AcceptSearchSuggestion -> {
                if (searchUiState.value.hasSuggestions()) {
                    val suggestedText = searchUiState.value.getAutofillSuggestion()
                    viewModelScope.launch {
                        searchUiState.value = searchUiState.value.updateSearchTextState(
                            suggestedText,
                            TextRange(suggestedText.length)
                        )
                        _searchTextFieldValueFlow.emit(
                            TextFieldValue(
                                text = suggestedText,
                                // Move the cursor to the end of the search bar
                                selection = TextRange(suggestedText.length)
                            )
                        )
                    }
                }
                else {
                    searchUiState.value = searchUiState.value.copy(
                        searchBarTextFieldState = searchUiState.value.getSearchTextFieldState().copy(
                            isFocused = false
                        )
                    )
                }
            }
            is ListGenusPageUiEvent.RemoveSearchTerm -> {
                val newSearch = searchUiState.value.getSearch().withoutTerm(event.term)
                searchUiState.value = searchUiState.value.updateSearch(newSearch)
                applySearch()
            }
            is ListGenusPageUiEvent.UpdateScrollState -> {
                _uiState.value = _uiState.value.copy(
                    firstVisibleItem = event.state.firstVisibleItemIndex,
                    firstVisibleItemOffset = event.state.firstVisibleItemScrollOffset
                )
            }

            ListGenusPageUiEvent.NavigateUp -> {
                if (searchUiState.value.getFullQuery().isNotEmpty()) {
                    // If there's any text
                    handleEvent(ListGenusPageUiEvent.ClearSearchQueryOrHideBar)
                }
                else {
                    // Close the Search Bar when navigating up
                    handleEvent(ListGenusPageUiEvent.ToggleSearchBar(false))
                }
            }

            is ListGenusPageUiEvent.ShowToast -> {
                viewModelScope.launch {
                    toastFlow.emit(event.message)
                }
            }

            is ListGenusPageUiEvent.FocusSearchBar -> {
                // Update the UI to reflect the fact that the search bar is/isn't focused
                searchUiState.value = searchUiState.value.copy(
                    searchBarTextFieldState = searchUiState.value.getSearchTextFieldState().copy(
                        isFocused = event.focused,
                        isHintVisible = event.focused
                    )
                )
            }
        }
    }

    private fun makeSearch(newQuery: String, newTextSelection: TextRange, applyAfter: Boolean=false) {
        viewModelScope.launch {
            val oldSearch = searchUiState.value.getSearch()
            val newSearch = genusUseCases.makeNewGenusSearch(newQuery, oldSearch)
            searchUiState.value = searchUiState.value.updateSearch(newSearch)
            if (applyAfter) {
                applySearch()
            }
        }
    }

    private fun applySearch(resetScroll: Boolean = true) {
        viewModelScope.launch {
            _applySearchFlow.emit(resetScroll)
        }
    }

    private fun refreshFeed() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                allPageData = _listOfGeneraByLetter.value
            )
            applySearch()
            toastFlow.emit("Refreshed feed!")
        }
    }

    override fun getToastFlow(): Flow<String> = toastFlow

    private fun clearOrHideSearch() {
        if (!searchUiState.value.isQueryEmpty()) {
            // If any text is present, clear it, but leave the bar open
            searchUiState.value = searchUiState.value
                .updateSearch(BlankSearch())
            applySearch(true)
        }
        else {
            // Otherwise, hide the search bar
            contentMode.value = ListGenusContentMode.Pager
        }
    }

}
