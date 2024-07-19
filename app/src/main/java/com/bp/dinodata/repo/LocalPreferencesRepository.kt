package com.bp.dinodata.repo

import com.bp.dinodata.db.AppDatabase

interface ILocalPreferencesRepository {
    suspend fun getColorForGenus(genusName: String): String?

    fun updateColorForGenus(name: String, color: String): Boolean
}

class LocalPreferencesRepository(
    private val db: AppDatabase
): ILocalPreferencesRepository {

    override suspend fun getColorForGenus(
        genusName: String
    ): String? {
        return db.genusDao().getColorForGenusName(genusName)
    }

    override fun updateColorForGenus(name: String, color: String): Boolean {
        TODO("Not yet implemented")
    }
}