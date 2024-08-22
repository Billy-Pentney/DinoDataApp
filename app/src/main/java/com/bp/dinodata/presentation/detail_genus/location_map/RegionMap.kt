package com.bp.dinodata.presentation.detail_genus.location_map

interface IRegionMap {
    fun getMarker(key: String): IMarker?
    fun getAllKeys(): List<String>
    fun getRegionName(): String
}

class RegionMap(
    private val name: String,
    private val markers: Map<String, IMarker> = emptyMap()
): IRegionMap {
    constructor(
        name: String,
        vararg pairs: Pair<String, IMarker>
    ): this(name, pairs.toMap())

    override fun getRegionName(): String = name
    override fun getMarker(key: String): IMarker? = markers[key]
    override fun getAllKeys(): List<String> = markers.keys.toList()
}