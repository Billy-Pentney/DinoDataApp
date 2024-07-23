package com.bp.dinodata.data.search

interface IGeneratesSearchSuggestions {
    fun suggestSearchSuffixes(text: String, takeTop: Int = 2): List<String>
    fun getListOfOptions(): List<String>
}

interface IParsesType<T> {
    fun matchType(text: String): T?
}

interface ISearchTypeConverter<T>: IParsesType<T>, IGeneratesSearchSuggestions