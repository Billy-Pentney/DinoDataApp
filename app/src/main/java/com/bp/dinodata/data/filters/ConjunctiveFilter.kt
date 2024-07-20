package com.bp.dinodata.data.filters

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