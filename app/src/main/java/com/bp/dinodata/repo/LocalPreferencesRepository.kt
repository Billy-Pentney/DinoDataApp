package com.bp.dinodata.repo

import android.util.Log
import com.bp.dinodata.data.genus.LocalPrefs
import com.bp.dinodata.data.genus.ILocalPrefs
import com.bp.dinodata.db.AppDatabase
import com.bp.dinodata.db.entities.ColorEntity
import com.bp.dinodata.db.entities.GenusColorUpdate
import kotlinx.coroutines.flow.Flow

interface ILocalPreferencesRepository {
    suspend fun getColorForGenus(genusName: String): String?
    suspend fun updateColorForGenus(name: String, color: String?)
    suspend fun getGenusPrefs(name: String): LocalPrefs?
    fun getPrefsFlow(currentGenusName: String): Flow<ILocalPrefs?>
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

    override suspend fun updateColorForGenus(name: String, color: String?) {
        Log.i(TAG, "Attempt to set color($name) = \'$color\'")

        if (color != null) {
            val colorEntity = db.colorDao().getByName(color)
            if (colorEntity != null) {
                val id = colorEntity.id
                Log.i(TAG, "Retrieved color \'$color\' with id: $id")
                db.genusDao().upsertColor(GenusColorUpdate(name, id))
            }
            else {
                Log.d(TAG, "No colors found with name $color. Cannot update genus $name.")
            }
        }
        else {
            Log.i(TAG, "Clearing color for genus: $name")
            db.genusDao().upsertColor(GenusColorUpdate(name, null))
        }
    }

    override suspend fun getGenusPrefs(name: String): LocalPrefs? {
        return db.genusDao().getGenusPrefs(name)
    }

    override fun getPrefsFlow(currentGenusName: String): Flow<ILocalPrefs?> {
        return db.genusDao().getFlow(currentGenusName)
    }

    override suspend fun getAllColors(): List<ColorEntity> {
        return db.colorDao().getAll()
    }

    override fun getGenusLocalPrefsFlow(): Flow<Map<String, LocalPrefs>> {
        return db.genusDao().getGenusToLocalPrefsFlow()
    }
}