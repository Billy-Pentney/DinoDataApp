package com.bp.dinodata.use_cases

import com.bp.dinodata.data.GenusSearch
import com.bp.dinodata.data.genus.IGenus
import com.bp.dinodata.data.ResultsByLetter
import com.bp.dinodata.data.genus.IGenusPrefs
import com.bp.dinodata.data.genus.IGenusWithImages
import com.bp.dinodata.repo.IGenusRepository
import com.bp.dinodata.repo.ILocalPreferencesRepository
import kotlinx.coroutines.flow.Flow

class GenusUseCases(
    private val genusRepository: IGenusRepository,
    private val localPrefRepository: ILocalPreferencesRepository
) {
    fun getGeneraGroupedByLetter(
        callback: (ResultsByLetter<IGenus>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        genusRepository.getAllGenera(
            { callback(ResultsByLetter(it)) },
            onError
        )
    }

    fun getGenusByNameFlow(genusName: String): Flow<IGenusWithImages?> {
        return genusRepository.getGenusWithImagesFlow(genusName)
    }

    fun generateSearchFromQuery(query: String, capitalSensitive: Boolean): GenusSearch {
        return GenusSearch.constructFromQuery(query, capitalSensitive)
    }

    fun getGenusPrefsFlow(currentGenusName: String): Flow<IGenusPrefs?> {
        return localPrefRepository.getPrefsFlow(currentGenusName)
    }
}

