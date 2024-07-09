package com.bp.dinodata.repo

import android.util.Log
import com.bp.dinodata.data.ImageUrlData.mapToImageUrlDTOs
import com.google.firebase.firestore.CollectionReference

class GenusImageRepository(
    val genusImageCollection: CollectionReference
) {
    private val TAG = "GenusImageRepository"

//    private fun getAllGenera() {
//        genusImageCollection.get()
//            .addOnSuccessListener { documents ->
//                // Convert the results to Genus objects
//                val allGenera = documents.map { doc ->
//                    GenusBuilderImpl.fromDict(doc.data)?.build()
//                }
//                    .filterNotNull()
//                generaFlow.tryEmit(allGenera)
//            }
//            .addOnFailureListener { exception ->
//                Log.d(TAG, "Error getting genera.", exception)
//            }
//    }

    private val cachedImageData: MutableMap<String, Map<*,*>> = mutableMapOf()

    fun getGenusImagesFor(genusName: String) {
        genusImageCollection
            .document(genusName)
            .get()
            .addOnSuccessListener { doc ->
                val imageData = mapToImageUrlDTOs(doc.data)
                imageData?.let {
                    cachedImageData[genusName] = it
                }
            }
            .addOnFailureListener { exc ->
                Log.d(TAG, "Error getting image data for genera.", exc)
            }
    }


}