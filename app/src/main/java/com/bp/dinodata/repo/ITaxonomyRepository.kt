package com.bp.dinodata.repo

import kotlinx.coroutines.flow.Flow

interface ITaxonomyRepository {
    /**
     * Get a flow which provides a map from a child taxon name (e.g. "Brachiosauridae") to the name
     * of the parent taxon to which the child belongs (e.g. "Sauropoda")
     * */
    fun getTaxonToParentMapFlow(): Flow<Map<String, String>>

    /**
     * Get a flow which provides a list of all unique taxon names (clades, orders, families).
     */
    fun getAllTaxonNamesFlow(): Flow<List<String>>

    suspend fun getTaxonToParentMap(): Map<String, String>
    suspend fun getAllTaxonNames(): List<String>
}