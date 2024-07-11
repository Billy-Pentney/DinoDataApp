package com.bp.dinodata.di

import android.content.Context
import com.bp.dinodata.repo.AudioRepository
import com.bp.dinodata.repo.GenusRepository
import com.bp.dinodata.use_cases.GenusUseCases
import com.bp.dinodata.use_cases.TextToSpeechUseCases
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun providesGenusRepository(): GenusRepository {
        val storage = Firebase.firestore
        val genusCollection = storage.collection("genera")
        val genusImageCollection = storage.collection("images")
        return GenusRepository(genusCollection, genusImageCollection)
    }

    @Provides
    fun providesAudioRepository(): AudioRepository {
        val storageRef = Firebase.storage.reference
        return AudioRepository(storageRef)
    }

    @Provides
    fun providesGenusUseCases(genusRepo: GenusRepository): GenusUseCases {
        return GenusUseCases(genusRepo)
    }


    @Provides
    fun providesTextUseCases(
        @ApplicationContext context: Context,
        audioRepository: AudioRepository
    ): TextToSpeechUseCases {
        return TextToSpeechUseCases(context, audioRepository)
    }

}