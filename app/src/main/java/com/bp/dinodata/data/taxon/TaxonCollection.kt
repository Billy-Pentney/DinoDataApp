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

data class TaxonCollection(
    val rootTaxa: List<ITaxon>
) {
    private val nameToTaxon: MutableMap<String, ITaxon> = mutableMapOf()
    private val isExpanded: MutableMap<ITaxon, Boolean> = mutableMapOf()

    init {
        rootTaxa.forEach {
            nameToTaxon[it.getName()] = it
        }
    }

    private fun addTaxaToMap(taxa: List<ITaxon>) {
        taxa.forEach {
            nameToTaxon[it.getName()] = it
            addTaxaToMap(it.getChildrenTaxa())
        }
    }

    fun markAsExpanded(taxon: ITaxon) {
        isExpanded[taxon] = true
    }

    fun getDisplayableTaxonList(): List<IDisplayableTaxon> {
        return rootTaxa.map {
            ExpandableTaxon(it, isExpanded[it] ?: false)
        }
    }

}
