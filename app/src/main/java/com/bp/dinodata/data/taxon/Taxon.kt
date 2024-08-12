package com.bp.dinodata.data.taxon

import android.util.Log
import com.bp.dinodata.data.genus.IHasName

interface ITaxon: IHasName {
    fun getChildrenTaxa(): List<ITaxon>
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

data class Taxon(
    private val name: String,
    private val children: List<ITaxon> = emptyList()
): ITaxon {
    override fun getChildrenTaxa(): List<ITaxon> = children
    override fun getName(): String = name
}

interface IMutableTaxon: ITaxon {
    fun addChild(child: ITaxon): Boolean
}

data class MutableTaxon(
    private val name: String,
    private val initialChildren: List<ITaxon> = listOf()
): IMutableTaxon {
    private val childrenByName: MutableMap<String, ITaxon> = initialChildren.associateBy { it.getName() }.toMutableMap()

    override fun getChildrenTaxa(): List<ITaxon> = childrenByName.values.toList()
    override fun getName(): String = name
    override fun addChild(child: ITaxon): Boolean {
        val childName = child.getName()

        if (childName in childrenByName.keys) {
            Log.w("Taxon", "Adding duplicate taxon with name $childName")
        }
        childrenByName[childName] = child
        return true
    }
}