package com.bp.dinodata.presentation.vm

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.bp.dinodata.data.Genus
import com.bp.dinodata.use_cases.GenusUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ListGenusViewModel @Inject constructor(
    @set:Inject var genusUseCases: GenusUseCases
): ViewModel() {

    private val _listOfGenera: MutableState<List<Genus>> = mutableStateOf(emptyList())

    init {
        genusUseCases.getAllGenera()
    }

    fun getListOfGenera(): State<List<Genus>> {
        return _listOfGenera
    }

}
