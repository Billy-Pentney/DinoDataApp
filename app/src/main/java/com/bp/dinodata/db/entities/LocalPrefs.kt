package com.bp.dinodata.db.entities

import androidx.room.ColumnInfo
import com.bp.dinodata.data.genus.IGenusPrefs

data class LocalPrefs(
    @ColumnInfo(name="is_favourite")
    private val isFavourite: Boolean = false,
    @ColumnInfo(name="color_name")
    private val colorName: String? = null
): IGenusPrefs {
    override fun isUserFavourite(): Boolean = isFavourite
    override fun getSelectedColorName(): String? = colorName
}
