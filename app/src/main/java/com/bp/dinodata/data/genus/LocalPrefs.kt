package com.bp.dinodata.data.genus

import androidx.room.ColumnInfo

interface IHasUserFavourite {
    fun isUserFavourite(): Boolean
}

interface IHasPreferredColor {
    fun getSelectedColorName(): String?
}

interface IHasPreferredImage {
    fun getPreferredImageIndex(): Int
}

interface ILocalPrefs: IHasPreferredColor, IHasUserFavourite, IHasPreferredImage

data class LocalPrefs(
    @ColumnInfo(name="is_favourite")
    private val _isFavourite: Boolean = false,
    @ColumnInfo(name="color_name")
    private val _color: String? = null,
    @ColumnInfo(name="image_index")
    private val _imageIndex: Int = 0
): ILocalPrefs {
    override fun isUserFavourite(): Boolean = _isFavourite
    override fun getPreferredImageIndex(): Int = _imageIndex
    override fun getSelectedColorName(): String? = _color
}