package com.bp.dinodata.di

import com.bp.dinodata.repo.GenusRepository
import com.bp.dinodata.use_cases.GenusUseCases
import com.bp.dinodata.use_cases.GetAllGenera
import com.bp.dinodata.use_cases.GetGenusByName
import com.google.firebase.Firebase
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideFirebaseCloudStorage(): FirebaseFirestore {
        return Firebase.firestore
    }

    @Provides
    fun provideFirebaseGenusCollection(
        storage: FirebaseFirestore
    ): CollectionReference {
        return storage.collection("genera")
    }

    @Provides
    fun providesGenusRepository(
        storage: FirebaseFirestore
    ): GenusRepository {
        val genusCollection = storage.collection("genera")
        return GenusRepository(genusCollection)
    }

    @Provides
    fun providesGenusUseCases(
        genusRepo: GenusRepository
    ): GenusUseCases {
        return GenusUseCases(
            GetAllGenera(genusRepo),
            GetGenusByName(genusRepo)
        )
    }

}