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
    private val name: String
): GenusBuilder {
    private var nameMeaning: String? = null
    private var namePronunciation: String? = null
    private var diet: Diet? = null
    private var yearsLived: String? = null
    private var timePeriod: String? = null
    private var length: String? = null
    private var weight: String? = null
    private var type: CreatureType = CreatureType.Other

    companion object {
        fun fromDict(dict: MutableMap<String, Any>): GenusBuilder? {
            val name = dict["name"]
            // Only proceed if a name is given
            return name?.let {
                val builder = GenusBuilderImpl(name.toString())
                dict["timePeriod"]?.let { builder.setTimePeriod(it.toString()) }
                dict["diet"]?.let { builder.setDiet(it.toString()) }
                dict["yearsLived"]?.let { builder.setYearsLived(it.toString()) }
                dict["weight"]?.let { builder.setWeight(it.toString()) }
                dict["length"]?.let { builder.setLength(it.toString()) }
                dict["type"]?.let { builder.setCreatureType(it.toString()) }
                builder
            }
        }
    }

    override fun setDiet(dietStr: String): GenusBuilder {
        this.diet = when(dietStr.lowercase()) {
            "herbivorous" -> Diet.Herbivore
            "carnivorous" -> Diet.Carnivore
            "piscivorous" -> Diet.Piscivore
            "omnivorous" -> Diet.Omnivore
            else -> Diet.Unknown
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
            else          -> CreatureType.Other
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