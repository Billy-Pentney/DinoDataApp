package com.bp.dinodata.data.filters

/**
 * Trivial 'pass-through' filter which always accepts any items it receives.
 * This class avoids unnecessary computation by using a string-based filter.
 * */
class BlankFilter<T>: IFilter<T> {
    override fun acceptsItem(item: T): Boolean {
        return true
    }
}