package com.bp.dinodata.data.taxon

import android.util.Log

/** Describes a taxon whose children can be modified */
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