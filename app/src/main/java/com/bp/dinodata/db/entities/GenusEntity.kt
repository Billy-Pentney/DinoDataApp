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

    @ColumnInfo(name="color_id")
    val colorId: Int? = null,

    @ColumnInfo(name="is_favourite")
    val isFavourite: Boolean = false,
)