package com.bp.dinodata.di

import android.content.Context
import android.net.ConnectivityManager
import androidx.core.content.ContextCompat.getSystemService
import com.bp.dinodata.repo.AudioRepository
import com.bp.dinodata.repo.GenusRepository
import com.bp.dinodata.repo.IAudioRepository
import com.bp.dinodata.repo.IGenusRepository
import com.bp.dinodata.use_cases.GenusUseCases
import com.bp.dinodata.use_cases.AudioPronunciationUseCases
import com.bp.dinodata.use_cases.PlayPrerecordedAudioUseCase
import com.bp.dinodata.use_cases.ReadTextToSpeechUseCase
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private const val GENERA_COLLECTION_NAME = "genera"
    private const val IMAGES_COLLECTION_NAME = "images"

    @Provides
    fun providesConnectivityManager(@ApplicationContext context: Context): ConnectivityManager {
        return context.getSystemService(ConnectivityManager::class.java)
    }

    @Singleton
    @Provides
    fun providesGenusRepository(connectivityManager: ConnectivityManager): GenusRepository {
        val storage = Firebase.firestore
        return GenusRepository(
            genusCollection = storage.collection(GENERA_COLLECTION_NAME),
            genusImageCollection = storage.collection(IMAGES_COLLECTION_NAME),
            connectivityManager = connectivityManager
        )
    }

    @Singleton
    @Provides
    fun providesAudioRepository(
        @ApplicationContext context: Context
    ): AudioRepository {
        val storageRef = Firebase.storage.reference
        val cacheDir = context.cacheDir
        return AudioRepository(storageRef, cacheDir)
    }

    @Singleton
    @Provides
    fun providesGenusUseCases(genusRepo: IGenusRepository): GenusUseCases {
        return GenusUseCases(genusRepo)
    }

    @Singleton
    @Provides
    fun providesAudioPronunciationUseCases(
        @ApplicationContext context: Context,
        audioRepository: IAudioRepository
    ): AudioPronunciationUseCases {
        return AudioPronunciationUseCases(
            PlayPrerecordedAudioUseCase(context, audioRepository)
        )
    }
}