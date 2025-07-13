package com.bp.dinodata.presentation.list_genus

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.TextRange
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bp.dinodata.data.IResultsByLetter
import com.bp.dinodata.data.genus.IGenus
import com.bp.dinodata.data.genus.IGenusWithPrefs
import com.bp.dinodata.data.search.BlankSearch
import com.bp.dinodata.data.search.GenusSearch
import com.bp.dinodata.data.search.IMutableSearch
import com.bp.dinodata.data.search.ISearch
import com.bp.dinodata.presentation.DataState
import com.bp.dinodata.presentation.map
import com.bp.dinodata.use_cases.GenusUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class ListGenusViewModel @Inject constructor(
    @set:Inject var genusUseCases: GenusUseCases,
): ViewModel(), IListGenusViewModel {

    private val _listOfGeneraByLetter: StateFlow<DataState<IResultsByLetter<IGenusWithPrefs>>>
        = genusUseCases.getGenusWithPrefsByLetterFlow()
        .stateIn(viewModelScope, SharingStarted.Lazily, DataState.LoadInProgress())

    private var searchResultFlow: Flow<DataState<List<IGenus>>>? = emptyFlow()

    private val pagerUiState: MutableState<ListGenusUiState> = mutableStateOf(ListGenusUiState())
    private val contentMode: MutableState<ListGenusContentMode> = mutableStateOf(ListGenusContentMode.Pager)
    private val searchUiState: MutableState<ListGenusSearchUiState> = mutableStateOf(
        ListGenusSearchUiState()
    )

    private val searchTextFlow: MutableStateFlow<String> = MutableStateFlow("")
    private val searchObjFlow: MutableStateFlow<IMutableSearch<IGenus>> = MutableStateFlow(
        GenusSearch("")
    )
    private val newSearchFlow = genusUseCases.makeNewGenusSearchFlow(searchTextFlow, searchObjFlow)

    private val _uiEventFlow: MutableSharedFlow<ListGenusPageUiEvent> = MutableSharedFlow()
    private val toastFlow: MutableSharedFlow<String> = MutableSharedFlow()

    init {
        viewModelScope.launch {
            _listOfGeneraByLetter.collectLatest {
                pagerUiState.value = pagerUiState.value.copy(
                    allPageData = it
                )
                if (it is DataState.Failed) {
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

        // Set the latest search in the UI state
        viewModelScope.launch {
            searchObjFlow.collect {
                Log.d("ListGenusViewModel", "Updating search state with \"${it.getFullQuery()}\"")
                searchUiState.value = searchUiState.value.updateSearch(it)
            }
        }

        // Apply the Genus search, with a delay of 200 milliseconds to avoid overcomputation
        viewModelScope.launch {
            newSearchFlow.debounce(100)
                .collectLatest {
                    applySearch(it)
                }
        }
    }

    override fun getContentModeState(): State<ListGenusContentMode> = contentMode
    override fun getSearchUiState(): State<IListGenusSearchUiState> = searchUiState

    override fun getUiState(): State<ListGenusUiState> = pagerUiState

    override fun onUiEvent(event: ListGenusPageUiEvent) {
        viewModelScope.launch {
            _uiEventFlow.emit(event)
        }
    }

    private fun handleEvent(event: ListGenusPageUiEvent) {
        when (event) {
            is ListGenusPageUiEvent.UpdateSearchQuery -> {
                searchUiState.value = searchUiState.value.updateSearchTextState(
                    event.textValue.text, event.textValue.selection
                )
                viewModelScope.launch {
                    Log.d("ListGenusViewModel", "Emitting search with text \"${event.textValue.text}\"")
                    searchTextFlow.emit(event.textValue.text)
                }
            }

            ListGenusPageUiEvent.RunSearch -> {
                applySearch(searchUiState.value.getSearch())
            }
            ListGenusPageUiEvent.ClearSearchQueryOrHideBar -> {
                clearOrHideSearch()
            }
            ListGenusPageUiEvent.RefreshFeed -> {
                refreshFeed()
            }

            is ListGenusPageUiEvent.ToggleSearchBar -> {
                contentMode.value =
                    when {
                        event.visible -> ListGenusContentMode.Search
                        else          -> ListGenusContentMode.Pager
                    }
            }
            is ListGenusPageUiEvent.SwitchToPage -> {
                pagerUiState.value = pagerUiState.value.copy(selectedPageIndex = event.pageIndex)
            }
            is ListGenusPageUiEvent.AcceptSearchSuggestion -> {
                if (searchUiState.value.hasSuggestions()) {
                    val suggestedText = searchUiState.value.getAutofillSuggestion()
                    searchUiState.value = searchUiState.value.updateSearchTextState(
                        suggestedText,
                        TextRange(suggestedText.length),
                        modifiedByApp = true            // We've set the content
                    )
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
                viewModelScope.launch {
                    searchObjFlow.emit(newSearch)
                }
            }
            is ListGenusPageUiEvent.UpdateScrollState -> {
                when (contentMode.value) {
                    ListGenusContentMode.Pager -> {
                        pagerUiState.value = pagerUiState.value.copy(
                            firstVisibleItem = event.state.firstVisibleItemIndex,
                            firstVisibleItemOffset = event.state.firstVisibleItemScrollOffset
                        )
                    }
                    ListGenusContentMode.Search -> {
                        searchUiState.value = searchUiState.value.copy(
                            firstVisibleItem = event.state.firstVisibleItemIndex,
                            firstVisibleItemOffset = event.state.firstVisibleItemScrollOffset
                        )
                    }
                }
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


    private fun applySearch(search: ISearch<IGenus>, resetScroll: Boolean = true) {
        Log.d("ListGenusViewModel", "Applying search \"${search.getFullQuery()}\"")

        searchUiState.value = searchUiState.value.copy(
            searchResults = DataState.LoadInProgress()
        )

        Log.d("ListGenusViewModel", "Search has suggestion: \"${search.getAutofillSuggestion()}\"")

        viewModelScope.launch {
            // Apply the search, getting back a new flow
            searchResultFlow = genusUseCases.applyGenusSearch(search)

            // Retrieve from the flow and update the UI state
            searchResultFlow?.collectLatest { searchResultState ->
                val firstVisibleItemIndex =
                    if (resetScroll) 0 else searchUiState.value.getFirstVisibleItemIndex()
                val firstVisibleItemOffset =
                    if (resetScroll) 0 else searchUiState.value.getFirstVisibleItemOffset()

                Log.d("ListGenusViewModel", "New search results: ${searchResultState.map { it.size }}")

                searchUiState.value = searchUiState.value
                    .copy(
                        searchResults = searchResultState,
                        firstVisibleItem = firstVisibleItemIndex,
                        firstVisibleItemOffset = firstVisibleItemOffset
                    )
            }
        }
    }

    private fun refreshFeed() {
        viewModelScope.launch {
            pagerUiState.value = pagerUiState.value.copy(
                allPageData = _listOfGeneraByLetter.value
            )
            applySearch(searchUiState.value.getSearch())
            toastFlow.emit("Refreshed feed!")
        }
    }

    override fun getToastFlow(): Flow<String> = toastFlow

    private fun clearOrHideSearch() {
        if (!searchUiState.value.isQueryEmpty()) {
            // If any text is present, clear it, but leave the bar open
            searchUiState.value = searchUiState.value
                .updateSearch(BlankSearch())
            applySearch(searchUiState.value.getSearch(), resetScroll=true)
        }
        else {
            // Otherwise, hide the search bar
            contentMode.value = ListGenusContentMode.Pager
        }
    }
}
