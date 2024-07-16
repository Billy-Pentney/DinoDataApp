package com.bp.dinodata.di

import com.bp.dinodata.repo.AudioRepository
import com.bp.dinodata.repo.GenusRepository
import com.bp.dinodata.repo.IAudioRepository
import com.bp.dinodata.repo.IGenusRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface MyRepositoryModules {
    @Binds
    fun provideGenusRepository(repository: GenusRepository): IGenusRepository

    @Binds
    fun provideAudioRepository(repository: AudioRepository): IAudioRepository
}