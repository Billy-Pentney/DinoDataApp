package com.bp.dinodata.di

/**
 * Names of the Firebase collections.
 * These values should be provided to storage.collection(..) to retrieve a reference
 * to the corresponding document collection.
 * **/
object FirebaseCollections {
    const val CREATURE_TYPE = "creature_types"
    const val TAXONOMY = "taxonomy"
    const val GENERA = "genera"
    const val IMAGES = "images"
    const val FORMATIONS = "formations"
}