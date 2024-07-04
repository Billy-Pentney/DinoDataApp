package com.bp.dinodata.presentation

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bp.dinodata.data.Genus
import com.bp.dinodata.use_cases.GenusUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class DetailGenusViewModel @Inject constructor(
    handle: SavedStateHandle,
    @set:Inject var genusUseCases: GenusUseCases
): ViewModel() {

    companion object {
        const val GENUS_KEY = "genusName"
    }

    private var currentGenusName: String = checkNotNull(handle[GENUS_KEY])

    private var _visibleGenus: StateFlow<Genus?> = MutableStateFlow(null)

    init {
        // Retrieve the flow containing this genus
        _visibleGenus = genusUseCases.getGenusByName(currentGenusName).stateIn(
            viewModelScope, SharingStarted.Lazily, null
        )
    }

    fun getVisibleGenus(): StateFlow<Genus?> = _visibleGenus


}