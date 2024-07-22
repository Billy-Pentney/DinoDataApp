package com.bp.dinodata.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bp.dinodata.db.entities.ColorEntity

@Dao
interface ColorDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(vararg colors: ColorEntity): List<Long>

    @Query("SELECT * FROM colors")
    suspend fun getAll(): List<ColorEntity>

    @Query("SELECT * FROM colors WHERE color_name == :color LIMIT 1")
    suspend fun getByName(color: String): ColorEntity?
}