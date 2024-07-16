package com.bp.dinodata.use_cases

import com.bp.dinodata.data.Genus
import com.bp.dinodata.data.IGenus
import com.bp.dinodata.data.ResultsByLetter
import com.bp.dinodata.repo.PageResult
import com.bp.dinodata.repo.GenusRepository
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

