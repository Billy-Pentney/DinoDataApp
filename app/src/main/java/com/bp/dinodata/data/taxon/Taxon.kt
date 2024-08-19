package com.bp.dinodata.data.taxon

import com.bp.dinodata.data.genus.IGenus
import com.bp.dinodata.data.genus.IHasName

interface ITaxon: IHasName {
    /**
     * Get all children who are immediate descendants of this node.
     * For a genus, this is the species.
     * */
    fun getChildrenTaxa(): List<ITaxon>

    /** Indicates if this node has any children. */
    fun hasChildrenTaxa(): Boolean = getChildrenTaxa().isNotEmpty()

    /** Get the number of immediate children of this node. */
    fun getNumChildren(): Int = getChildrenTaxa().size

    /**
     * Get the total number of nodes which are descendants of this node.
     * This includes children, grandchildren... etc., down to the leaves (genera). */
    fun getNumOfDescendants(): Int {
        // Add one to include each node in its count
        return 1 + getChildrenTaxa().sumOf { it.getNumOfDescendants() }
    }
}


/**
 * Describes a high-level node of the Taxonomic tree whose type is not specified.
 * This could be a Clade, Family, Order,.. etc.
 * Genera and Species should be constructed using their respective builders.
 * */
data class Taxon(
    private val name: String,
    private val initialChildren: List<ITaxon> = emptyList()
): ITaxon {

    private val _children = Taxon.sortChildrenTaxa(initialChildren)

    companion object {
        fun sortChildrenTaxa(children: List<ITaxon>): List<ITaxon> {
            return children.sortedWith{ taxonA: ITaxon, taxonB: ITaxon ->
                // Place the clades, families... before the genera
                if (taxonA !is IGenus || taxonB is IGenus) {
                    -1
                }
                else {
                    1
                }
            }
        }
    }

    // Sort the children so that taxa are grouped first, and genera are at the end
    override fun getChildrenTaxa(): List<ITaxon> = _children
    override fun getName(): String = name
}

