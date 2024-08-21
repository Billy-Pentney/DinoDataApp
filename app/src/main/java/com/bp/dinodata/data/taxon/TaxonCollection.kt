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
    override fun getParentTaxonName(): String? = taxon.getParentTaxonName()
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
    fun collapseAllTaxa(): ITaxonCollection
    fun getNumExpandedTaxa(): Int

    fun getPathOfTaxaToName(name: String): List<String>

    fun getAllExpandedNames(): Set<String>
    fun markTaxonAsHighlighted(taxon: ITaxon): Boolean
    fun markAsHighlighted(vararg names: String): List<Boolean>
    fun getAllHighlightedNames(): Set<String>
}

class TaxonCollection(
    private val taxonDictionary: MutableMap<String, ITaxon> = mutableMapOf(),
    private val roots: List<String> = emptyList(),
    private val expanded: MutableSet<String> = roots.toMutableSet(),
    private val highlighted: MutableSet<String> = mutableSetOf()
): ITaxonCollection {

    private fun addTaxon(taxon: ITaxon, isExpanded: Boolean = false) {
        taxonDictionary[taxon.getName().lowercase()] = taxon
        addTaxa(taxon.getChildrenTaxa())
    }

    private fun addTaxa(taxa: List<ITaxon>) {
        taxa.forEach {
            taxonDictionary[it.getName().lowercase()] = it
            addTaxa(it.getChildrenTaxa())
        }
    }

    override fun markTaxonAsExpanded(taxon: ITaxon): Boolean {
        val taxonName = taxon.getName().lowercase()
        if (taxonName !in taxonDictionary.keys) {
            addTaxon(taxon, true)
        }
        else {
            expanded.add(taxonName)
        }
        return true
    }

    override fun markAsExpanded(vararg names: String): List<Boolean> {
        return names.map { name ->
            val taxon = taxonDictionary[name.lowercase()]
            if (taxon != null) {
                markTaxonAsExpanded(taxon)
            }
            else {
                false
            }
        }
    }

    override fun markTaxonAsHighlighted(taxon: ITaxon): Boolean {
        val taxonName = taxon.getName().lowercase()
        if (taxonName !in taxonDictionary.keys) {
            addTaxon(taxon, true)
        }
        else {
            highlighted.add(taxonName)
        }
        return true
    }

    override fun markAsHighlighted(vararg names: String): List<Boolean> {
        return names.map { name ->
            val taxon = taxonDictionary[name.lowercase()]
            if (taxon != null) {
                markTaxonAsHighlighted(taxon)
            }
            else {
                false
            }
        }
    }

    override fun getTaxonByName(name: String): ITaxon? = taxonDictionary[name.lowercase()]
    override fun getRoots(): List<ITaxon> = roots.mapNotNull { taxonDictionary[it] }
    override fun isEmpty(): Boolean = roots.isEmpty()

    /**
     * Make a new copy of this collection, with the given pairs used to mark
     * taxa as expanded.
     * */
    fun copy(
        taxonDictionary: Map<String, ITaxon> = this.taxonDictionary,
        roots: List<String> = this.roots,
        expanded: Set<String> = this.expanded,
        highlighted: Set<String> = this.highlighted
    ): TaxonCollection {
        return TaxonCollection(
            taxonDictionary = taxonDictionary.toMutableMap(),
            roots = roots,
            expanded = expanded.map{ it.lowercase()}.toMutableSet(),
            highlighted = highlighted.map { it.lowercase() }.toMutableSet()
        )
    }

    fun setExpansionState(vararg taxonStates: Pair<String, Boolean>): TaxonCollection {
        val newExpanded = expanded.toMutableSet()

        val groups = taxonStates.groupBy { it.second }

        // Remove collapsed taxa
        val newCollapsedNames = groups[false]?.map { it.first.lowercase() }?.toSet() ?: emptySet()
        newExpanded.removeAll(newCollapsedNames)

        // Add expanded taxa
        val newExpandedNames = groups[true]?.map { it.first.lowercase() }?.toSet() ?: emptySet()
        newExpanded.addAll(newExpandedNames)

        return this.copy(
            expanded = newExpanded
        )
    }

    override fun getExpansionMapping(): Map<String, Boolean> = taxonDictionary.keys.associateWith { it in expanded }

    override fun isExpanded(taxon: ITaxon): Boolean {
        return taxon.getName().lowercase() in expanded
    }

    override fun collapseAllTaxa(): TaxonCollection {
        return TaxonCollection(
            taxonDictionary = taxonDictionary,
            roots = roots,
            expanded = mutableSetOf()
        )
    }

    override fun getNumExpandedTaxa(): Int = expanded.size

    override fun getPathOfTaxaToName(name: String): List<String> {
        val cleanName = name.trim().lowercase()

        var taxon: ITaxon? = getTaxonByName(cleanName)
        val toExpand = mutableListOf<String>()

        // Step up the tree until we find a node with no parents
        while (taxon != null) {
            toExpand.add(taxon.getName().lowercase())
            val parentName = taxon.getParentTaxonName()?.lowercase()
            taxon = parentName?.let { getTaxonByName(parentName) }
        }

        // Return the path from root-to-leaf
        return toExpand.reversed()
    }


    override fun getAllExpandedNames(): Set<String> = expanded
    override fun getAllHighlightedNames(): Set<String> = highlighted
}
