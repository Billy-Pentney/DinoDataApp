package com.bp.dinodata.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.bp.dinodata.db.entities.ColorEntity

@Dao
interface ColorDao {
    @Insert
    fun insert(vararg colors: ColorEntity)

    @Query("SELECT * FROM colors")
    fun getAll(): List<ColorEntity>

    @Query("SELECT * FROM colors WHERE color_name == :color LIMIT 1")
    suspend fun getByName(color: String): ColorEntity?
}