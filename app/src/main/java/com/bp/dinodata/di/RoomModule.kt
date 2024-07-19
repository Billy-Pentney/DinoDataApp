package com.bp.dinodata.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.bp.dinodata.db.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {
    private const val DATABASE_NAME = "preferences"

    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
            .createFromAsset("database/myapp.db")
            .build()
    }
}