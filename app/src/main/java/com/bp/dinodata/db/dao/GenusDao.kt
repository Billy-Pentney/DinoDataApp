package com.bp.dinodata.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.MapColumn
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.bp.dinodata.data.genus.Genus
import com.bp.dinodata.data.genus.LocalPrefs
import com.bp.dinodata.db.entities.GenusColorUpdate
import com.bp.dinodata.db.entities.GenusEntity
import com.bp.dinodata.db.entities.GenusFavouriteUpdate
import com.bp.dinodata.db.entities.GenusImageIndexUpdate
import kotlinx.coroutines.flow.Flow

@Dao
interface GenusDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg genera: GenusEntity)

    @Update
    fun updateAll(vararg genera: GenusEntity)

    @Query("SELECT color_name FROM genera LEFT JOIN colors ON color_id == id " +
            "WHERE name LIKE :name LIMIT 1")
    suspend fun getColorForGenusName(name: String): String?

    @Query("SELECT color_name, is_favourite, image_index FROM genera " +
            "LEFT JOIN colors ON color_id == id " +
            "WHERE name LIKE :name LIMIT 1")
    suspend fun getGenusPrefs(name: String): LocalPrefs?

    @Query("SELECT is_favourite, color_name, image_index FROM genera " +
            "LEFT JOIN colors ON color_id == id " +
            "WHERE name LIKE :name LIMIT 1")
    fun getFlow(name: String): Flow<LocalPrefs?>

    @Query("UPDATE genera SET color_id = :colorId WHERE name LIKE :name")
    suspend fun updateColor(name: String, colorId: Int?)

    /** Update or insert only the colour field. */
    @Upsert(GenusEntity::class)
    suspend fun upsertColor(update: GenusColorUpdate)

    /** Update or insert only the favourite field. */
    @Upsert(GenusEntity::class)
    suspend fun upsertFavourite(genusFavouriteUpdate: GenusFavouriteUpdate)

    @Query("SELECT name, color_name, is_favourite, image_index FROM genera " +
            "LEFT JOIN colors ON color_id == id")
    fun getGenusToLocalPrefsFlow(): Flow<Map<@MapColumn(columnName = "name") String, LocalPrefs>>

    @Upsert(GenusEntity::class)
    suspend fun upsertImageIndex(genusImageIndexUpdate: GenusImageIndexUpdate)
}
