package com.bp.dinodata.use_cases

import com.bp.dinodata.data.GenusSearch
import com.bp.dinodata.data.genus.IGenus
import com.bp.dinodata.data.ResultsByLetter
import com.bp.dinodata.data.genus.GenusWithImages
import com.bp.dinodata.data.genus.IGenusWithImages
import com.bp.dinodata.repo.IGenusRepository
import com.bp.dinodata.repo.ILocalPreferencesRepository

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

    fun getGenusByName(
        genusName: String,
        onCompletion: (IGenusWithImages?) -> Unit,
        onFailure: () -> Unit,
    ) {
        genusRepository.getGenusWithImages(genusName, onCompletion, onFailure)
    }

    suspend fun getGenusColor(genusName: String): String? {
        return localPrefRepository.getColorForGenus(genusName)
    }

    fun generateSearchFromQuery(query: String, capitalSensitive: Boolean): GenusSearch {
        return GenusSearch.constructFromQuery(query, capitalSensitive)
    }

//    fun getNextPageOfGenera(
//        startAfter: String? = null,
//        callback: (PageResult<Genus>) -> Unit,
//        onException: (Exception) -> Unit
//    ) {
//        genusRepository.getNextPage(
//            startAfter = startAfter,
//            callback = callback,
//            onException = onException
//        )
//    }
}

