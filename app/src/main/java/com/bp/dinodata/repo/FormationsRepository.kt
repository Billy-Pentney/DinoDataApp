package com.bp.dinodata.repo

import com.bp.dinodata.data.FormationBuilder
import com.bp.dinodata.data.IFormation
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface IFormationsRepository {
    fun getFormationsFlow(): Flow<List<IFormation>>
    fun getFormationsByNameFlow(): Flow<Map<String, IFormation>>
}


class FormationsRepository @Inject constructor(
    private val connectivityChecker: IConnectionChecker,
    private val formationsCollection: CollectionReference
): IFormationsRepository {
    override fun getFormationsFlow(): Flow<List<IFormation>> {
        return formationsCollection.snapshots().map { snapshot ->
            snapshot.mapNotNull { doc ->
                FormationBuilder.makeFromDict(doc.data)?.build()
            }
        }
    }

    override fun getFormationsByNameFlow(): Flow<Map<String,IFormation>> {
        return getFormationsFlow().map {
            it.associateBy { formation -> formation.getName() }
        }
    }
}