package com.bp.dinodata.data.enum_readers

import android.util.Log
import com.bp.dinodata.data.DataParsing
import com.bp.dinodata.data.search.ISearchTypeConverter

/**
 * A standard type-converter for a generic type T. This enables strings to be converted to objects
 * of the given type via the property dataMap, and for search suggestions to be generated
 * from a string prefix.
 * @property dataMap A mapping from string to the target type which is used as a lookup to retrieve
 * the target type.
 * @property logTag A string to be used as the logging tag.
 */
abstract class TypeConverter<T>(
    protected val dataMap: Map<String, T>,
    protected val logTag: String = "TypeConverter"
): ISearchTypeConverter<T> {

    open fun preprocessText(text: String): String {
        return text.trim().lowercase()
    }

    override fun matchType(text: String): T? {
        val cleanText = preprocessText(text)
        return if (cleanText in dataMap.keys) {
            dataMap[cleanText]
        }
        else {
            Log.d(logTag, "Saw unfamiliar creature type $text")
            null
        }
    }

    override fun suggestSearchSuffixes(text: String, takeTop: Int): List<String> {
        val keysByCommonPrefix = DataParsing.getLongestPotentialSuffixes(text, dataMap.keys)
        return keysByCommonPrefix.take(takeTop.coerceAtLeast(1))
    }

    override fun getListOfOptions(): List<String> {
        return dataMap.keys.toList().map { it.lowercase() }
    }
}