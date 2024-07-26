package com.bp.dinodata.presentation.detail_genus

import com.bp.dinodata.data.genus.IDetailedGenus
import com.bp.dinodata.presentation.DataState


interface ImageIndexControlState {
    fun canIncreaseImageIndex(): Boolean
    fun canDecreaseImageIndex(): Boolean
    fun getCurrentImageIndex(): Int
}

interface IMutableImageIndexControlState: ImageIndexControlState {
    fun tryIncreaseImageIndex(): IMutableImageIndexControlState
    fun tryDecreaseImageIndex(): IMutableImageIndexControlState
    fun tryUpdateImageIndex(increment: Int): IMutableImageIndexControlState
}

interface IDetailScreenUiState: IMutableImageIndexControlState {
    fun getGenusData(): IDetailedGenus?
}

data class DetailScreenUiState(
    val genusName: String,
    val genusData: DataState<out IDetailedGenus> = DataState.Idle(),
    val colorSelectDialogVisible: Boolean = false,
    val canPlayPronunciationAudio: Boolean = true,
    val preferencesCardExpanded: Boolean = false,
    val listOfColors: List<String> = emptyList(),
    private val currentImageIndex: Int = 0
): IDetailScreenUiState {
    private val numImages = getGenusData()?.getNumDistinctImages() ?: 0

    override fun getGenusData(): IDetailedGenus? {
        if (genusData is DataState.Success) {
            return genusData.data
        }
        return null
    }

    override fun tryIncreaseImageIndex(): DetailScreenUiState {
        if (canIncreaseImageIndex()) {
            return this.copy(currentImageIndex = currentImageIndex+1)
        }
        return this
    }

    override fun tryDecreaseImageIndex(): DetailScreenUiState {
        if (canDecreaseImageIndex()) {
            return this.copy(currentImageIndex = currentImageIndex-1)
        }
        return this
    }

    override fun tryUpdateImageIndex(increment: Int): DetailScreenUiState {
        return when {
            increment > 0 -> tryIncreaseImageIndex()
            increment < 0 -> tryDecreaseImageIndex()
            else -> this
        }
    }

    override fun canIncreaseImageIndex(): Boolean = currentImageIndex < numImages - 1
    override fun canDecreaseImageIndex(): Boolean = currentImageIndex > 0
    override fun getCurrentImageIndex(): Int = currentImageIndex

}