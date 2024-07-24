package com.bp.dinodata.data.genus

import com.bp.dinodata.data.IBuilder
import com.bp.dinodata.data.IDictParser

interface ISpecies: IHasName {
    fun getDiscoveryText(): String?
    fun isTypeSpecies(): Boolean
}

data class Species(
    private val name: String,
    private val discoveredBy: String? = null,
    private val discoveryYear: Int? = null,
    private val isType: Boolean = false
): ISpecies {
    override fun getDiscoveryText(): String? {
        if (discoveryYear != null && discoveredBy != null) {
            return "$discoveredBy, $discoveryYear"
        }
        return null
    }

    override fun isTypeSpecies(): Boolean = isType
    override fun getName(): String = name
}