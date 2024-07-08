package com.bp.dinodata.presentation.vm

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bp.dinodata.data.Genus
import com.bp.dinodata.presentation.LoadState
import com.bp.dinodata.use_cases.GenusUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private var lastPageNum = 0

    private var _listOfGenera: StateFlow<List<Genus>>

//    private var _visibleGenera: MutableState<List<Genus>> = mutableStateOf(emptyList())

    init {
        _isLoaded.value = LoadState.LoadingPage(1)
        _listOfGenera = genusUseCases.getGeneraAsList()
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList()).also {
                _isLoaded.value = LoadState.IsLoaded(1)
            }
        _isLoaded.value = LoadState.IsLoaded(1)
//        initiateNextPageLoad()
    }

    fun getListOfGenera(): StateFlow<List<Genus>> = _listOfGenera
    fun getIsLoadedState(): State<LoadState> = _isLoaded

//    fun initiateNextPageLoad() {
//        viewModelScope.launch {
//            // Note that a load-operation is in-progress
//            _isLoaded.value = LoadState.LoadingPage(lastPageNum)
//
//            genusUseCases.getNextPageOfGenera(
//                startAfter = _visibleGenera.value.lastOrNull()?.name,
//                callback = {
//                    val list = _visibleGenera.value.toMutableList()
//                    list.addAll(it)
//                    _visibleGenera.value = list
//
//                    // The load operation is complete
//                    _isLoaded.value = LoadState.IsLoaded(lastPageNum)
//                    Log.d("ListGenusViewModel", "Received ${it.size} genera via callback")
//                },
//                onException = { exception ->
//                    _isLoaded.value = LoadState.Error(exception.message)
//                    Log.d("ListGenusViewModel", "Failed due to Firebase exception", exception)
//                }
//            )
//        }
//    }
}
