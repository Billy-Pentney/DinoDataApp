package com.bp.dinodata.data.enum_readers

import android.util.Log
import com.bp.dinodata.data.DataParsing
import com.bp.dinodata.data.Diet
import com.bp.dinodata.data.search.ISearchTypeConverter

object DietConverter: TypeConverter<Diet>(
    mapOf(
        "carnivore" to Diet.Carnivore,
        "herbivore" to Diet.Herbivore,
        "omnivore" to Diet.Omnivore,
        "piscivore" to Diet.Piscivore,
        "unknown" to Diet.Unknown
    ),
    "DietConverter"
) {
    override fun preprocessText(text: String): String {
        return text.lowercase().trim().replace("vorous", "vore")
    }

    override fun matchType(text: String): Diet? {
        val cleanText = this.preprocessText(text)
        val matchedType = dataMap[cleanText]

        return if (matchedType != null) {
            matchedType
        }
        else {
            Log.d("DietParser", "Saw unfamiliar diet $text")
            null
        }
    }
}