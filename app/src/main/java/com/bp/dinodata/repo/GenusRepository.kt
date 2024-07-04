package com.bp.dinodata.repo

import android.util.Log
import androidx.annotation.OptIn
import com.bp.dinodata.data.Genus
import com.bp.dinodata.data.GenusBuilderImpl
import com.google.firebase.firestore.CollectionReference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

class GenusRepository(
    val genusCollection: CollectionReference
) {
    private val TAG = "GenusRepository"

    private var generaFlow: MutableStateFlow<List<Genus>> = MutableStateFlow(emptyList())

    init {
        getAllGenera()
    }

    private fun getAllGenera() {
        genusCollection.get()
            .addOnSuccessListener { documents ->
                // Convert the results to Genus objects
                val allGenera = documents.map { doc ->
                        GenusBuilderImpl.fromDict(doc.data)?.build()
                    }
                    .filterNotNull()
                generaFlow.tryEmit(allGenera)
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting genera.", exception)
            }
    }

    fun getGeneraFlow(): Flow<List<Genus>> {
        return generaFlow
    }

    /**
     * Attempt to get the first genus with the given name
     */
    fun getGenus(genusName: String): Flow<Genus?> {
        return generaFlow.map { list -> list.firstOrNull { it.name == genusName } }
    }
}