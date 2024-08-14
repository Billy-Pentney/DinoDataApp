package com.bp.dinodata.use_cases

import com.bp.dinodata.data.Formation
import com.bp.dinodata.data.genus.DetailedGenus
import com.bp.dinodata.data.genus.IDetailedGenus
import com.bp.dinodata.data.genus.IGenusWithImages
import com.bp.dinodata.data.genus.ILocalPrefs
import com.bp.dinodata.presentation.DataState
import com.bp.dinodata.presentation.map
import com.bp.dinodata.repo.FormationsRepository
import com.bp.dinodata.repo.IFormationsRepository
import com.bp.dinodata.repo.IGenusRepository
import com.bp.dinodata.repo.ILocalPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class GenusDetailUseCases(
    private val genusRepository: IGenusRepository,
    private val localPrefRepository: ILocalPreferencesRepository,
    private val formationsRepository: IFormationsRepository
) {
    fun getDetailedGenus(genusName: String): Flow<DataState<out DetailedGenus>> {
        val genusWithImagesFlow = getGenusByNameFlow(genusName)
        val genusPrefsFlow = getGenusPrefsFlow(genusName)
        val formationsFlow = formationsRepository.getFormationsByNameFlow()

        return combine(genusWithImagesFlow, genusPrefsFlow, formationsFlow) {
            genusWithImages, genusPrefs, formationsMap ->
            genusWithImages.map { genusData ->
                val formationNames = genusData.getFormationNames()
                val formations = formationNames.map { fName ->
                    // Try to look-up the formation, but if we fail to find it,
                    // make a new formation with the given name only
                    formationsMap[fName] ?: Formation(fName)
                }
                DetailedGenus(genusData, genusPrefs, formations)
            }
        }
    }

    fun getGenusByNameFlow(genusName: String): Flow<DataState<IGenusWithImages>> {
        return genusRepository.getGenusWithImagesFlow(genusName).map {
            if (it == null) {
                DataState.Failed("Null data")
            }
            else {
                DataState.Success(it)
            }
        }
    }

    fun getGenusPrefsFlow(currentGenusName: String): Flow<ILocalPrefs?> {
        return localPrefRepository.getPrefsFlow(currentGenusName)
    }

    suspend fun updateColor(genusName: String, colorName: String?) {
        localPrefRepository.updateColorForGenus(genusName, colorName)
    }

    suspend fun updateFavouriteStatus(currentGenusName: String, favourite: Boolean) {
        localPrefRepository.updateFavouriteStatus(currentGenusName, favourite)
    }

    suspend fun updateSelectedImageIndex(
        genusName: String,
        newImageIndex: Int
    ) {
        localPrefRepository.updateSelectedImageIndex(genusName, newImageIndex)
    }

}