package com.bp.dinodata.di

import com.bp.dinodata.repo.AudioRepository
import com.bp.dinodata.repo.ConnectionChecker
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

    @Binds
    fun bindsFormationsRepository(repository: FormationsRepository): IFormationsRepository

    @Binds
    fun bindsTaxonomyRepository(repository: TaxonomyRepository): ITaxonomyRepository

    @Binds
    fun bindsConnectionChecker(connectionChecker: ConnectionChecker): IConnectionChecker
}