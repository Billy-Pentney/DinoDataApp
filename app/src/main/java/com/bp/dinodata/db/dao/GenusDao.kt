package com.bp.dinodata.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.MapColumn
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.bp.dinodata.data.genus.GenusPrefs
import com.bp.dinodata.data.genus.IGenusPrefs
import com.bp.dinodata.db.entities.ColorEntity
import com.bp.dinodata.db.entities.GenusEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GenusDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg genera: GenusEntity)

    @Update
    fun updateAll(vararg genera: GenusEntity)


    @Query("SELECT name, color_name FROM genera JOIN colors ON color_id == id")
    fun getGenusToColorMap(): Map<
            @MapColumn(columnName = "name") String,
            @MapColumn(columnName = "color_name") String
        >

    @Query("SELECT name, is_favourite FROM genera")
    suspend fun getGenusToFavouriteMap(): Map<
            @MapColumn(columnName = "name") String,
            @MapColumn(columnName = "is_favourite") Boolean
        >

    @Query("SELECT color_name FROM genera JOIN colors ON color_id == id " +
            "WHERE name LIKE :name LIMIT 1")
    suspend fun getColorForGenusName(name: String): String?

    @Query("SELECT is_favourite, color_name FROM genera JOIN colors ON color_id == id " +
            "WHERE name LIKE :name LIMIT 1")
    suspend fun getGenusPrefs(name: String): GenusPrefs?

    @Query("SELECT is_favourite, color_name FROM genera JOIN colors ON color_id == id " +
            "WHERE name LIKE :name LIMIT 1")
    fun getFlow(name: String): Flow<GenusPrefs?>

    @Query("UPDATE genera SET color_id = :colorId WHERE name LIKE :name")
    suspend fun updateColor(name: String, colorId: Int)
}
