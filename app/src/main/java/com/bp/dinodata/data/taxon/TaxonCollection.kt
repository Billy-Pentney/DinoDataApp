package com.bp.dinodata.data.taxon

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

    fun isExpanded(taxon: ITaxon): Boolean
}

class TaxonCollection(
    private val _taxonDictionary: MutableMap<String, ITaxon> = mutableMapOf(),
    private val roots: List<String> = emptyList(),
    private val expanded: MutableMap<String, Boolean> = mutableMapOf()
): ITaxonCollection {

    constructor(
        rootTaxa: List<String>,
        taxaByName: Map<String, ITaxon> = emptyMap(),
        isTaxonExpanded: Map<ITaxon, Boolean> = emptyMap()
    ): this(
        _taxonDictionary = taxaByName.mapValues {
            val taxon = it.value
            ExpandableTaxon(taxon, isTaxonExpanded[taxon] ?: false)
        }.toMutableMap(),
        roots = rootTaxa
    )

    init {
        // Mark all the roots as expanded by default
        roots.forEach {
            expanded[it] = true
        }
    }

    private fun addTaxon(taxon: ITaxon, isExpanded: Boolean = false) {
        _taxonDictionary[taxon.getName()] = taxon
        addTaxa(taxon.getChildrenTaxa())
    }

    private fun addTaxa(taxa: List<ITaxon>) {
        taxa.forEach {
            _taxonDictionary[it.getName()] = ExpandableTaxon(it)
            addTaxa(it.getChildrenTaxa())
        }
    }

    override fun markTaxonAsExpanded(taxon: ITaxon): Boolean {
        val taxonName = taxon.getName()
        if (taxonName !in _taxonDictionary.keys) {
            addTaxon(taxon, true)
        }
        else {
            expanded[taxonName] = true
        }
        return true
    }

    override fun markAsExpanded(vararg names: String): List<Boolean> {
        return names.map { name ->
            val taxon = _taxonDictionary[name]
            if (taxon != null) {
                markTaxonAsExpanded(taxon)
            }
            else {
                false
            }
        }
    }

    override fun getTaxonByName(name: String): ITaxon? = _taxonDictionary[name]
    override fun getRoots(): List<ITaxon> = roots.mapNotNull { _taxonDictionary[it] }
    override fun isEmpty(): Boolean = roots.isEmpty()

    /**
     * Make a new copy of this collection, with the given pairs used to mark
     * taxa as expanded
     * */
    fun copy(vararg taxonStates: Pair<String, Boolean>): TaxonCollection {
        val expandedMap = expanded.toMutableMap()
        taxonStates.forEach {
            expandedMap[it.first] = it.second
        }

        return TaxonCollection(_taxonDictionary, roots)
    }

    override fun getExpansionMapping(): Map<String, Boolean> = expanded

    override fun isExpanded(taxon: ITaxon): Boolean {
        return expanded[taxon.getName()] ?: false
    }
}
