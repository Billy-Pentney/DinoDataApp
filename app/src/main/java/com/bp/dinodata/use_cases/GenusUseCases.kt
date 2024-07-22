package com.bp.dinodata.use_cases

import android.util.Log
import androidx.compose.ui.text.TextRange
import com.bp.dinodata.data.IResultsByLetter
import com.bp.dinodata.data.ResultsByLetter
import com.bp.dinodata.data.genus.GenusWithPrefs
import com.bp.dinodata.data.genus.ILocalPrefs
import com.bp.dinodata.data.genus.IGenusWithImages
import com.bp.dinodata.data.genus.IGenusWithPrefs
import com.bp.dinodata.data.search.GenusSearch
import com.bp.dinodata.presentation.list_genus.ISearchBarUiState
import com.bp.dinodata.presentation.list_genus.ListGenusUiState
import com.bp.dinodata.repo.IGenusRepository
import com.bp.dinodata.repo.ILocalPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class GenusUseCases(
    private val genusRepository: IGenusRepository,
    private val localPrefRepository: ILocalPreferencesRepository
) {
    fun getGenusByNameFlow(genusName: String): Flow<IGenusWithImages?> {
        return genusRepository.getGenusWithImagesFlow(genusName)
    }

    fun getGenusLocationsFlow(): Flow<List<String>> {
        return genusRepository.getLocationsFlow()
    }

    fun getGenusTaxaFlow(): Flow<List<String>> {
        return genusRepository.getAllTaxaFlow()
    }

    fun getGenusPrefsFlow(currentGenusName: String): Flow<ILocalPrefs?> {
        return localPrefRepository.getPrefsFlow(currentGenusName)
    }

    suspend fun updateColor(genusName: String, colorName: String?) {
        localPrefRepository.updateColorForGenus(genusName, colorName)
    }

    suspend fun getAllColors(): List<String> {
        return localPrefRepository.getAllColors().map { it.name }
    }

    fun getGenusWithPrefsFlow(): Flow<List<IGenusWithPrefs>?> {
        val genera = genusRepository.getAllGeneraFlow()
        val prefs = localPrefRepository.getGenusLocalPrefsFlow()

        return genera.combine(prefs) { generaList, prefsMap ->
            generaList?.map {
                val name = it.getName()
                GenusWithPrefs(it, prefsMap[name])
            }
        }
    }

    fun getGenusWithPrefsByLetterFlow(): Flow<IResultsByLetter<IGenusWithPrefs>?> {
        return getGenusWithPrefsFlow().map {
            it?.let { ResultsByLetter(it) }
        }
    }
}

