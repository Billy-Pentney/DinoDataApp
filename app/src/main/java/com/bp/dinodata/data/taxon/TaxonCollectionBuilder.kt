package com.bp.dinodata.data.taxon

import com.bp.dinodata.data.IBuilder
import com.bp.dinodata.data.genus.IGenus

class TaxonCollectionBuilder(
    childToParentMap: Map<String, String> = emptyMap()
): IBuilder<ITaxonCollection> {

    private val taxaMap: MutableMap<String, ITaxon> = mutableMapOf()
    private var taxaRoots: List<ITaxon> = emptyList()
    private var taxaChildren: MutableSet<String> = mutableSetOf()
    private var parentMap: Map<String, String> = mutableMapOf()

    init {
        if (childToParentMap.isNotEmpty()) {
            buildTaxaFromParentMapping(childToParentMap)
        }
    }

    fun buildTaxaFromParentMapping(parents: Map<String, String>): TaxonCollectionBuilder {
        // Construct taxa to represent the tree
        parents.forEach { (childName, parentName) ->
            val lowerChildName = childName.lowercase()
            val lowerParentName = parentName.lowercase()

            if (lowerParentName == "saurischia") {
                print("here!")
            }

            if (lowerParentName !in taxaMap.keys) {
                taxaMap[lowerParentName] = MutableTaxon(lowerParentName)
            }
            if (lowerChildName !in taxaMap.keys) {
                taxaMap[lowerChildName] = MutableTaxon(lowerChildName, parentName = lowerParentName)
            }

            val child = taxaMap[lowerChildName]
            val parent = taxaMap[lowerParentName]

            if (child is IMutableTaxon) {
                child.setParent(lowerParentName)
            }
            if (parent is IMutableTaxon && child != null) {
                parent.addChild(child)
            }
        }
        taxaChildren.addAll(parents.keys.map { it.lowercase() })
        parentMap = parentMap + parents.mapKeys { it.key.lowercase() }

        // Get the names of taxa which do not have a parent
        val taxaNamesRoots = parentMap.values.map { it.lowercase() }.minus(taxaChildren)
        taxaRoots = taxaMap.filter { it.key in taxaNamesRoots }.map { it.value }

        return this
    }

    fun addGenera(generaList: List<IGenus>): TaxonCollectionBuilder {
        // Add the genera to the tree as leaves
        generaList.forEach { genus ->
            val lineage = genus.getListOfTaxonomy()
            lineage.lastOrNull()?.let { lastTaxon ->
                val lastTaxonLower = lastTaxon.lowercase()
                val parent: ITaxon?

                if (lastTaxonLower !in taxaMap.keys) {
                    parent = MutableTaxon(lastTaxonLower)
                    taxaMap[lastTaxonLower] = parent
                }
                else {
                    parent = taxaMap[lastTaxonLower]
                }

                if (parent is IMutableTaxon) {
                    parent.addChild(genus)
                }
            }
            taxaMap[genus.getName().lowercase()] = genus
        }
        return this
    }

    override fun clear(): IBuilder<ITaxonCollection> {
        taxaMap.clear()
        taxaRoots = emptyList()
        return this
    }

    override fun build(): TaxonCollection {
        return TaxonCollection(
            taxaMap,
            taxaRoots.sortedByDescending { it.getNumOfDescendants() }.map { it.getName().lowercase() }
        )
    }
}