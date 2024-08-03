package com.bp.dinodata.presentation.detail_genus

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bp.dinodata.data.genus.DetailedGenus
import com.bp.dinodata.data.genus.IGenusWithImages
import com.bp.dinodata.data.genus.ILocalPrefs
import com.bp.dinodata.presentation.DataState
import com.bp.dinodata.presentation.map
import com.bp.dinodata.repo.AudioPlayStatus
import com.bp.dinodata.use_cases.AudioPronunciationUseCases
import com.bp.dinodata.use_cases.GenusUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
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

    private var currentGenusName: String = checkNotNull(handle[GENUS_KEY])

    // Stores the details for the Genus being shown
    private var _genusWithImages: Flow<DataState<IGenusWithImages>>
        = genusUseCases.getGenusByNameFlow(currentGenusName)
    // Store the local preferences (color/favourite.. etc.) for the genus, if they exist
    private var _genusPrefs: Flow<ILocalPrefs?>
        = genusUseCases.getGenusPrefsFlow(currentGenusName)
    // Combined unit for this genus
    private var _genusDetail: StateFlow<DataState<out DetailedGenus>>
        = MutableStateFlow(DataState.Idle())

    private var _uiState: MutableState<DetailScreenUiState> = mutableStateOf(
        DetailScreenUiState(currentGenusName)
    )

    private var toastFlow: MutableSharedFlow<String> = MutableSharedFlow()

    init {
        _genusDetail = combine(_genusWithImages, _genusPrefs) { genusWithImages, genusPrefs ->
            genusWithImages.map { data ->
                DetailedGenus(data, genusPrefs)
            }
        }.stateIn(viewModelScope, SharingStarted.Lazily, DataState.LoadInProgress())

        viewModelScope.launch {
            _genusDetail.collectLatest {
                _uiState.value = _uiState.value.copy(
                    genusData = it
                )
            }
        }
    }

    private fun showToast(message: String) {
        viewModelScope.launch {
            toastFlow.emit(message)
        }
    }

    fun onEvent(event: DetailGenusUiEvent) {
        when (event) {
            DetailGenusUiEvent.PlayNamePronunciation -> playPronunciationFile()
            is DetailGenusUiEvent.SelectColor -> {
                viewModelScope.launch {
                    genusUseCases.updateColor(
                        currentGenusName,
                        event.colorName
                    )
                }
            }
            is DetailGenusUiEvent.ShowColorSelectDialog -> {
                _uiState.value = _uiState.value.copy(
                    colorSelectDialogVisible = event.visible
                )
            }
            is DetailGenusUiEvent.ToggleItemFavouriteStatus -> {
                viewModelScope.launch {
                    genusUseCases.updateFavouriteStatus(currentGenusName, event.isFavourite)
                }
            }
            is DetailGenusUiEvent.SetPreferencesCardExpansion -> {
                _uiState.value = _uiState.value.copy(
                    preferencesCardExpanded = event.expanded
                )
            }
            is DetailGenusUiEvent.UpdateVisibleImageIndex -> {
                _uiState.value = _uiState.value.tryUpdateImageIndex(event.increment)
                viewModelScope.launch {
                    genusUseCases.updateSelectedImageIndex(
                        currentGenusName,
                        _uiState.value.getCurrentImageIndex()
                    )
                }
            }
        }
    }

    fun getUiState(): State<DetailScreenUiState> = _uiState

    /**
     * Invoke and handle the use-case of playing the Text-to-speech pronunciation
     * of this genus' name.
     */
    private fun playPronunciationFile() {
        val name = currentGenusName
        Log.i(LOG_TAG, "Play pronunciation for $currentGenusName")

        audioPronunciationUseCases.playPrerecordedAudio(
            genusName = name,
            onCompletion = { status ->
                when (status) {
                    is AudioPlayStatus.MediaPlayerError -> {
                        Log.d(LOG_TAG, "A MediaPlayer error (${status.type}, ${status.extra}) occurred during playback for \'$name\'")
                        _uiState.value = _uiState.value.copy(
                            canPlayPronunciationAudio = false
                        )
                    }
                    AudioPlayStatus.MissingNetwork -> {
                        Log.d(LOG_TAG, "Unable to play audio for \'$name\' due to no network!")
                        showToast("This file is not available offline. Please connect to a network!")
                        _uiState.value = _uiState.value.copy(
                            canPlayPronunciationAudio = false
                        )
                    }
                    AudioPlayStatus.FileNotFound -> {
                        Log.d(LOG_TAG, "TTS Audio for genus \'$name\' was not found!")
                        showToast("The pronunciation audio is not currently available")
                        _uiState.value = _uiState.value.copy(
                            canPlayPronunciationAudio = false
                        )
                    }
                    AudioPlayStatus.Success -> {
                        Log.d(LOG_TAG, "Successfully played pronunciation audio for '$name'")
                    }
                    AudioPlayStatus.UnknownError -> {
                        Log.d(LOG_TAG, "An unknown error prevented audio retrieval/playback")
                    }
                }
            }
        )
    }

    fun getToastFlow(): Flow<String> = toastFlow
}


sealed class DetailGenusUiEvent {
    data object PlayNamePronunciation: DetailGenusUiEvent()
    data class ShowColorSelectDialog(val visible: Boolean): DetailGenusUiEvent()
    data class SelectColor(val colorName: String?): DetailGenusUiEvent()
    data class ToggleItemFavouriteStatus(val isFavourite: Boolean): DetailGenusUiEvent()
    data class SetPreferencesCardExpansion(val expanded: Boolean): DetailGenusUiEvent()

    data class UpdateVisibleImageIndex(val increment: Int): DetailGenusUiEvent()
}

