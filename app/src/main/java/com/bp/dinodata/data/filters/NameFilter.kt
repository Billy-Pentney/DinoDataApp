package com.bp.dinodata.data.filters

import com.bp.dinodata.data.genus.IHasName

class NameFilter(
    private val nameQuery: String,
    private val capitalSensitive: Boolean
): IFilter<IHasName> {
    override fun acceptsItem(item: IHasName): Boolean {
        val itemName = item.getName()
        return if (!capitalSensitive) {
            itemName.lowercase().contains(nameQuery, true)
        } else {
            itemName.contains(nameQuery, true)
        }
    }

    override fun toString(): String {
        return "\'$nameQuery\' in NAME"
    }
}