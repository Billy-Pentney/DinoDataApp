package com.bp.dinodata.data.filters

import com.bp.dinodata.data.genus.IGenus
import com.bp.dinodata.data.genus.ILocalPrefs

class SelectedColorFilter(
    private val acceptedColors: List<String>
): IFilter<IGenus> {
    private val acceptsUncolored = acceptedColors.contains("NONE")

    override fun acceptsItem(item: IGenus): Boolean {
        if (item is ILocalPrefs) {
            val color = item.getSelectedColorName()
            return color in acceptedColors || (color == null && acceptsUncolored)
        }
        return false
    }
}