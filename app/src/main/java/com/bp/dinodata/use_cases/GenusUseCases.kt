package com.bp.dinodata.use_cases

import com.bp.dinodata.data.Genus
import com.bp.dinodata.repo.GenusPageResult
import com.bp.dinodata.repo.GenusRepository
import kotlinx.coroutines.flow.Flow

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

    fun getAllGenera(
        callback: (List<Genus>?) -> Unit,
        onError: (Exception) -> Unit
    ) {
        genusRepository.getAllGenera(callback, onError)
    }

    fun getNextPageOfGenera(
        startAfter: String? = null,
        callback: (GenusPageResult) -> Unit,
        onException: (Exception) -> Unit
    ) {
        genusRepository.getNextPage(
            startAfter=startAfter,
            callback = callback,
            onException = onException
        )
    }
}

