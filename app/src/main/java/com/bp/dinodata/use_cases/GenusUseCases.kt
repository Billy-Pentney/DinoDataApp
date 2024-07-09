package com.bp.dinodata.use_cases

import com.bp.dinodata.data.Genus
import com.bp.dinodata.repo.PageResult
import com.bp.dinodata.repo.GenusRepository

class GenusUseCases(
    private val genusRepository: GenusRepository
) {
    fun getGenusByName(
        genusName: String,
        callback: (Genus?) -> Unit,
        onFailure: () -> Unit,
    ) {
        genusRepository.getGenus(genusName, callback, onFailure)
    }

//    fun getAllGenera(
//        callback: (List<Genus>?) -> Unit,
//        onError: (Exception) -> Unit
//    ) {
//        genusRepository.getAllGenera(callback, onError)
//    }

    fun getNextPageOfGenera(
        startAfter: String? = null,
        callback: (PageResult<Genus>) -> Unit,
        onException: (Exception) -> Unit
    ) {
        genusRepository.getNextPage(
            startAfter = startAfter,
            callback = callback,
            onException = onException
        )
    }
}

