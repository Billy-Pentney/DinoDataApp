package com.bp.dinodata.presentation.list_genus

import android.util.Log
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bp.dinodata.data.Genus
import com.bp.dinodata.presentation.LoadState
import com.bp.dinodata.repo.PageResult
import com.bp.dinodata.use_cases.GenusUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListGenusViewModel @Inject constructor(
    @set:Inject var genusUseCases: GenusUseCases
): ViewModel() {

    private var _isLoaded: MutableState<LoadState> = mutableStateOf(LoadState.NotLoading)
    private var _listOfGenera: MutableStateFlow<List<Genus>> = MutableStateFlow(emptyList())

    private var _visibleGenera: MutableStateFlow<List<Genus>> = MutableStateFlow(emptyList())

    private var searchQuery: MutableState<String> = mutableStateOf("")
    private var searchBarVisibility: MutableState<Boolean> = mutableStateOf(false)

    private val nextPageNum: MutableState<Int> = mutableIntStateOf(0)
    private val _allPagesLoaded: MutableState<Boolean> = mutableStateOf(false)

    init {
        loadAllGenera()
//        initiateNextPageLoad()
    }

    private fun loadAllGenera() {
        _isLoaded.value = LoadState.LoadingPage(nextPageNum.value)
        viewModelScope.launch {
            genusUseCases.getAllGenera(
                callback = ::updateListOfGenera,
                onError = {
                    Log.d("ListGenusViewModel", "Error when fetching genera", it)
                }
            )
        }
    }

    private fun updateListOfGenera(genera: List<Genus>) {
        viewModelScope.launch {
            _listOfGenera.value = genera
            _isLoaded.value = LoadState.IsLoaded(nextPageNum.value)
            nextPageNum.value++
            applySearchQuery(searchQuery.value)
        }
    }

    private fun appendToListOfGenera(fetchResult: PageResult<Genus>) {
        viewModelScope.launch {
            val newGenera = fetchResult.data

            // Append the new data to the end of the old list
            val existingGenera = _listOfGenera.value.toMutableList()
            existingGenera.addAll(newGenera)
            _listOfGenera.value = existingGenera

            Log.d("ListGenusViewModel",
                "Appended ${newGenera.size} genera via callback. " +
                        "Total size: ${existingGenera.size}")

            // Indicate that this page has been loaded
            _isLoaded.value = LoadState.IsLoaded(nextPageNum.value)
            nextPageNum.value++

            _allPagesLoaded.value = fetchResult.isAllDataRetrieved

            applySearchQuery(searchQuery.value)
        }
    }

    fun getListOfGenera(): StateFlow<List<Genus>>
        = _visibleGenera

    fun getIsLoadedState(): State<LoadState> = _isLoaded

    private fun initiateNextPageLoad() {
        if (_isLoaded.value is LoadState.LoadingPage) {
            Log.d("ListGenusViewModel", "Load already in progress; ignoring duplicate")
            return
        }
        else {
            Log.d("ListGenusViewModel", "Triggering page-load")
        }

        viewModelScope.launch {
            // Note that a load-operation is in-progress
            if (nextPageNum.value == 0) {
                _isLoaded.value = LoadState.LoadingFirstPage
            }
            else {
                _isLoaded.value = LoadState.LoadingPage(nextPageNum.value)
            }

            genusUseCases.getNextPageOfGenera(
                startAfter = _listOfGenera.value.lastOrNull()?.getName(),
                callback = ::appendToListOfGenera,
                onException = { exc ->
                    _isLoaded.value = LoadState.Error(exc.message)
                    Log.d("ListGenusViewModel", "Failed to load next page due to Firebase exception", exc)
                }
            )
        }
    }

    fun onEvent(event: ListGenusPageUiEvent) {
        when(event) {
            ListGenusPageUiEvent.InitiateNextPageLoad -> {
                initiateNextPageLoad()
            }
            is ListGenusPageUiEvent.UpdateSearchQuery -> {
                applySearchQuery(event.query, event.capSensitive)
            }
            ListGenusPageUiEvent.ClearSearchQueryOrHideBar -> clearSearchQueryOrHideSearchBar()
            is ListGenusPageUiEvent.ToggleSearchBar -> {
                searchBarVisibility.value = event.visible
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
            _visibleGenera.emit(
                _listOfGenera.value.filter {
                    it.getName().startsWith(queryTrimmed, ignoreCase = !capitalSensitive)
                }
            )
        }
    }

    private fun clearSearchQueryOrHideSearchBar() {
        if (searchQuery.value.isNotEmpty()) {
            // If any text is present, clear it, but leave the bar open
            searchQuery.value = ""
            viewModelScope.launch {
                _visibleGenera.emit(_listOfGenera.value)
            }
        }
        else {
            // Otherwise, hide the search bar
            searchBarVisibility.value = false
        }
    }

    fun getSearchQueryState(): State<String> = searchQuery
    fun getSearchBarVisibility(): State<Boolean> = searchBarVisibility
}

