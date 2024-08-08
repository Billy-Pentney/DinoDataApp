package com.bp.dinodata.data.filters

import com.bp.dinodata.data.genus.IHasTaxonomy

class TaxonFilter(
    private val acceptedTaxa: List<String>
): IFilter<IHasTaxonomy> {
    override fun acceptsItem(item: IHasTaxonomy): Boolean {
        val taxonList = item.getListOfTaxonomy().map { it.lowercase() }
        return taxonList.any {
            itemTaxon ->
            acceptedTaxa.any { it in itemTaxon }
        }
    }

    override fun toString(): String {
        return "${acceptedTaxa.map{"\'$it\'"}.joinToString(" or ")} in TAXONOMY"
    }
}