package com.bp.dinodata.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.MapColumn
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.bp.dinodata.data.genus.LocalPrefs
import com.bp.dinodata.db.entities.GenusColorUpdate
import com.bp.dinodata.db.entities.GenusEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GenusDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg genera: GenusEntity)

    @Update
    fun updateAll(vararg genera: GenusEntity)

    @Query("SELECT color_name FROM genera JOIN colors ON color_id == id " +
            "WHERE name LIKE :name LIMIT 1")
    suspend fun getColorForGenusName(name: String): String?

    @Query("SELECT is_favourite, color_name FROM genera JOIN colors ON color_id == id " +
            "WHERE name LIKE :name LIMIT 1")
    suspend fun getGenusPrefs(name: String): com.bp.dinodata.data.genus.LocalPrefs?

    @Query("SELECT is_favourite, color_name FROM genera JOIN colors ON color_id == id " +
            "WHERE name LIKE :name LIMIT 1")
    fun getFlow(name: String): Flow<com.bp.dinodata.data.genus.LocalPrefs?>

    @Query("UPDATE genera SET color_id = :colorId WHERE name LIKE :name")
    suspend fun updateColor(name: String, colorId: Int?)

    @Upsert(GenusEntity::class)
    suspend fun upsertColor(update: GenusColorUpdate)

    @Query("SELECT name, color_name, is_favourite FROM genera JOIN colors ON color_id == id")
    fun getGenusToLocalPrefsFlow(): Flow<Map<@MapColumn(columnName = "name") String, LocalPrefs>>
}
