package com.bp.dinodata.use_cases

import com.bp.dinodata.data.Genus
import com.bp.dinodata.repo.GenusRepository
import kotlinx.coroutines.flow.Flow

class GetGenusByName(
    private val genusRepository: GenusRepository
) {

}
