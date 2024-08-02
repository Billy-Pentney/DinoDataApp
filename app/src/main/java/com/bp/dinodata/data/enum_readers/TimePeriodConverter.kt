package com.bp.dinodata.data.enum_readers

import com.bp.dinodata.data.DataParsing
import com.bp.dinodata.data.Epoch
import com.bp.dinodata.data.Subepoch
import com.bp.dinodata.data.TimePeriod
import com.bp.dinodata.data.search.ISearchTypeConverter

object TimePeriodConverter: ISearchTypeConverter<TimePeriod> {
    private val TimePeriodMap = mapOf(
        "cretaceous" to Epoch.Cretaceous,
        "jurassic" to Epoch.Jurassic,
        "triassic" to Epoch.Triassic
    )

    override fun matchType(text: String): TimePeriod? {
        val splits = text.split(" ")
        var subepoch: Subepoch? = null
        var epoch: Epoch? = null

        for (split in splits) {
            when (split.lowercase()) {
                "cretaceous" -> { epoch = Epoch.Cretaceous
                }
                "jurassic" -> { epoch = Epoch.Jurassic
                }
                "triassic" -> { epoch = Epoch.Triassic
                }
                "early" -> { subepoch = Subepoch.Early
                }
                "middle" -> { subepoch = Subepoch.Middle
                }
                "late" -> { subepoch = Subepoch.Late
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