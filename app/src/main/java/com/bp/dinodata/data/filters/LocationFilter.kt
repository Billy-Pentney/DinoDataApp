package com.bp.dinodata.data.filters

import com.bp.dinodata.data.genus.IHasLocationInfo

class LocationFilter(
    acceptedLocations: List<String>,
    private val capitalSensitive: Boolean = false
): IFilter<IHasLocationInfo> {

    private val _acceptedLocations: List<String> = preprocessList(acceptedLocations)

    private fun preprocessList(list: List<String>): List<String> {
        return if (capitalSensitive) {
            list
        } else {
            list.map { element -> element.lowercase().replace("_", " ") }
        }
    }

    override fun acceptsItem(item: IHasLocationInfo): Boolean {
        val locations = preprocessList(item.getLocations())
        return locations.any { it in _acceptedLocations }
    }
}