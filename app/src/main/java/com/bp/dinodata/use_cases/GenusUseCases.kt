package com.bp.dinodata.use_cases

import android.util.Log
import com.bp.dinodata.data.IResultsByLetter
import com.bp.dinodata.data.ResultsByLetter
import com.bp.dinodata.data.genus.GenusWithPrefs
import com.bp.dinodata.data.genus.IGenusWithImages
import com.bp.dinodata.data.genus.IGenusWithPrefs
import com.bp.dinodata.data.genus.ILocalPrefs
import com.bp.dinodata.data.taxon.IMutableTaxon
import com.bp.dinodata.data.taxon.ITaxon
import com.bp.dinodata.data.taxon.MutableTaxon
import com.bp.dinodata.data.taxon.Taxon
import com.bp.dinodata.presentation.DataState
import com.bp.dinodata.repo.IGenusRepository
import com.bp.dinodata.repo.ILocalPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class GenusUseCases(
    private val genusRepository: IGenusRepository,
    private val localPrefRepository: ILocalPreferencesRepository
) {
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

    fun getGenusLocationsFlow(): Flow<List<String>> {
        return genusRepository.getLocationsFlow()
    }

    fun getGenusTaxaFlow(): Flow<List<String>> {
        return genusRepository.getAllTaxaFlow()
    }

    fun getGenusPrefsFlow(currentGenusName: String): Flow<ILocalPrefs?> {
        return localPrefRepository.getPrefsFlow(currentGenusName)
    }

    suspend fun updateColor(genusName: String, colorName: String?) {
        localPrefRepository.updateColorForGenus(genusName, colorName)
    }

    suspend fun getAllColors(): List<String> {
        return localPrefRepository.getAllColors().map { it.name }
    }

    fun getGenusWithPrefsFlow(): Flow<List<IGenusWithPrefs>?> {
        val genera = genusRepository.getAllGeneraFlow()
        val prefs = localPrefRepository.getGenusLocalPrefsFlow()

        return genera.combine(prefs) { generaList, prefsMap ->
            generaList?.map {
                val name = it.getName()
                GenusWithPrefs(it, prefsMap[name])
            }
        }
    }

    fun getGenusWithPrefsByLetterFlow(): Flow<DataState<IResultsByLetter<IGenusWithPrefs>>> {
        return getGenusWithPrefsFlow().map {
            if (it != null) {
                DataState.Success(ResultsByLetter(it))
            }
            else {
                DataState.Failed("no data retrieved")
            }
        }
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


    fun getTaxonomyList(): Flow<DataState<List<ITaxon>>> {
        return genusRepository.getAllGeneraFlow().map { genusList ->
            if (genusList == null) {
                return@map DataState.Failed("no genera")
            }

            val taxonMap: MutableMap<String, IMutableTaxon> = mutableMapOf()
            val nonRootNames = mutableSetOf<String>()

            genusList.forEach { genus ->
                // A list of strings, taxon names
                val lineage = genus.getListOfTaxonomy()

                // All except the first taxon are not the root
                nonRootNames.addAll(lineage.drop(1))

                lineage.forEachIndexed { i, taxonName ->
                    // Retrieve from the cache if it exists, or make a new object
                    val taxon: IMutableTaxon = taxonMap[taxonName]
                        ?: MutableTaxon(taxonName)

                    if (i < lineage.size-1) {
                        // This taxon has a child
                        val childName = lineage[i+1]
                        val child = taxonMap[childName] ?: MutableTaxon(childName)
                        if (childName !in taxonMap.keys) {
                            // If this child doesn't exist, then we add it
                            taxonMap[childName] = child
                        }
                        taxon.addChild(child)
                    }
                    else {
                        // No direct children for this taxon, but the genus is the leaf
                        taxon.addChild(genus)
                    }

                    // Save the new taxon to the map
                    taxonMap[taxonName] = taxon
                }
            }

            // Get the taxa which have no parents (i.e. the roots)
            val rootTaxa = taxonMap.filter { it.key !in nonRootNames }.map { it.value }
            DataState.Success(rootTaxa)
        }
    }
}

