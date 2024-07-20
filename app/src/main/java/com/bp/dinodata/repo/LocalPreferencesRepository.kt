package com.bp.dinodata.repo

import android.util.Log
import com.bp.dinodata.data.genus.GenusPrefs
import com.bp.dinodata.data.genus.IGenusPrefs
import com.bp.dinodata.db.AppDatabase
import com.bp.dinodata.db.entities.ColorEntity
import com.bp.dinodata.db.entities.GenusColorUpdate
import com.bp.dinodata.db.entities.LocalPrefs
import kotlinx.coroutines.flow.Flow

interface ILocalPreferencesRepository {
    suspend fun getColorForGenus(genusName: String): String?
    suspend fun updateColorForGenus(name: String, color: String?)
    suspend fun getGenusPrefs(name: String): GenusPrefs?
    fun getPrefsFlow(currentGenusName: String): Flow<IGenusPrefs?>
    suspend fun getAllColors(): List<ColorEntity>
    fun getGenusLocalPrefsFlow(): Flow<Map<String, LocalPrefs>>
}

class LocalPreferencesRepository(
    private val db: AppDatabase
): ILocalPreferencesRepository {

    companion object {
        private const val TAG = "LocalPrefRepo"
    }

    override suspend fun getColorForGenus(genusName: String): String? {
        return db.genusDao().getColorForGenusName(genusName)
    }

    override suspend fun updateColorForGenus(genusName: String, color: String?) {
        Log.i(TAG, "Attempt to set color($genusName) = \'$color\'")

        if (color != null) {
            val colorEntity = db.colorDao().getByName(color)
            if (colorEntity != null) {
                val id = colorEntity.id
                Log.i(TAG, "Retrieved color \'$color\' with id: $id")
                db.genusDao().upsertColor(GenusColorUpdate(genusName, id))
            }
            else {
                Log.d(TAG, "No colors found with name $color. Cannot update genus $genusName.")
            }
        }
        else {
            Log.i(TAG, "Clearing color for genus: $genusName")
            db.genusDao().upsertColor(GenusColorUpdate(genusName, null))
        }
    }

    override suspend fun getGenusPrefs(name: String): GenusPrefs? {
        return db.genusDao().getGenusPrefs(name)
    }

    override fun getPrefsFlow(currentGenusName: String): Flow<IGenusPrefs?> {
        return db.genusDao().getFlow(currentGenusName)
    }

    override suspend fun getAllColors(): List<ColorEntity> {
        return db.colorDao().getAll()
    }

    override fun getGenusLocalPrefsFlow(): Flow<Map<String, LocalPrefs>> {
        return db.genusDao().getGenusToLocalPrefsFlow()
    }
}