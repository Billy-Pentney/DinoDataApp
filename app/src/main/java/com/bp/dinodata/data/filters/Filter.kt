package com.bp.dinodata.data.filters

/**
 * A filter defines one or more restrictions on items of type T.
 * This can be used to identify the elements of that type which meet some restriction
 * e.g. a search query.
 */
interface IFilter<T> {
    /** Returns true if and only if the given item matches this filter. */
    fun acceptsItem(item: T): Boolean
    fun<R:T> applyTo(list: Iterable<R>): List<T> {
        return list.filter { acceptsItem(it) }
    }
}


//class MeasurementFilter(
//    private val length: IDescribesLength
//): IFilter<IHasMeasurements> {
//    override fun acceptsItem(item: IHasMeasurements): Boolean {
//        val length = item.getLength()
//        length?.
//    }
//}


