package com.bp.dinodata.presentation.list_genus

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bp.dinodata.data.IGenus
import com.bp.dinodata.data.IResultsByLetter
import com.bp.dinodata.data.ResultsByLetter
import com.bp.dinodata.presentation.LoadState
import com.bp.dinodata.use_cases.GenusUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListGenusViewModel @Inject constructor(
    @set:Inject var genusUseCases: GenusUseCases
): ViewModel() {

//    private var _listOfGenera: MutableStateFlow<List<Genus>> = MutableStateFlow(emptyList())
    private var _listOfGeneraByLetter: MutableStateFlow<IResultsByLetter<IGenus>> = MutableStateFlow(ResultsByLetter())

    private var _uiState: MutableState<ListGenusUiState> = mutableStateOf(ListGenusUiState(emptyList()))

    private var searchQuery: MutableState<String> = mutableStateOf("")
    private var searchBarVisibility: MutableState<Boolean> = mutableStateOf(false)


    init {
        _uiState.value = _uiState.value.copy(loadState = LoadState.InProgress)

        viewModelScope.launch {
            genusUseCases.getGeneraGroupedByLetter(
                callback = {
                    updateGroupedListOfGenera(it)
                    _uiState.value = _uiState.value.copy(
                        visiblePage = _listOfGeneraByLetter.value.getGroupByIndex(0),
                        selectedPageIndex = 0
                    )
                },
                onError = {
                    Log.d("ListGenusViewModel", "Error when fetching genera", it)
                }
            )
        }
    }

    private fun updateGroupedListOfGenera(genera: IResultsByLetter<IGenus>) {
        viewModelScope.launch {
            _listOfGeneraByLetter.value = genera

            val pageIndex = _uiState.value.selectedPageIndex

            if (_uiState.value.searchBarVisible) {
                applySearchQuery(searchQuery.value)
            }
            else {
                _uiState.value = _uiState.value.copy(
                    loadState = LoadState.Loaded,
                    visiblePage =_listOfGeneraByLetter.value.getGroupByIndex(pageIndex),
                    pageKeys = _listOfGeneraByLetter.value.getKeys().map { it.toString() }
                )
            }
        }
    }

    fun getUiState(): State<ListGenusUiState> = _uiState

    fun onEvent(event: ListGenusPageUiEvent) {
        when(event) {
            is ListGenusPageUiEvent.UpdateSearchQuery -> {
                applySearchQuery(event.query, event.capSensitive)
            }
            ListGenusPageUiEvent.ClearSearchQueryOrHideBar -> clearSearchQueryOrHideSearchBar()
            is ListGenusPageUiEvent.ToggleSearchBar -> {
                _uiState.value = _uiState.value.copy(
                    searchBarVisible = event.visible
                )
            }
            is ListGenusPageUiEvent.SwitchToPage -> {
                val key = _listOfGeneraByLetter.value.getKey(event.pageIndex)
                // TODO - check if index is invalid
                val visibleGenera = _listOfGeneraByLetter.value.getGroupByLetter(key)
                _uiState.value = _uiState.value.copy(
                    visiblePage = visibleGenera,
                    selectedPageIndex = event.pageIndex
                )
            }
        }
    }

    private fun applySearchQuery(query: String, capitalSensitive: Boolean = false) {
        searchQuery.value =
            (when {
                capitalSensitive -> query
                else -> query.lowercase()
            })

        val queryTrimmed = query.trim()

        viewModelScope.launch {
            val filteredGenera = _listOfGeneraByLetter.value.toList().filter {
                it.getName().startsWith(queryTrimmed, ignoreCase = !capitalSensitive)
            }
            _uiState.value = _uiState.value.applySearch(
                searchQuery = searchQuery.value,
                searchResults = filteredGenera
            )
        }
    }

    private fun clearSearchQueryOrHideSearchBar() {
        if (_uiState.value.searchBarQuery.isNotEmpty()) {
            // If any text is present, clear it, but leave the bar open
            _uiState.value = _uiState.value.applySearch(
                searchQuery = "",
                _listOfGeneraByLetter.value.toList()
            )
        }
        else {
            // Otherwise, hide the search bar
            _uiState.value = _uiState.value.copy(searchBarVisible = false)
        }
    }

    fun getSearchQueryState(): State<String> = searchQuery
    fun getSearchBarVisibility(): State<Boolean> = searchBarVisibility
}
