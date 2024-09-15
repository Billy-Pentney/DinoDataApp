package com.bp.dinodata.di

import android.content.Context
import android.net.ConnectivityManager
import com.bp.dinodata.db.AppDatabase
import com.bp.dinodata.repo.AudioRepository
import com.bp.dinodata.repo.ConnectionChecker
import com.bp.dinodata.repo.CreatureTypeRepository
import com.bp.dinodata.repo.FormationsRepository
import com.bp.dinodata.repo.GenusRepository
import com.bp.dinodata.repo.IAudioRepository
import com.bp.dinodata.repo.IConnectionChecker
import com.bp.dinodata.repo.IFormationsRepository
import com.bp.dinodata.repo.IGenusRepository
import com.bp.dinodata.repo.ILocalPreferencesRepository
import com.bp.dinodata.repo.ITaxonomyRepository
import com.bp.dinodata.repo.LocalPreferencesRepository
import com.bp.dinodata.repo.TaxonomyRepository
import com.bp.dinodata.use_cases.AudioPronunciationUseCases
import com.bp.dinodata.use_cases.GenusDetailUseCases
import com.bp.dinodata.use_cases.GenusUseCases
import com.bp.dinodata.use_cases.ListGenusScreenUseCases
import com.bp.dinodata.use_cases.PlayPrerecordedAudioUseCase
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

    @Provides
    fun providesConnectivityManager(@ApplicationContext context: Context): ConnectivityManager {
        return context.getSystemService(ConnectivityManager::class.java)
    }

    @Singleton
    @Provides
    fun providesConnectionChecker(connectivityManager: ConnectivityManager): ConnectionChecker {
        return ConnectionChecker(
            connectivityManager
        )
    }

    @Singleton
    @Provides
    fun providesGenusRepository(connectionChecker: IConnectionChecker): GenusRepository {
        val storage = Firebase.firestore
        return GenusRepository(
            genusCollection = storage.collection(FirebaseCollections.GENERA),
            genusImageCollection = storage.collection(FirebaseCollections.IMAGES),
            connectivityChecker = connectionChecker
        )
    }


    @Singleton
    @Provides
    fun providesFormationsRepository(connectionChecker: IConnectionChecker): FormationsRepository {
        val storage = Firebase.firestore
        return FormationsRepository(
            formationsCollection = storage.collection(FirebaseCollections.FORMATIONS),
            connectivityChecker = connectionChecker
        )
    }

    @Singleton
    @Provides
    fun providesCreatureTypeRepository(connectionChecker: IConnectionChecker): CreatureTypeRepository {
        val storage = Firebase.firestore
        return CreatureTypeRepository(
            creatureTypesCollection = storage.collection(FirebaseCollections.CREATURE_TYPE),
            connectivityChecker = connectionChecker
        )
    }

    @Singleton
    @Provides
    fun providesLocalPrefRepository(db: AppDatabase): LocalPreferencesRepository {
        return LocalPreferencesRepository(db)
    }

    @Singleton
    @Provides
    fun providesTaxonomyRepository(): TaxonomyRepository {
        return TaxonomyRepository(Firebase.firestore.collection(FirebaseCollections.TAXONOMY))
    }

    @Singleton
    @Provides
    fun providesAudioRepository(
        @ApplicationContext context: Context,
        connectionChecker: IConnectionChecker
    ): AudioRepository {
        val storageRef = Firebase.storage.reference
        val cacheDir = context.cacheDir
        return AudioRepository(storageRef, cacheDir, connectionChecker)
    }

    @Singleton
    @Provides
    fun providesGenusUseCases(
        genusRepo: IGenusRepository,
        localPreferencesRepo: LocalPreferencesRepository,
        taxonRepo: ITaxonomyRepository
    ): GenusUseCases {
        return GenusUseCases(genusRepo, localPreferencesRepo, taxonRepo)
    }

    @Singleton
    @Provides
    fun providesGenusDetailUseCases(
        genusRepository: IGenusRepository,
        localPreferencesRepo: ILocalPreferencesRepository,
        formationsRepo: IFormationsRepository
    ): GenusDetailUseCases {
        return GenusDetailUseCases(
            genusRepository,
            localPreferencesRepo,
            formationsRepo
        )
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