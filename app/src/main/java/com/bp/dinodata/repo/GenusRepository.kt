package com.bp.dinodata.repo

import android.util.Log
import com.bp.dinodata.data.ImageDataProcessingUtils.mapToImageUrlDTOs
import com.bp.dinodata.data.MultiImageUrlData
import com.bp.dinodata.data.genus.GenusBuilder
import com.bp.dinodata.data.genus.GenusWithImages
import com.bp.dinodata.data.genus.IGenus
import com.bp.dinodata.data.genus.IGenusWithImages
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

class GenusRepository @Inject constructor(
    private val connectivityChecker: IConnectionChecker,
    private val genusCollection: CollectionReference,
    private val genusImageCollection: CollectionReference
) : IGenusRepository {
    companion object {
        const val DEFAULT_PAGE_SIZE: Long = 25
        const val TAG = "GenusRepository"
    }

    private var generaByName: Map<String, IGenus> = emptyMap()

    private val hasNetConnectivity
        get() = connectivityChecker.hasNetworkAccess()

    /**
     * Get a flow which emits a list of all known genus data, as retrieved
     * from the firebase collection.
     * If such data does not exist, then null is returned.
     */
    override fun getAllGeneraFlow(): Flow<List<IGenus>?> {
        return genusCollection
            .orderBy("name")
            .snapshots()
            .map { snapshot ->
                val lastVisible = snapshot.documents.last()
                Log.d(TAG, "Converting documents up to ${lastVisible?.id}")

                // Convert the results to Genus objects
                snapshot.mapNotNull { doc ->
                    GenusBuilder.fromDict(doc.data)?.build()
                }.also {
                    if (it.isNotEmpty()) {
                        generaByName = it.associateBy { genus -> genus.getName() }
                    }
                }
            }
    }

    /**
     * Get a flow which emits a list of all unique locations contained in the genus data.
     */
    override fun getLocationsFlow(): Flow<List<String>> = getAllGeneraFlow().map { genera ->
        genera?.fold(mutableSetOf<String>()) { locationSet, genus ->
            val locations = genus.getLocations()
            locationSet.addAll(locations)
            locationSet
        }?.toList() ?: emptyList()
    }

    /**
     * Get a flow which emits a list of all unique taxa in the genus data.
     */
    override fun getAllTaxaFlow(): Flow<List<String>> {
        return getAllGeneraFlow().map { genera ->
            genera?.fold(mutableSetOf<String>()) { taxaSet, genus ->
                val taxa = genus.getListOfTaxonomy()
                taxaSet.addAll(taxa)
                taxaSet
            }?.toList() ?: emptyList()
        }
    }

    private fun getGenusNameFlow(
        genusName: String,
        onFailure: () -> Unit = {}
    ): Flow<IGenus?> {
        val firebaseFlow = genusCollection
            .document(genusName)
            .snapshots().map { snapshot ->
                Log.d(TAG, "Successfully got result from Firebase!")
                val genusBuilder = snapshot.data?.let { GenusBuilder.fromDict(it) }

                if (genusBuilder != null) {
                    genusBuilder.build()
                } else {
                    Log.d(TAG, "Unable to construct data object for genus $genusName.")
                    onFailure()
                    null
                }
            }

        return firebaseFlow
    }

    /**
     * Get a flow which may emit an image-data map for the given genus name.
     * If no images are known or the target image file cannot be parsed,
     * then null is emitted by the flow. Otherwise, the flow emits a map
     * from the creature name (e.g. species) to a collection of relevant images.
     */
    private fun getGenusImagesFlow(genusName: String): Flow<Map<String, MultiImageUrlData>?> {
        return genusImageCollection
            .document(genusName)
            .snapshots()
            .map { snapshot ->
                mapToImageUrlDTOs(snapshot.data)
            }
    }

    /**
     * Get a flow which emits a GenusWithImages object for the genus with the given name,
     * if one exists. This object contains the genus data (i.e. stats and information),
     * and references to their images, if any are known.
     */
    override fun getGenusWithImagesFlow(genusName: String): Flow<IGenusWithImages?> {
        val dataFlow = getGenusNameFlow(genusName)
        val imagesFlow = getGenusImagesFlow(genusName)

        return combine(dataFlow, imagesFlow) { genusData, genusImages ->
            genusData?.let {
                GenusWithImages(genusData, genusImages)
            }
        }
    }

    private var locationsList: List<String> = emptyList()

    override suspend fun getLocations(): List<String> {
        if (locationsList.isNotEmpty()) {
            return locationsList
        }
        return getLocationsFlow().first().also {
            locationsList = it
        }
    }

    private var generaNamesFlow: StateFlow<List<String>>? = null

    override suspend fun getAllGeneraNames(): List<String> {
        return if (generaNamesFlow == null) {
            getAllGeneraFlow()
                .map { list -> list?.map { it.getName().lowercase() } ?: emptyList() }
                .stateIn(CoroutineScope(Dispatchers.IO))
                .also { generaNamesFlow = it }
                .value
        } else {
            generaNamesFlow!!.value
        }
    }
}


