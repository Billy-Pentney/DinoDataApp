package com.bp.dinodata.data

import android.util.Log


interface GenusBuilder {
    fun build(): Genus

    fun setDiet(dietStr: String): GenusBuilder
    fun setYearsLived(timeRange: String): GenusBuilder
    fun setTimePeriod(period: String): GenusBuilder
    fun setNamePronunciation(pronunciation: String): GenusBuilder
    fun setNameMeaning(meaning: String): GenusBuilder
    fun setLength(length: String): GenusBuilder
    fun setWeight(weight: String): GenusBuilder
    fun setCreatureType(type: String): GenusBuilder
    fun splitTimePeriodAndYears(periodAndYears: String): GenusBuilder
}



class GenusBuilderImpl(
    val name: String
): GenusBuilder {
    var nameMeaning: String? = null
    var namePronunciation: String? = null
    var diet: Diet? = null
    var yearsLived: String? = null
    var timePeriod: String? = null
    var length: String? = null
    var weight: String? = null
    var type: CreatureType = CreatureType.Other

    override fun setDiet(dietStr: String): GenusBuilder {
        this.diet = when(dietStr.lowercase()) {
            "herbivorous" -> Diet.Herbivorous
            "carnivorous" -> Diet.Carnivorous
            "piscivorous" -> Diet.Piscivorous
            "omnivorous" -> Diet.Omnivorous
            else -> null
        }
        return this
    }

    override fun setCreatureType(type: String): GenusBuilder {
        when (type.lowercase()) {
            "theropod"    -> CreatureType.Theropod_Large
            "ceratopsian" -> CreatureType.Ceratopsian
            "armoured"    -> CreatureType.Armoured
            "sauropod"    -> CreatureType.Sauropod
            "hadrosaur"   -> CreatureType.Hadrosaur
            "aquatic"     -> CreatureType.Aquatic
            "avian"       -> CreatureType.Avian
            "cenezoic"    -> CreatureType.Cenezoic
        }
        return this
    }

    override fun setYearsLived(timeRange: String): GenusBuilder {
        yearsLived = timeRange
        return this
    }

    override fun setTimePeriod(period: String): GenusBuilder {
        timePeriod = period
        return this
    }

    override fun splitTimePeriodAndYears(periodAndYears: String): GenusBuilder {
        val splits = periodAndYears.split(",")
        if (splits.size < 2) {
            Log.e("GenusBuilder", "Cannot split periodAndYears at ','")
            return this
        }
        return setTimePeriod(splits[0])
              .setYearsLived(splits[1])
    }

    override fun setNameMeaning(meaning: String): GenusBuilder {
        nameMeaning = meaning
        return this
    }

    override fun setNamePronunciation(pronunciation: String): GenusBuilder {
        namePronunciation = pronunciation
        return this
    }

    override fun setLength(length: String): GenusBuilder {
        this.length = length
        return this
    }

    override fun setWeight(weight: String): GenusBuilder {
        this.weight = weight
        return this
    }

    override fun build(): Genus {
        return Genus(
            name = name,
            diet = diet,
            length = length,
            weight = weight,
            yearsLived = yearsLived,
            timePeriod = timePeriod,
            nameMeaning = nameMeaning,
            namePronunciation = namePronunciation,
            type = type
        )
    }
}