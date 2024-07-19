package com.bp.dinodata.di

import com.bp.dinodata.repo.AudioRepository
import com.bp.dinodata.repo.GenusRepository
import com.bp.dinodata.repo.IAudioRepository
import com.bp.dinodata.repo.IGenusRepository
import com.bp.dinodata.repo.ILocalPreferencesRepository
import com.bp.dinodata.repo.LocalPreferencesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface MyRepositoryModules {
    @Binds
    fun bindsGenusRepository(repository: GenusRepository): IGenusRepository

    @Binds
    fun bindsAudioRepository(repository: AudioRepository): IAudioRepository

    @Binds
    fun bindsLocalDataRepository(repository: LocalPreferencesRepository): ILocalPreferencesRepository
}