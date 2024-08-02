package com.bp.dinodata.data.enum_readers

import android.util.Log
import com.bp.dinodata.data.DataParsing
import com.bp.dinodata.data.Diet
import com.bp.dinodata.data.search.ISearchTypeConverter

object DietConverter: ISearchTypeConverter<Diet> {

    private val DietTypesMap = mapOf(
        "carnivore" to Diet.Carnivore,
        "herbivore" to Diet.Herbivore,
        "omnivore" to Diet.Omnivore,
        "piscivore" to Diet.Piscivore
    )

    override fun matchType(text: String): Diet? {
        val cleanText = text.trim().lowercase()
        return when {
            cleanText.startsWith("herbivor") -> Diet.Herbivore
            cleanText.startsWith("carnivor") -> Diet.Carnivore
            cleanText.startsWith("piscivor") -> Diet.Piscivore
            cleanText.startsWith("omnivor") -> Diet.Omnivore
            cleanText == "unknown" -> Diet.Unknown
            else -> {
                Log.d("DietParser", "Saw unfamiliar diet $text")
                null
            }
        }
    }

    override fun suggestSearchSuffixes(text: String, takeTop: Int): List<String> {
        val keysByCommonPrefix = DataParsing.getLongestPotentialSuffixes(text, DietTypesMap.keys)
        return keysByCommonPrefix.take(takeTop.coerceAtLeast(1))
    }

    override fun getListOfOptions(): List<String> {
        return DietTypesMap.keys.toList()
    }
}