package com.bp.dinodata.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bp.dinodata.data.Genus
import com.bp.dinodata.use_cases.GenusUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
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

    // Stores the details for the Genus being shown
    private var _visibleGenus: MutableStateFlow<Genus?> = MutableStateFlow(null)

    init {
        viewModelScope.launch {
            genusUseCases.getGenusByName(
                currentGenusName,
                callback = { it?.let { updateGenus(it) } },
                onFailure = { }
            )
        }
    }

    private fun updateGenus(genus: Genus) {
        viewModelScope.launch {
            _visibleGenus.emit(genus)
        }
    }

    fun getVisibleGenus(): StateFlow<Genus?> = _visibleGenus

}