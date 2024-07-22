package com.bp.dinodata.db.entities

import androidx.room.ColumnInfo

class GenusColorUpdate(
    @ColumnInfo(name = "name")
    val genusName: String,
    @ColumnInfo(name = "color_id")
    val colorId: Long? = null
)