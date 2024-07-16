package com.bp.dinodata.repo

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.bp.dinodata.data.Genus
import com.bp.dinodata.data.GenusBuilderImpl
import com.bp.dinodata.data.ImageUrlData.mapToImageUrlDTOs
import com.bp.dinodata.data.MultiImageUrlData
import com.google.firebase.firestore.CollectionReference
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

    private var generaByName: Map<String, Genus> = emptyMap()

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


    /**
     * Attempt to retrieve data for the genus with the given name. This includes the image
     * data object which may have been omitted on the first pass.
     * If retrieval is successful, the callback is invoked with the created genus, or null
     * if the genus is invalid.
     * Otherwise, if an error occurred, onFailure is called.
     * */
    override fun getGenus(
        genusName: String,
        callback: (Genus?) -> Unit,
        onFailure: () -> Unit
    ) {
        // Attempt to use the pre-loaded data
        if (generaByName.containsKey(genusName)) {
            Log.d(TAG, "Image-request received for existing genus $genusName")
            val genusData = generaByName[genusName]!!

            if (genusData.getNumDistinctImages() > 0) {
                // Early-exit if we've already gotten these images
                callback(genusData)
                return
            }
            else {
                getImagesForGenus(
                    genusName,
                    callback = { imageUrlMap ->
                        val combinedGenusData =
                            if (imageUrlMap != null) {
                                Log.d(TAG, "Retrieved images for existing genus $genusName")
                                // Make a new genus object with the images
                                GenusBuilderImpl(genusData)
                                    .addImageUrlMap(imageUrlMap)
                                    .build()
    //                            generaByName[genusName] = combinedGenusData
                            } else {
                                Log.d(TAG, "Received null image data for existing genus $genusName")
                                // Return without images
                                genusData
                            }
                        callback(combinedGenusData)
                    },
                    onFailure = {
                        // Return but without images
                        Log.d(TAG, "Failed to get images for existing genus $genusName")
                        callback(genusData)
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
                        // Now, get the images (if any)
                        getImagesForGenus(
                            genusName,
                            callback = { imageUrlMap ->
                                imageUrlMap?.let {
                                    genusBuilder.addImageUrlMap(imageUrlMap)
                                }
                                callback(genusBuilder.build())
                            },
                            onFailure = {
                                callback(genusBuilder.build())
                            }
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


    fun getNextPage(
        startAfter: String?,
        pageSize: Long = DEFAULT_PAGE_SIZE,
        callback: (PageResult<Genus>) -> Unit,
        onException: (Exception) -> Unit
    ) {
        val first = genusCollection
            .orderBy("name")
            .startAfter(startAfter)
            .limit(pageSize)

        first.get()
            .addOnSuccessListener { snapshot ->
                val lastVisible = snapshot.documents.lastOrNull()
                Log.d(TAG, "Converting documents up to ${lastVisible?.id}")
                // Convert the results to Genus objects
                val nextPageGenera = snapshot.mapNotNull { doc ->
                    GenusBuilderImpl.fromDict(doc.data)?.build()
                }
                callback(
                    PageResult(
                        data = nextPageGenera,
                        isAllDataRetrieved = snapshot.documents.size < pageSize
                    )
                )
            }
            .addOnFailureListener(onException)
    }

}


