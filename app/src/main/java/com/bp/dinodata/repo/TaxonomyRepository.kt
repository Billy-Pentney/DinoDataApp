package com.bp.dinodata.repo

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

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

    private var taxonNamesFlow: StateFlow<List<String>>? = null

    override suspend fun getAllTaxonNames(): List<String> {
        return if (taxonNamesFlow == null) {
            getAllTaxonNamesFlow()
                .stateIn(CoroutineScope(Dispatchers.IO))
                .also { taxonNamesFlow = it }
                .value
        } else {
            taxonNamesFlow!!.value
        }
    }

    override suspend fun getTaxonToParentMap(): Map<String, String> {
        return getTaxonToParentMapFlow().first()
    }
}