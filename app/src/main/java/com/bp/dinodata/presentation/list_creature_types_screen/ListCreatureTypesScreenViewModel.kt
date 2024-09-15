package com.bp.dinodata.presentation.list_creature_types_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bp.dinodata.data.CreatureType
import com.bp.dinodata.data.ICreatureTypeInfo
import com.bp.dinodata.repo.ICreatureTypeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ListCreatureTypesScreenViewModel @Inject constructor(
    @set:Inject var creatureTypesRepository: ICreatureTypeRepository
): ViewModel(), IListCreatureTypesScreenViewModel {

    private val typeInfoMap = creatureTypesRepository.getCreatureTypeInfoMapFlow().stateIn(
        viewModelScope, SharingStarted.Lazily, emptyMap()
    )

    override fun getCreatureTypeInfoMapState(): StateFlow<Map<CreatureType, ICreatureTypeInfo>> {
        return typeInfoMap
    }
}