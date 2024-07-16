package com.bp.dinodata.repo

import com.bp.dinodata.data.Genus
import com.bp.dinodata.data.MultiImageUrlData

interface IGenusRepository {
    fun getAllGenera(
        callback: (List<Genus>) -> Unit,
        onError: (Exception) -> Unit
    )

    /**
     * Attempt to retrieve data for the genus with the given name. This includes the image
     * data object which may have been omitted on the first pass.
     * If retrieval is successful, the callback is invoked with the created genus, or null
     * if the genus is invalid.
     * Otherwise, if an error occurred, onFailure is called.
     * */
    fun getGenus(
        genusName: String,
        callback: (Genus?) -> Unit,
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
}