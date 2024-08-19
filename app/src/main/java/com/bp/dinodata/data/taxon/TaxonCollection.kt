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
    fun getTaxonByName(name: String): ITaxon?
    fun getRoots(): List<ITaxon>
    fun isEmpty(): Boolean
}

class TaxonCollection(
    private val rootTaxa: List<ITaxon>,
    taxaByName: Map<String, ITaxon> = emptyMap(),
    isTaxonExpanded: Map<ITaxon, Boolean> = emptyMap()
): ITaxonCollection {

    private val _taxonDictionary = taxaByName.toMutableMap()
    private val _isTaxonExpanded = isTaxonExpanded.toMutableMap()

    init {
        rootTaxa.forEach {
            _taxonDictionary[it.getName()] = it
        }
    }

    private fun addTaxaToMap(taxa: List<ITaxon>) {
        taxa.forEach {
            _taxonDictionary[it.getName()] = it
            addTaxaToMap(it.getChildrenTaxa())
        }
    }

    fun markAsExpanded(taxon: ITaxon) {
        _isTaxonExpanded[taxon] = true
    }

//    fun getDisplayableTaxonList(): List<IDisplayableTaxon> {
//        return rootTaxa.map {
//            ExpandableTaxon(it, isExpanded[it] ?: false)
//        }
//    }

    override fun getTaxonByName(name: String): ITaxon? = _taxonDictionary[name]
    override fun getRoots(): List<ITaxon> = rootTaxa
    override fun isEmpty(): Boolean = rootTaxa.isEmpty()

    companion object {
        fun buildTaxaFromParentMapping(taxonParents: Map<String, String>): Map<String, IMutableTaxon> {
            val taxaMap: MutableMap<String, IMutableTaxon> = mutableMapOf()



            return taxaMap
        }
    }

}
