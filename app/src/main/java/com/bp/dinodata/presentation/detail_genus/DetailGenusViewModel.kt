package com.bp.dinodata.presentation.detail_genus

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bp.dinodata.data.genus.DetailedGenus
import com.bp.dinodata.data.genus.IDetailedGenus
import com.bp.dinodata.data.genus.IGenusPrefs
import com.bp.dinodata.data.genus.IGenusWithImages
import com.bp.dinodata.presentation.LoadState
import com.bp.dinodata.use_cases.GenusUseCases
import com.bp.dinodata.use_cases.AudioPronunciationUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
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

//    private val _loadState: MutableState<LoadState> = mutableStateOf(LoadState.InProgress)
    private var currentGenusName: String = checkNotNull(handle[GENUS_KEY])

    // Stores the details for the Genus being shown
    private var _genusWithImages: Flow<IGenusWithImages?> = genusUseCases.getGenusByNameFlow(currentGenusName)
    private var _genusPrefs: Flow<IGenusPrefs?> = genusUseCases.getGenusPrefsFlow(currentGenusName)
    private var _genusDetail: StateFlow<DetailedGenus?> = MutableStateFlow(null)

    private var _uiState: MutableState<DetailScreenUiState> = mutableStateOf(
        DetailScreenUiState(currentGenusName, loadState = LoadState.InProgress)
    )

    init {

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                listOfColors = genusUseCases.getAllColors()
            )
        }

        _genusDetail = combine(_genusWithImages, _genusPrefs) { genusWithImages, genusPrefs ->
            genusWithImages?.let {
                DetailedGenus(genusWithImages, genusPrefs)
            }
        }.stateIn(viewModelScope, SharingStarted.Lazily, null)

        viewModelScope.launch {
            _genusDetail.collectLatest {
                val loadState =
                    if (it != null) {
                        LoadState.Loaded
                    } else {
                        LoadState.Error("no genus detail data!")
                    }

                _uiState.value = _uiState.value.copy(
                    genusData = it,
                    loadState = loadState,
                    selectedColorName = it?.getSelectedColorName()
                )
            }
        }
    }

    fun onEvent(event: DetailGenusUiEvent) {
        when (event) {
            DetailGenusUiEvent.PlayNamePronunciation -> playPronunciationFile()
            is DetailGenusUiEvent.SelectColor -> {
                _uiState.value = _uiState.value.copy(
                    selectedColorName = event.colorName
                )
                viewModelScope.launch {
                    genusUseCases.updateColor(
                        currentGenusName,
                        event.colorName
                    )
                }
            }
            is DetailGenusUiEvent.ShowColorSelectDialog -> {
                _uiState.value = _uiState.value.copy(
                    colorSelectDialogVisibility = event.visible
                )
            }
        }
    }

    fun getUiState(): State<DetailScreenUiState> = _uiState

    private fun playPronunciationFile() {
        val name = currentGenusName
        Log.i(LOG_TAG, "Play pronunciation for $currentGenusName")

        audioPronunciationUseCases.playPrerecordedAudio(
            name,
            callback = { success ->
                if (success) {
                    Log.d(LOG_TAG, "Successfully played \'$name\' pronunciation")
                }
                else {
                    Log.d(LOG_TAG, "An error occurred when playing audio for \'$name\'")
                    _uiState.value = _uiState.value.copy(
                        canPlayPronunciationAudio = false
                    )
                }
            }
        )
    }
}


sealed class DetailGenusUiEvent {
    data object PlayNamePronunciation: DetailGenusUiEvent()
    data class ShowColorSelectDialog(val visible: Boolean): DetailGenusUiEvent()
    data class SelectColor(val colorName: String?): DetailGenusUiEvent()
}

data class DetailScreenUiState(
    val genusName: String,
    val loadState: LoadState = LoadState.NotLoading,
    val genusData: IDetailedGenus? = null,
    val colorSelectDialogVisibility: Boolean = false,
    val canPlayPronunciationAudio: Boolean = true,
    val listOfColors: List<String> = emptyList(),
    val selectedColorName: String? = null
)