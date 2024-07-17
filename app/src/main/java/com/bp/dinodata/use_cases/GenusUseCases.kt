package com.bp.dinodata.use_cases

import com.bp.dinodata.data.GenusSearch
import com.bp.dinodata.data.genus.Genus
import com.bp.dinodata.data.genus.IGenus
import com.bp.dinodata.data.ResultsByLetter
import com.bp.dinodata.data.filters.IFilter
import com.bp.dinodata.repo.IGenusRepository

class GenusUseCases(
    private val genusRepository: IGenusRepository
) {
//    fun getAllGenera(
//        callback: (List<Genus>) -> Unit,
//        onError: (Exception) -> Unit
//    ) {
//        genusRepository.getAllGenera(
//            callback,
//            onError
//        )
//    }

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
        callback: (Genus?) -> Unit,
        onFailure: () -> Unit,
    ) {
        genusRepository.getGenus(genusName, callback, onFailure)
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

