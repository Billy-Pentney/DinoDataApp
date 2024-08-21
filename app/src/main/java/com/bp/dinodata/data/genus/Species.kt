package com.bp.dinodata.data.genus

import com.bp.dinodata.data.taxon.ITaxon

interface ISpecies: IHasName, ITextSearchable, ITaxon {
    fun getDiscoveryText(): String?
    fun isTypeSpecies(): Boolean
}

data class Species(
    private val name: String,
    private val discoveredBy: String? = null,
    private val discoveryYear: Int? = null,
    private val isType: Boolean = false,
    private val genusName: String? = null
): ISpecies {
    override fun getDiscoveryText(): String? {
        if (discoveryYear != null && discoveredBy != null) {
            return "$discoveredBy, $discoveryYear"
        }
        return null
    }

    override fun isTypeSpecies(): Boolean = isType
    override fun getName(): String = name

    override fun containsText(searchText: String): Boolean {
        return name.contains(searchText)
    }

    override fun getChildrenTaxa(): List<ITaxon> = emptyList()

    override fun getParentTaxonName(): String? = genusName
}