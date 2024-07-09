package com.bp.dinodata.repo

import android.util.Log
import com.bp.dinodata.data.Genus
import com.bp.dinodata.data.GenusBuilderImpl
import com.bp.dinodata.data.ImageUrlData.mapToImageUrlDTOs
import com.google.firebase.firestore.CollectionReference

class GenusRepository(
    val genusCollection: CollectionReference,
    val genusImageCollection: CollectionReference
) {
    private val TAG = "GenusRepository"

    companion object {
        const val DEFAULT_PAGE_SIZE: Long = 25
    }

    fun getAllGenera(
        callback: (List<Genus>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val first = genusCollection
            .orderBy("name")

        first.get()
            .addOnSuccessListener { snapshot ->
                val lastVisible = snapshot.documents.last()
                Log.d(TAG, "Converting documents up to ${lastVisible?.id}")

                // Convert the results to Genus objects
                val nextPageGenera = snapshot.mapNotNull { doc ->
                    GenusBuilderImpl.fromDict(doc.data)?.build()
                }
                callback(nextPageGenera)
            }
            .addOnFailureListener { onError(it) }
    }


    /** Attempt to get the first genus with the given name  */
    fun getGenus(
        genusName: String,
        callback: (Genus?) -> Unit,
        onFailure: () -> Unit
    ) {
        genusCollection
            .document(genusName)
            .get()
            .addOnSuccessListener { snapshot ->
                val genusBuilder = snapshot?.data?.let { GenusBuilderImpl.fromDict(it) }

                if (genusBuilder == null) {
                    Log.d(TAG, "No data for genera.")
                    onFailure()
                    return@addOnSuccessListener
                }

                // Now, get the images (if any)

                genusImageCollection
                    .document(genusName)
                    .get()
                    .addOnSuccessListener { imgDoc ->
                        val imageUrlMap = mapToImageUrlDTOs(imgDoc.data)
                        if (imageUrlMap != null) {
                            genusBuilder.addImageUrlMap(imageUrlMap)
                        }
                        callback(genusBuilder.build())
                    }
                    .addOnFailureListener { exc ->
                        Log.d(TAG, "No images for genera $genusName", exc)
                        callback(genusBuilder.build())
                    }
            }
            .addOnFailureListener { exc ->
                Log.d(TAG, "Genus $genusName not found", exc)
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


