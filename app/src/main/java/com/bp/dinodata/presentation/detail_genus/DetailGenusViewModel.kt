package com.bp.dinodata.presentation.detail_genus

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bp.dinodata.data.genus.DetailedGenus
import com.bp.dinodata.data.genus.Genus
import com.bp.dinodata.data.genus.GenusWithImages
import com.bp.dinodata.data.genus.IDetailedGenus
import com.bp.dinodata.data.genus.IGenus
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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.last
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

    private val _loadState: MutableState<LoadState> = mutableStateOf(LoadState.InProgress)
    private var currentGenusName: String = checkNotNull(handle[GENUS_KEY])

    private var _titleCardExpanded: MutableState<Boolean> = mutableStateOf(true)

    // Stores the details for the Genus being shown
    private var _genusWithImages: Flow<IGenusWithImages?> = genusUseCases.getGenusByNameFlow(currentGenusName)
    private var _genusPrefs: Flow<IGenusPrefs?> = genusUseCases.getGenusPrefsFlow(currentGenusName)
    private var _genusDetail: StateFlow<DetailedGenus?> = MutableStateFlow(null)

    init {
        _genusDetail = combine(_genusWithImages, _genusPrefs) { genusWithImages, genusPrefs ->
            val genusDetail = genusWithImages?.let {
                DetailedGenus(genusWithImages, genusPrefs)
            }

            _loadState.value =
                when (genusDetail != null) {
                    true -> LoadState.Loaded
                    false -> LoadState.Error("No genus data found!")
                }

            genusDetail
        }.stateIn(viewModelScope, SharingStarted.Lazily, null)
    }

    fun getVisibleGenus(): StateFlow<IDetailedGenus?> = _genusDetail

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
        val name = currentGenusName
        Log.i(LOG_TAG, "Play pronunciation for $currentGenusName")

//        if (name == null) {
//            Log.d(LOG_TAG, "Could not retrieve genus name for TTS")
//            return
//        }

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

    fun getLoadState(): State<LoadState> = _loadState
}




sealed class DetailGenusUiEvent {

    data class ChangeTitleCardExpansion(val expanded: Boolean): DetailGenusUiEvent()

    data object PlayNamePronunciation: DetailGenusUiEvent()

}