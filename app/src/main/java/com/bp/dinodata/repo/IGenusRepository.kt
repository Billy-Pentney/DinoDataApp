package com.bp.dinodata.repo

import com.bp.dinodata.data.MultiImageUrlData
import com.bp.dinodata.data.genus.IGenus
import com.bp.dinodata.data.genus.IGenusWithImages
import kotlinx.coroutines.flow.Flow

interface IGenusRepository {
    /**
     * Asynchronously get the genera data and invoke the callback if successful.
     * If the data is not retrieved, then onError is invoked with the exception which was triggered.
     */
    fun getAllGenera(
        callback: (List<IGenus>) -> Unit,
        onError: (Exception) -> Unit
    )

    /**
     * Returns a flow which provides the current list of known genera data.
     */
    fun getAllGeneraFlow(): Flow<List<IGenus>?>

    /**
     * Attempt to retrieve data for the genus with the given name. This includes the image
     * data object which may have been omitted on the first pass.
     * If retrieval is successful, the callback is invoked with the created genus, or null
     * if the genus is invalid.
     * Otherwise, if an error occurred, onFailure is called.
     * */
    fun getGenusWithImages(
        genusName: String,
        onCompletion: (IGenusWithImages?) -> Unit,
        onFailure: () -> Unit
    )

    /**
     * Attempts to retrieve and parse the document containing the image-location
     * for the genus with the given name.
     * If successful, the image-data is passed to the callback; otherwise, onFailure is invoked.
     */
    fun getImagesForGenus(
        genusName: String,
        callback: (Map<String, MultiImageUrlData>?) -> Unit,
        onFailure: () -> Unit
    )


    /**
     * Get a flow providing an object with the data and images for the given genus, if such
     * data exists.
     */
    fun getGenusWithImagesFlow(genusName: String): Flow<IGenusWithImages?>

    /**
     * Get a flow which provides a list of all unique locations in the loaded genera data.
     */
    fun getLocationsFlow(): Flow<List<String>>
    fun getAllTaxaFlow(): Flow<List<String>>
}