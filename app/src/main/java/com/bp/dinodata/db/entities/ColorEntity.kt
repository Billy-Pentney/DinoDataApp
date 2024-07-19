package com.bp.dinodata.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "colors")
data class ColorEntity(
    @PrimaryKey
    @ColumnInfo(name="id")
    val id: Int,

    @ColumnInfo(name="color_name")
    val name: String
)
