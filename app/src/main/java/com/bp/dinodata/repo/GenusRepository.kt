package com.bp.dinodata.repo

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import com.bp.dinodata.data.Genus
import com.bp.dinodata.data.GenusBuilderImpl
import com.bp.dinodata.presentation.LoadState
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.dataObjects
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

class GenusRepository(
    val genusCollection: CollectionReference
) {
    private val TAG = "GenusRepository"

    private var generaList: Flow<List<Genus>> = genusCollection
        .orderBy("name")
        .snapshots().map { snapshot ->
            snapshot.mapNotNull { doc ->
                GenusBuilderImpl.fromDict(doc.data)?.build()
            }
        }

    private var lastVisible: DocumentSnapshot? = null
    private var pageNumber: Int = 0

    companion object {
        const val DEFAULT_PAGE_SIZE: Long = 25
    }

    suspend fun addGeneraListener(
        pageSize: Long = DEFAULT_PAGE_SIZE,
        startAfterName: String? = null,
        callback: (List<Genus>) -> Unit,
        onError: (FirebaseFirestoreException) -> Unit
    ) {
        Log.d(TAG, "Starting at ${lastVisible?.id}")

        val first = genusCollection
            .orderBy("name")
            .startAfter(startAfterName)
            .limit(pageSize)

        first.addSnapshotListener { snapshot, exception ->
            if (snapshot == null || snapshot.isEmpty) {
                return@addSnapshotListener
            }

            if (exception != null) {
                Log.d(TAG, "Error getting genera.", exception)
                onError(exception)
            }

            lastVisible = snapshot.documents.last()
            Log.d(TAG, "Converting documents up to ${lastVisible?.id}")

            // Convert the results to Genus objects
            val nextPageGenera = snapshot.mapNotNull { doc ->
                GenusBuilderImpl.fromDict(doc.data)?.build()
            }

            callback(nextPageGenera)
            pageNumber++
        }
    }

    fun getGeneraFlow(): Flow<List<Genus>> {
        return generaList
    }

    /** Attempt to get the first genus with the given name  */
    fun getGenus(genusName: String): Flow<Genus?> {
        return generaList.map { list -> list.first { it.name == genusName } }
    }
}