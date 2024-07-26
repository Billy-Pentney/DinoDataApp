package com.bp.dinodata.db.entities

import androidx.room.ColumnInfo

class GenusColorUpdate(
    @ColumnInfo(name = "name")
    val genusName: String,
    @ColumnInfo(name = "color_id")
    val colorId: Long? = null
)

class GenusFavouriteUpdate(
    @ColumnInfo(name = "name")
    val genusName: String,
    @ColumnInfo(name = "is_favourite")
    val isFavourite: Boolean = false
)

class GenusImageIndexUpdate(
    @ColumnInfo(name = "name")
    val genusName: String,
    @ColumnInfo(name = "image_index")
    val imageIndex: Int = 0
)