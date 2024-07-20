package com.bp.dinodata.data.genus

import androidx.room.ColumnInfo

interface IGenusPrefs {
    fun isUserFavourite(): Boolean
    fun getSelectedColorName(): String?
}

data class GenusPrefs(
    @ColumnInfo(name="is_favourite")
    private val _isFavourite: Boolean = false,
    @ColumnInfo(name="color_name")
    private val _color: String? = null
): IGenusPrefs {
    override fun isUserFavourite(): Boolean = _isFavourite
    override fun getSelectedColorName(): String? = _color
}