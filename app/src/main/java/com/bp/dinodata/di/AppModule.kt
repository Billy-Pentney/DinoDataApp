package com.bp.dinodata.di

import android.content.Context
import android.speech.tts.TextToSpeech
import com.bp.dinodata.repo.GenusImageRepository
import com.bp.dinodata.repo.GenusRepository
import com.bp.dinodata.use_cases.GenusUseCases
import com.bp.dinodata.use_cases.GetGeneraAsList
import com.bp.dinodata.use_cases.GetGenusByName
import com.bp.dinodata.use_cases.TextToSpeechUseCases
import com.google.firebase.Firebase
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideFirebaseCloudStorage(): FirebaseFirestore {
        return Firebase.firestore
    }

//    @Provides
//    fun provideFirebaseGenusCollection(storage: FirebaseFirestore): CollectionReference {
//        return storage.collection("genera")
//    }
//
//    @Provides
//    fun provideFirebaseGenusImageCollection(storage: FirebaseFirestore): CollectionReference {
//        return storage.collection("images")
//    }

    @Provides
    fun providesGenusRepository(storage: FirebaseFirestore): GenusRepository {
        val genusCollection = storage.collection("genera")
        val genusImageCollection = storage.collection("images")
        return GenusRepository(genusCollection, genusImageCollection)
    }

    @Provides
    fun providesGenusUseCases(genusRepo: GenusRepository): GenusUseCases {
        return GenusUseCases(genusRepo)
    }

//    @Provides
//    fun ProvidesFirebaseStorage(): FirebaseStorage {
//        return Firebase.storage
//    }


    @Provides
    fun providesTextUseCases(
        @ApplicationContext context: Context
    ): TextToSpeechUseCases {
        return TextToSpeechUseCases(context)
    }

}