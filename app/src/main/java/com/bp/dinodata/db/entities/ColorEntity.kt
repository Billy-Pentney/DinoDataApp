package com.bp.dinodata.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "colors", indices = [Index("id")])
data class ColorEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="id")
    val id: Long,

    @ColumnInfo(name="color_name")
    val name: String
)
