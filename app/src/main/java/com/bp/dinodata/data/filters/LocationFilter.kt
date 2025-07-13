package com.bp.dinodata.data.filters

import com.bp.dinodata.data.genus.IHasLocationInfo

class LocationFilter(
    acceptedLocations: List<String>,
    private val caseSensitive: Boolean = false
): IFilter<IHasLocationInfo> {

    private fun preprocessList(locations: List<String>): List<String> {
        return locations.map { loc ->
            loc.replace("_", " ")
                .let {
                    if (!caseSensitive) {
                        it.lowercase()
                    } else {
                        it
                    }
                }
        }
    }

    private val _acceptedLocations: List<String> = preprocessList(acceptedLocations)

    override fun acceptsItem(item: IHasLocationInfo): Boolean {
        val locations = preprocessList(item.getLocations())
        return locations.any { it in _acceptedLocations }
    }
}