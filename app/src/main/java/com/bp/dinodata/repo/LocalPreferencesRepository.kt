package com.bp.dinodata.repo

import android.util.Log
import com.bp.dinodata.data.genus.LocalPrefs
import com.bp.dinodata.data.genus.ILocalPrefs
import com.bp.dinodata.db.AppDatabase
import com.bp.dinodata.db.entities.ColorEntity
import com.bp.dinodata.db.entities.GenusColorUpdate
import com.bp.dinodata.db.entities.GenusFavouriteUpdate
import kotlinx.coroutines.flow.Flow

interface ILocalPreferencesRepository {
    suspend fun getColorForGenus(genusName: String): String?
    suspend fun updateColorForGenus(name: String, color: String?)
    suspend fun getGenusPrefs(name: String): LocalPrefs?
    fun getPrefsFlow(currentGenusName: String): Flow<ILocalPrefs?>
    suspend fun getAllColors(): List<ColorEntity>
    fun getGenusLocalPrefsFlow(): Flow<Map<String, LocalPrefs>>
    suspend fun addColor(colorName: String)
    suspend fun updateFavouriteStatus(currentGenusName: String, favourite: Boolean)
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
                Log.d(TAG, "No colors found with name \'$color\'.")
                val colorIds = db.colorDao().insert(ColorEntity(id=0, name=color))
                if (colorIds.getOrNull(0) != null) {
                    Log.d(TAG, "Created color \'$color\' with id ${colorIds[0]}")
                    db.genusDao().upsertColor(GenusColorUpdate(name, colorIds[0]))
                }
                else {
                    Log.d(TAG, "Unable to create color with name $color. Cannot update genus $name.")
                }
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

    override suspend fun addColor(colorName: String) {
        // Id is auto-generated
        db.colorDao().insert(ColorEntity(id = 0, name = colorName))
    }

    override suspend fun updateFavouriteStatus(currentGenusName: String, favourite: Boolean) {
        db.genusDao().upsertFavourite(
            GenusFavouriteUpdate(currentGenusName, favourite)
        )
    }
}