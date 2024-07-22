package com.bp.dinodata.repo

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.bp.dinodata.data.genus.Genus
import com.bp.dinodata.data.genus.GenusBuilderImpl
import com.bp.dinodata.data.ImageUrlData.mapToImageUrlDTOs
import com.bp.dinodata.data.MultiImageUrlData
import com.bp.dinodata.data.genus.GenusWithImages
import com.bp.dinodata.data.genus.IGenus
import com.bp.dinodata.data.genus.IGenusWithImages
import com.google.firebase.firestore.CollectionReference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class GenusRepository @Inject constructor(
    private val connectivityManager: ConnectivityManager,
    private val genusCollection: CollectionReference,
    private val genusImageCollection: CollectionReference
) : IGenusRepository {
    companion object {
        const val DEFAULT_PAGE_SIZE: Long = 25
        const val TAG = "GenusRepository"
    }

    private var generaByName: Map<String, IGenus> = emptyMap()

    private val hasNetConnectivity: Boolean
        get() = connectivityManager.getNetworkCapabilities(
            connectivityManager.activeNetwork
        )?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) ?: false

    override fun getAllGenera(
        callback: (List<Genus>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val first = genusCollection
            .orderBy("name")

        Log.d(TAG, "Request for all genera!")

        first.get()
            .addOnSuccessListener { snapshot ->
                val lastVisible = snapshot.documents.last()
                Log.d(TAG, "Converting documents up to ${lastVisible?.id}")

                // Convert the results to Genus objects
                val genera = snapshot.mapNotNull { doc ->
                    GenusBuilderImpl.fromDict(doc.data)?.build()
                }
                if (genera.isNotEmpty()) {
                    generaByName = genera.associateBy { it.getName() }
                }

                callback(genera)
            }
            .addOnFailureListener { onError(it) }
    }

    override fun getAllGeneraFlow(): Flow<List<IGenus>?> = flow {
        val snapshot = genusCollection.orderBy("name")
            .get()
            .await()

        val lastVisible = snapshot.documents.last()
        Log.d(TAG, "Converting documents up to ${lastVisible?.id}")

        // Convert the results to Genus objects
        val genera = snapshot.mapNotNull { doc ->
            GenusBuilderImpl.fromDict(doc.data)?.build()
        }
        if (genera.isNotEmpty()) {
            generaByName = genera.associateBy { it.getName() }
        }

        emit(genera)
    }


    /**
     * Attempt to retrieve data for the genus with the given name. This includes the image
     * data object which may have been omitted on the first pass.
     * If retrieval is successful, the callback is invoked with the created genus, or null
     * if the genus is invalid.
     * Otherwise, if an error occurred, onFailure is called.
     * */
    override fun getGenusWithImages(
        genusName: String,
        onCompletion: (IGenusWithImages?) -> Unit,
        onFailure: () -> Unit
    ) {
        // Attempt to use the pre-loaded data
        if (generaByName.containsKey(genusName)) {
            Log.d(TAG, "Image-request received for existing genus $genusName")
            val genusData = generaByName[genusName]!!

            if (genusData is GenusWithImages) {
                // Early-exit if we've already gotten these images
                onCompletion(genusData)
                return
            }
            else {
                getImagesForGenus(
                    genusName,
                    callback = { imageUrlMap ->
                        if (imageUrlMap != null) {
                            Log.d(TAG, "Retrieved images for existing genus $genusName")
                        } else {
                            Log.d(TAG, "Received null image data for existing genus $genusName")
                        }
                        val genusWithImageData = GenusWithImages(genusData, imageUrlMap)
                        onCompletion(genusWithImageData)
                    },
                    onFailure = {
                        // Return but without images
                        Log.d(TAG, "Failed to get images for existing genus $genusName")
                        onCompletion(GenusWithImages(genusData))
                    }
                )
            }
        }
        else if (hasNetConnectivity) {
            // Otherwise, fetch the genus from Firebase
            Log.d(TAG, "Image-request received for not-downloaded genus $genusName")

            genusCollection
                .document(genusName)
                .get()
                .addOnSuccessListener { snapshot ->
                    val genusBuilder = snapshot?.data?.let { GenusBuilderImpl.fromDict(it) }

                    if (genusBuilder != null) {
                        val genusData = genusBuilder.build()

                        // Now, get the images (if any)
                        getImagesForGenus(
                            genusName,
                            callback = { onCompletion(GenusWithImages(genusData, it)) },
                            onFailure = { onCompletion(GenusWithImages(genusData)) }
                        )
                    } else {
                        Log.d(TAG, "Unable to construct data object for genus $genusName.")
                        onFailure()
                    }
                }
                .addOnFailureListener { exc ->
                    Log.d(TAG, "Failed to retrieve genus data for $genusName.", exc)
                    onFailure()
                }
        }
    }

    private fun getGenusNameFlow(
        genusName: String,
        onFailure: () -> Unit = {}
    ): Flow<IGenus?> = flow {
        if (genusName in generaByName.keys) {
            emit(generaByName[genusName])
        }

        if (hasNetConnectivity) {
            val snapshot = genusCollection
                .document(genusName)
                .get()
                .await()

            val genusBuilder = snapshot?.data?.let { GenusBuilderImpl.fromDict(it) }

            if (genusBuilder != null) {
                val genus = genusBuilder.build()
                emit(genus)
            } else {
                Log.d(TAG, "Unable to construct data object for genus $genusName.")
                onFailure()
            }
        }
        else {
            Log.d(TAG, "Cannot retrieve genus data. No network connection")
        }
    }

    private fun getGenusImagesFlow(genusName: String): Flow<Map<String, MultiImageUrlData>?> = flow {
        val snapshot = genusImageCollection
            .document(genusName)
            .get()
            .await()

        val imageDataMap = mapToImageUrlDTOs(snapshot?.data)
        emit(imageDataMap)
    }

    override fun getGenusWithImagesFlow(genusName: String): Flow<IGenusWithImages?> {
        val dataFlow = getGenusNameFlow(genusName)
        val imagesFlow = getGenusImagesFlow(genusName)

        return combine(dataFlow, imagesFlow) { genusData, genusImages ->
            genusData?.let {
                GenusWithImages(genusData, genusImages)
            }
        }
    }

    /**
     * Attempts to retrieve and parse the document containing the image-location
     * for the genus with the given name.
     * If successful, the image-data is passed to the callback; otherwise, onFailure is invoked.
     */
    override fun getImagesForGenus(
        genusName: String,
        callback: (Map<String, MultiImageUrlData>?) -> Unit,
        onFailure: () -> Unit
    ) {
        if (hasNetConnectivity) {
            genusImageCollection
                .document(genusName)
                .get()
                .addOnSuccessListener { callback(mapToImageUrlDTOs(it.data)) }
                .addOnFailureListener { exc ->
                    Log.d(TAG, "No images for genera $genusName", exc)
                    onFailure()
                }
        }
        else {
            Log.d(TAG, "Cannot retrieve images due to lack of Network Connectivity")
            onFailure()
        }
    }


}


