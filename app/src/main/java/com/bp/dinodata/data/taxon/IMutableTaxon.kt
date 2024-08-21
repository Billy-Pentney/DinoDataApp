package com.bp.dinodata.data.taxon

import android.util.Log
import com.bp.dinodata.data.genus.IGenus

/** Describes a taxon whose children can be modified */
interface IMutableTaxon: ITaxon {
    fun addChild(child: ITaxon): Boolean
    fun toTaxon(): ITaxon
    fun setParent(lowerParentName: String)
}

data class MutableTaxon(
    private val name: String,
    private val initialChildren: List<ITaxon> = listOf(),
    private var parentName: String? = null
): IMutableTaxon {
    private val childrenByName: MutableMap<String, ITaxon> = initialChildren.associateBy { it.getName() }.toMutableMap()

    override fun getChildrenTaxa(): List<ITaxon> = Taxon.sortChildrenTaxa(
        childrenByName.values.toList()
    )
    override fun getName(): String = name.replaceFirstChar { it.uppercase() }
    override fun addChild(child: ITaxon): Boolean {
        val childName = child.getName()

        if (childName in childrenByName.keys) {
            Log.w("Taxon", "Adding duplicate taxon with name $childName")
        }
        childrenByName[childName] = child
        return true
    }

    override fun toTaxon(): ITaxon {
        return Taxon(
            name = name,
            initialChildren = childrenByName.values.toList(),
            parentTaxonName = parentName
        )
    }

    override fun setParent(lowerParentName: String) {
        parentName = lowerParentName
    }

    override fun getParentTaxonName(): String? = parentName
}