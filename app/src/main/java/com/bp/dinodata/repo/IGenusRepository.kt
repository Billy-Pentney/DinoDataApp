package com.bp.dinodata.repo

import com.bp.dinodata.data.genus.IGenus
import com.bp.dinodata.data.genus.IGenusWithImages
import kotlinx.coroutines.flow.Flow

interface IGenusRepository {
    /**
     * Returns a flow which provides the current list of known genera data.
     */
    fun getAllGeneraFlow(): Flow<List<IGenus>?>

    /**
     * Get a flow providing an object with the data and images for the given genus, if such
     * data exists.
     */
    fun getGenusWithImagesFlow(genusName: String): Flow<IGenusWithImages?>

    /**
     * Get a flow which provides a list of all unique locations in the loaded genera data.
     */
    fun getLocationsFlow(): Flow<List<String>>

    /** Get a flow providing a list of all distinct taxonomic groups, extracted from the genera. */
    fun getAllTaxaFlow(): Flow<List<String>>

    /** Get the names of all unique country locations, extracted from the genera. */
    suspend fun getLocations(): List<String>

    /** Get the names of all genera. */
    suspend fun getAllGeneraNames(): List<String>
}