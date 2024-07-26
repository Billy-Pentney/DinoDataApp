package com.bp.dinodata.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "genera",
    foreignKeys = [ForeignKey(
        ColorEntity::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("color_id")
    )]
)
data class GenusEntity(
    @PrimaryKey
    @ColumnInfo(name="name")
    val name: String,

    @ColumnInfo(name="color_id", defaultValue = "NULL")
    val colorId: Long? = null,

    @ColumnInfo(name="is_favourite", defaultValue = "0")
    val isFavourite: Boolean = false,

    @ColumnInfo(name="image_index", defaultValue = "0")
    val imageIndex: Int = 0
)