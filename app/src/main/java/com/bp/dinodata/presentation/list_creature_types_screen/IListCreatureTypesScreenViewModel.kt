package com.bp.dinodata.presentation.list_creature_types_screen

import androidx.compose.runtime.State
import com.bp.dinodata.data.CreatureType
import com.bp.dinodata.data.ICreatureTypeInfo
import kotlinx.coroutines.flow.StateFlow

interface IListCreatureTypesScreenViewModel {
    fun getCreatureTypeInfoMapState(): StateFlow<Map<CreatureType, ICreatureTypeInfo>>
}