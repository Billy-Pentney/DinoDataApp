package com.bp.dinodata.repo

import com.bp.dinodata.data.CreatureType
import com.bp.dinodata.data.CreatureTypeInfo
import com.bp.dinodata.data.ICreatureTypeInfo
import com.bp.dinodata.data.attributions.ResourceAttribution
import com.bp.dinodata.data.enum_readers.CreatureTypeConverter
import com.bp.dinodata.presentation.detail_creature_type.TextWithAttribution
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface ICreatureTypeRepository {
    fun getCreatureTypeInfoMapFlow(): Flow<Map<CreatureType, ICreatureTypeInfo>>
}


class CreatureTypeRepository @Inject constructor(
    private val connectivityChecker: IConnectionChecker,
    private val creatureTypesCollection: CollectionReference
): ICreatureTypeRepository {

    companion object {
        private const val DESCRIPTION_KEY = "description"
        private const val ATTRIBUTION_KEY = "attribution"
        private const val ID_KEY = "id"
    }

    override fun getCreatureTypeInfoMapFlow(): Flow<Map<CreatureType, CreatureTypeInfo>> {
        return creatureTypesCollection.snapshots().map { snapshot ->
            snapshot.mapNotNull { doc ->
                // Construct the information object for each CreatureType

                // The id should match the internal name of the CreatureType
                val id: String = doc.data[ID_KEY].toString()

                val description: String = doc.data[DESCRIPTION_KEY].toString()
                val attribution: String = doc.data[ATTRIBUTION_KEY].toString()

                val creatureType = CreatureTypeConverter.matchType(id)

                creatureType?.let { type ->
                    CreatureTypeInfo(
                        type = type,
                        description = description,
                        descriptionAttribution = ResourceAttribution(attribution)
                    )
                }
            }.associateBy { it.getCreatureType() }
        }
    }
}