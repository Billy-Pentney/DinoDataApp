package com.bp.dinodata.repo

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

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

class TaxonomyRepository @Inject constructor(
    private val taxonomyCollection: CollectionReference
) : ITaxonomyRepository {

    companion object {
        const val PARENTS_FNAME = "taxon_parents"
    }

    override fun getTaxonToParentMapFlow(): Flow<Map<String, String>> {
        return taxonomyCollection.document(PARENTS_FNAME)
            .snapshots()
            .map { snapshot ->
                snapshot.data?.mapValues { childParentEntry ->
                    childParentEntry.value.toString()
                } ?: emptyMap()
            }
    }

    override fun getAllTaxonNamesFlow(): Flow<List<String>> {
        return taxonomyCollection.document(PARENTS_FNAME)
            .snapshots()
            .map { snapshot ->
                // Get all unique names in the taxonomy collection
                snapshot.data?.let { data ->
                    val childrenNames = data.keys.toSet()
                    val parentNames = data.values.map { it.toString() }.toSet()
                    (childrenNames + parentNames).toList()
                } ?: emptyList()
            }
    }

    override suspend fun getAllTaxonNames(): List<String> {
        return getAllTaxonNamesFlow().first()
    }

    override suspend fun getTaxonToParentMap(): Map<String, String> {
        return getTaxonToParentMapFlow().first()
    }
}