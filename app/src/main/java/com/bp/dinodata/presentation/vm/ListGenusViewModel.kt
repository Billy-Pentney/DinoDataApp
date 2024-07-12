package com.bp.dinodata.presentation.vm

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bp.dinodata.data.Genus
import com.bp.dinodata.presentation.LoadState
import com.bp.dinodata.repo.GeneraPageResult
import com.bp.dinodata.repo.PageResult
import com.bp.dinodata.use_cases.GenusUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
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

    private val nextPageNum: MutableState<Int> = mutableIntStateOf(0)
    private val _allPagesLoaded: MutableState<Boolean> = mutableStateOf(false)

    init {
        loadAllGenera()
//        initiateNextPageLoad()
    }

    private fun loadAllGenera() {
        viewModelScope.launch {
            _isLoaded.value = LoadState.LoadingPage(1)
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
            _isLoaded.value = LoadState.IsLoaded(1)
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
        = _visibleGenera.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

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
                startAfter = _listOfGenera.value.lastOrNull()?.name,
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
            ListGenusPageUiEvent.ClearSearchQuery -> clearSearchQuery()
        }
    }

    private fun applySearchQuery(query: String, capitalSensitive: Boolean = false) {
        searchQuery.value =
            (when {
                capitalSensitive -> query
                else -> query.lowercase()
            })

        viewModelScope.launch {
            _visibleGenera.emit(
                _listOfGenera.value.filter {
                    if (capitalSensitive) {
                        query.trim() in it.name
                    }
                    else {
                        query.trim().lowercase() in it.name.lowercase()
                    }
                }
            )
        }
    }

    private fun clearSearchQuery() {
        searchQuery.value = ""
        viewModelScope.launch {
            _visibleGenera.emit(_listOfGenera.value)
        }
    }

    fun getSearchQueryState(): State<String> = searchQuery
}

sealed class ListGenusPageUiEvent {

    data object InitiateNextPageLoad : ListGenusPageUiEvent()

    data class UpdateSearchQuery(
        val query: String,
        val capSensitive: Boolean = false
    ) : ListGenusPageUiEvent()

    data object ClearSearchQuery : ListGenusPageUiEvent()

}
