package com.bp.dinodata.use_cases

import com.bp.dinodata.data.Genus
import com.bp.dinodata.repo.GenusRepository
import kotlinx.coroutines.flow.Flow

class GetAllGenera(
    val genusRepository: GenusRepository
) {
    operator fun invoke(): Flow<List<Genus>> {
        return genusRepository.getGeneraFlow()
    }
}