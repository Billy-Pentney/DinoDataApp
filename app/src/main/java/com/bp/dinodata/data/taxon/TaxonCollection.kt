package com.bp.dinodata.data.taxon

import kotlin.math.exp

interface IDescribesExpandedState {
    fun isExpanded(): Boolean
}

interface IDisplayableTaxon: IDescribesExpandedState, ITaxon


data class ExpandableTaxon(
    private val taxon: ITaxon,
    private val isExpanded: Boolean = false
): IDisplayableTaxon {
    override fun getChildrenTaxa(): List<ITaxon> = taxon.getChildrenTaxa()
    override fun isExpanded(): Boolean = isExpanded
    override fun getName(): String = taxon.getName()
}

interface ITaxonCollection {
    /**
     * Attempts to get the taxon with the given name, if one exists.
     * @return The taxon with the given name if one exists, or null otherwise.
     * */
    fun getTaxonByName(name: String): ITaxon?

    /**
     * Get a list of the taxa which have no parents (i.e. the roots of the forest).
     */
    fun getRoots(): List<ITaxon>
    fun isEmpty(): Boolean

    /**
     * Record that the given taxon has been expanded.
     * If the taxon does not exist, add it to the dictionary
     * @return true if expanded; false otherwise.
     * */
    fun markTaxonAsExpanded(taxon: ITaxon): Boolean

    /**
     * Look-up the taxa corresponding to the given names, then attempt to expand them.
     * @return a list whose ith value indicates true if the ith name was successfully expanded, or
     * false otherwise. */
    fun markAsExpanded(vararg names: String): List<Boolean>

    fun getExpansionMapping(): Map<String, Boolean>

    /** Return true if and only if the given taxon is marked as expanded. */
    fun isExpanded(taxon: ITaxon): Boolean

    /** Return a new collection in which all taxa are contracted (not expanded). */
    fun closeAllTaxa(): ITaxonCollection
    fun getNumExpandedTaxa(): Int
}

class TaxonCollection(
    private val taxonDictionary: MutableMap<String, ITaxon> = mutableMapOf(),
    private val roots: List<String> = emptyList(),
    private val expanded: MutableMap<String, Boolean> = roots.associateWith { true }.toMutableMap()
): ITaxonCollection {

    constructor(
        rootTaxa: List<String>,
        taxaByName: Map<String, ITaxon> = emptyMap(),
        isTaxonExpanded: Map<ITaxon, Boolean> = emptyMap()
    ): this(
        taxonDictionary = taxaByName.mapValues {
            val taxon = it.value
            ExpandableTaxon(taxon, isTaxonExpanded[taxon] ?: false)
        }.toMutableMap(),
        roots = rootTaxa
    )

    private fun addTaxon(taxon: ITaxon, isExpanded: Boolean = false) {
        taxonDictionary[taxon.getName()] = taxon
        addTaxa(taxon.getChildrenTaxa())
    }

    private fun addTaxa(taxa: List<ITaxon>) {
        taxa.forEach {
            taxonDictionary[it.getName()] = ExpandableTaxon(it)
            addTaxa(it.getChildrenTaxa())
        }
    }

    override fun markTaxonAsExpanded(taxon: ITaxon): Boolean {
        val taxonName = taxon.getName()
        if (taxonName !in taxonDictionary.keys) {
            addTaxon(taxon, true)
        }
        else {
            expanded[taxonName] = true
        }
        return true
    }

    override fun markAsExpanded(vararg names: String): List<Boolean> {
        return names.map { name ->
            val taxon = taxonDictionary[name]
            if (taxon != null) {
                markTaxonAsExpanded(taxon)
            }
            else {
                false
            }
        }
    }

    override fun getTaxonByName(name: String): ITaxon? = taxonDictionary[name]
    override fun getRoots(): List<ITaxon> = roots.mapNotNull { taxonDictionary[it] }
    override fun isEmpty(): Boolean = roots.isEmpty()

    /**
     * Make a new copy of this collection, with the given pairs used to mark
     * taxa as expanded
     * */
    fun copy(vararg taxonStates: Pair<String, Boolean>): TaxonCollection {
        val newExpandedMap = expanded.toMutableMap()
        taxonStates.forEach {
            newExpandedMap[it.first] = it.second
        }

        return TaxonCollection(taxonDictionary, roots, newExpandedMap)
    }

    override fun getExpansionMapping(): Map<String, Boolean> = expanded

    override fun isExpanded(taxon: ITaxon): Boolean {
        return expanded[taxon.getName()] ?: false
    }

    override fun closeAllTaxa(): TaxonCollection {
        return TaxonCollection(
            taxonDictionary = taxonDictionary,
            roots = roots,
            expanded = mutableMapOf()
        )
    }

    override fun getNumExpandedTaxa(): Int = expanded.count { it.value }
}
