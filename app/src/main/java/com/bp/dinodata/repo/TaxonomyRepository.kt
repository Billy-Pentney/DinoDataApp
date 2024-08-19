package com.bp.dinodata.repo

import com.bp.dinodata.data.taxon.IMutableTaxon
import com.bp.dinodata.data.taxon.MutableTaxon
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface ITaxonomyRepository {
    fun getTaxonToParentMapFlow(): Flow<Map<String, String>>
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

}