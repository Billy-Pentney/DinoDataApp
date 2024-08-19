package com.bp.dinodata.data.taxon

import com.bp.dinodata.data.IBuilder
import com.bp.dinodata.data.genus.IGenus

class TaxonCollectionBuilder(
    childToParentMap: Map<String, String> = emptyMap()
): IBuilder<ITaxonCollection> {

    private val taxaMap: MutableMap<String, IMutableTaxon> = mutableMapOf()
    private var taxaRoots: List<ITaxon> = emptyList()
    private var taxaChildren: MutableSet<String> = mutableSetOf()

    init {
        if (childToParentMap.isNotEmpty()) {
            buildTaxaFromParentMapping(childToParentMap)
        }
    }

    fun buildTaxaFromParentMapping(parents: Map<String, String>): TaxonCollectionBuilder {
        // Construct taxa to represent the tree
        parents.forEach { (childName, parentName) ->
            if (parentName !in taxaMap.keys) {
                taxaMap[parentName] = MutableTaxon(parentName)
            }
            if (childName !in taxaMap.keys) {
                taxaMap[childName] = MutableTaxon(childName)
            }

            taxaMap[childName]?.let {
                taxaMap[parentName]?.addChild(it)
            }
        }
        taxaChildren.addAll(parents.keys.toList())

        // Get the names of taxa which do not have a parent
        val taxaNamesRoots = taxaMap.keys.filter { it !in taxaChildren }
        taxaRoots = taxaMap.filter { it.key in taxaNamesRoots }.map { it.value.toTaxon() }

        return this
    }

    fun addGenera(generaList: List<IGenus>): TaxonCollectionBuilder {
        // Add the genera to the tree as leaves
        generaList.forEach { genus ->
            val lineage = genus.getListOfTaxonomy()
            lineage.lastOrNull()?.let { lastTaxon ->
                if (lastTaxon !in taxaMap.keys) {
                    taxaMap[lastTaxon] = MutableTaxon(lastTaxon)
                }
                taxaMap[lastTaxon]?.addChild(genus)
            }
        }
        return this
    }

    override fun clear(): IBuilder<ITaxonCollection> {
        taxaMap.clear()
        taxaRoots = emptyList()
        return this
    }

    override fun build(): ITaxonCollection {
        return TaxonCollection(
            taxaRoots.sortedByDescending { it.getNumOfDescendants() },
            taxaMap
        )
    }
}