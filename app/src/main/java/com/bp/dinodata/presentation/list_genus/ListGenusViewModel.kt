package com.bp.dinodata.presentation.list_genus

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bp.dinodata.data.search.GenusSearch
import com.bp.dinodata.data.IResultsByLetter
import com.bp.dinodata.data.genus.IGenusWithPrefs
import com.bp.dinodata.presentation.LoadState
import com.bp.dinodata.use_cases.GenusUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListGenusViewModel @Inject constructor(
    @set:Inject var genusUseCases: GenusUseCases
): ViewModel() {

    private val _listOfGeneraByLetter: Flow<IResultsByLetter<IGenusWithPrefs>?> = genusUseCases.getGenusWithPrefsByLetterFlow()

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
                    applySearchQuery(_uiState.value.search.getQuery())
                }
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
                _uiState.value = _uiState.value.copy(searchBarVisible = event.visible)
            }
            is ListGenusPageUiEvent.SwitchToPage -> {
                _uiState.value = _uiState.value.copy(selectedPageIndex = event.pageIndex)
            }
            is ListGenusPageUiEvent.AcceptSearchSuggestion -> {
                val suggestions = _uiState.value.search.getSuggestedSuffixes()
                val existingQuery = _uiState.value.search.getQuery()
                if (suggestions.isNotEmpty()) {
                    val newQuery = existingQuery + suggestions[0]
                    _uiState.value = _uiState.value.applySearch(
                        GenusSearch.constructFromQuery(newQuery),
                        jumpCursorToEnd = true
                    )
                }
            }
        }
    }

    private fun applySearchQuery(query: String, capitalSensitive: Boolean = false) {
        val search = genusUseCases.generateSearchFromQuery(query, capitalSensitive)
        viewModelScope.launch {
            _uiState.value = _uiState.value.applySearch(search)
        }
    }

    private fun clearSearchQueryOrHideSearchBar() {
        if (!_uiState.value.search.isQueryEmpty()) {
            // If any text is present, clear it, but leave the bar open
            _uiState.value = _uiState.value.applySearch(GenusSearch())
        }
        else {
            // Otherwise, hide the search bar
            _uiState.value = _uiState.value.copy(searchBarVisible = false)
        }
    }

}
