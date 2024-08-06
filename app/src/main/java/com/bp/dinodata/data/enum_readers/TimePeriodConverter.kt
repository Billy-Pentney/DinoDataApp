package com.bp.dinodata.data.enum_readers

import com.bp.dinodata.data.time_period.CenozoicEpoch
import com.bp.dinodata.data.DataParsing
import com.bp.dinodata.data.time_period.Epoch
import com.bp.dinodata.data.time_period.MesozoicEpoch
import com.bp.dinodata.data.time_period.PaleozoicEpoch
import com.bp.dinodata.data.time_period.Subepoch
import com.bp.dinodata.data.time_period.TimePeriod
import com.bp.dinodata.data.search.ISearchTypeConverter

object TimePeriodConverter: ISearchTypeConverter<TimePeriod> {
    private val TimePeriodMap = mapOf(
        "cretaceous" to MesozoicEpoch.Cretaceous,
        "jurassic" to MesozoicEpoch.Jurassic,
        "triassic" to MesozoicEpoch.Triassic,
        "cambrian" to PaleozoicEpoch.Cambrian,
        "devonian" to PaleozoicEpoch.Devonian,
        "carboniferous" to PaleozoicEpoch.Carboniferous,
        "silurian" to PaleozoicEpoch.Silurian,
        "ordovician" to PaleozoicEpoch.Ordovician,
        "permian" to PaleozoicEpoch.Permian,
        "neogene" to CenozoicEpoch.Neogene,
        "paleogene" to CenozoicEpoch.Paleogene,
        "quaternary" to CenozoicEpoch.Quaternary
    )

    override fun matchType(text: String): TimePeriod? {
        val splits = text.split(" ")
        var subepoch: Subepoch? = null
        var epoch: Epoch? = null

        for (split in splits) {
            val lowerSplit = split.lowercase()

            val foundEpoch = TimePeriodMap[lowerSplit]

            if (foundEpoch != null) {
                epoch = foundEpoch
            }
            else {
                when (split.lowercase()) {
                    "early" -> subepoch = Subepoch.Early
                    "middle" -> subepoch = Subepoch.Middle
                    "late" -> subepoch = Subepoch.Late
                }
            }
        }
        return epoch?.let { TimePeriod(it, subepoch) }
    }

    override fun suggestSearchSuffixes(text: String, takeTop: Int): List<String> {
        val keysByCommonPrefix = DataParsing.getLongestPotentialSuffixes(text, TimePeriodMap.keys)
        return keysByCommonPrefix.take(takeTop.coerceAtLeast(1))
    }

    override fun getListOfOptions(): List<String> {
        return TimePeriodMap.keys.toList()
    }
}