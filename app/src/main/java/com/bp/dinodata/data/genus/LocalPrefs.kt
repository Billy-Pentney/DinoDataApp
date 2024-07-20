package com.bp.dinodata.data.genus

import androidx.room.ColumnInfo

interface IHasUserFavourite {
    fun isUserFavourite(): Boolean
}

interface IHasColor {
    fun getSelectedColorName(): String?
}

interface ILocalPrefs: IHasColor, IHasUserFavourite

data class LocalPrefs(
    @ColumnInfo(name="is_favourite")
    private val _isFavourite: Boolean = false,
    @ColumnInfo(name="color_name")
    private val _color: String? = null
): ILocalPrefs {
    override fun isUserFavourite(): Boolean = _isFavourite
    override fun getSelectedColorName(): String? = _color
}