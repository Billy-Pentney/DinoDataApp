package com.bp.dinodata.data

interface IBuilder<T> {
    /**
     * Resets all fields for this builder to their default value,
     * thus enabling a new object to be created.
     */
    fun clear(): IBuilder<T>

    /**
     * Constructs a new object of the given type, using the current status
     * of the internal builder fields.
     */
    fun build(): T
}

interface IDictParser<T> {
    /**
     * Attempt to construct a builder using the fields of the given map.
     * This method is intended to be called when parsing data from an abstract, untyped
     * representation, and will fail if the given map does not contain the minimum required
     * fields (typically only a name).
     */
    fun fromDict(dataMap: Map<*,*>): IBuilder<T>?
}
