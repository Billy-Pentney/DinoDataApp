package com.bp.dinodata.data.filters

import com.bp.dinodata.data.CreatureType
import com.bp.dinodata.data.Diet
import com.bp.dinodata.data.genus.IGenus
import com.bp.dinodata.data.genus.IHasCreatureType
import com.bp.dinodata.data.genus.IHasDiet
import com.bp.dinodata.data.genus.IHasName
import com.bp.dinodata.data.genus.IHasTaxonomy

/**
 * A filter defines one or more restrictions on items of type T.
 * This can be used to identify the elements of that type which meet some restriction
 * e.g. a search query.
 */
interface IFilter<T> {
    /** Returns true if and only if the given item matches this filter. */
    fun acceptsItem(item: T): Boolean
    fun applyTo(list: Iterable<T>): List<T> {
        return list.filter { acceptsItem(it) }
    }
}


class NameFilter(
    private val nameQuery: String,
    private val capitalSensitive: Boolean
): IFilter<IHasName> {
    override fun acceptsItem(item: IHasName): Boolean {
        val itemName = item.getName()
        return if (!capitalSensitive) {
            itemName.lowercase().contains(nameQuery, true)
        } else {
            itemName.contains(nameQuery, true)
        }
    }

    override fun toString(): String {
        return "\'$nameQuery\' in NAME"
    }
}

class CreatureTypeFilter(
    private val requiredTypes: List<CreatureType>
): IFilter<IHasCreatureType> {
    override fun acceptsItem(item: IHasCreatureType): Boolean = item.getCreatureType() in requiredTypes
    override fun toString(): String {
        return "TYPE in [${requiredTypes.joinToString(",")}]"
    }
}

class TaxonFilter(
    private val taxonSearch: String
): IFilter<IHasTaxonomy> {
    override fun acceptsItem(item: IHasTaxonomy): Boolean {
        val taxonList = item.getListOfTaxonomy()
        for (taxon in taxonList) {
            if (taxonSearch in taxon) {
                return true
            }
        }
        return false
    }

    override fun toString(): String {
        return "\'$taxonSearch\' in TAXONOMY"
    }
}

class DietFilter(
    private val acceptedDiets: List<Diet>
): IFilter<IHasDiet> {
    override fun acceptsItem(item: IHasDiet): Boolean {
        return item.getDiet() in acceptedDiets
    }
    override fun toString(): String {
        return "DIET in [${acceptedDiets.joinToString(",")}]"
    }
}

interface ICompositeFilter<T>: IFilter<T> {
    fun addFilter(filter: IFilter<T>)
}


/**
 * A filter which is comprised of zero or more sub-filters and only accepts if and only if
 * ALL of the subfilters accept.
 */
class ConjunctiveFilter<T>(
    filters: List<IFilter<in T>>
): ICompositeFilter<T> {
    private val _filters = filters.toMutableList()

    override fun acceptsItem(item: T): Boolean {
        // Accept if and only if ALL the filters accept
        return _filters.fold(true) { state, filter ->
            state && filter.acceptsItem(item)
        }
    }

    override fun addFilter(filter: IFilter<T>) {
        _filters.add(filter)
    }

    override fun toString(): String {
        return _filters.joinToString(" && ")
    }
}

