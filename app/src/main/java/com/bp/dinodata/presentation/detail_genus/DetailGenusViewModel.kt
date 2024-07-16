package com.bp.dinodata.presentation.detail_genus

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bp.dinodata.data.Genus
import com.bp.dinodata.presentation.LoadState
import com.bp.dinodata.use_cases.GenusUseCases
import com.bp.dinodata.use_cases.AudioPronunciationUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailGenusViewModel @Inject constructor(
    handle: SavedStateHandle,
    @set:Inject var genusUseCases: GenusUseCases,
    @set:Inject var audioPronunciationUseCases: AudioPronunciationUseCases
): ViewModel() {

    companion object {
        const val GENUS_KEY = "genusName"
        const val LOG_TAG = "DetailGenusViewModel"
    }

    private val _loadState: MutableState<LoadState> = mutableStateOf(LoadState.NotLoading)
    private var currentGenusName: String = checkNotNull(handle[GENUS_KEY])

    // Stores the details for the Genus being shown
    private var _visibleGenus: MutableStateFlow<Genus?> = MutableStateFlow(null)

    private var _titleCardExpanded: MutableState<Boolean> = mutableStateOf(true)

    init {
        viewModelScope.launch {
            _loadState.value = LoadState.InProgress
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
            _loadState.value = LoadState.Loaded
        }
    }

    fun getVisibleGenus(): StateFlow<Genus?> = _visibleGenus

    fun getTitleCardExpandedState(): State<Boolean> = _titleCardExpanded

    fun onEvent(event: DetailGenusUiEvent) {
        when (event) {
            is DetailGenusUiEvent.ChangeTitleCardExpansion -> {
                _titleCardExpanded.value = event.expanded
            }
            DetailGenusUiEvent.PlayNamePronunciation -> playPronunciationFile()
        }
    }

    private fun playPronunciationFile() {
        val name = _visibleGenus.value?.getName()
        Log.i(LOG_TAG, "Play pronunciation for $currentGenusName")

        if (name == null) {
            Log.d(LOG_TAG, "Could not retrieve genus name for TTS")
            return
        }

        audioPronunciationUseCases.playPrerecordedAudio(
            name,
            callback = { success ->
                if (success) {
                    Log.d(LOG_TAG, "Successfully played \'$name\' pronunciation")
                }
                else {
                    Log.d(LOG_TAG, "An error occurred when playing audio for \'$name\'")
                }
            }
        )
    }

    fun getLoadState(): State<LoadState> {
        return _loadState
    }
}




sealed class DetailGenusUiEvent {

    data class ChangeTitleCardExpansion(val expanded: Boolean): DetailGenusUiEvent()

    data object PlayNamePronunciation: DetailGenusUiEvent()

}