package com.bp.dinodata.use_cases

import com.bp.dinodata.data.Genus
import com.bp.dinodata.repo.GenusRepository
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.Flow

class GenusUseCases(
    private val genusRepository: GenusRepository
) {
    fun getGenusByName(genusName: String): Flow<Genus?> = genusRepository.getGenus(genusName)
    fun getGeneraAsList(): Flow<List<Genus>> = genusRepository.getGeneraFlow()

    suspend fun getNextPageOfGenera(
        startAfter: String? = null,
        callback: (List<Genus>) -> Unit,
        onException: (FirebaseFirestoreException) -> Unit
    ) {
        genusRepository.addGeneraListener(
            startAfterName = startAfter,
            callback=callback,
            onError=onException
        )
    }
}

