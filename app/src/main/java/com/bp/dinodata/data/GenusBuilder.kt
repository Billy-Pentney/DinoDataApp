package com.bp.dinodata.data

import android.util.Log
import com.bp.dinodata.data.quantities.IDescribesLength
import com.bp.dinodata.data.quantities.IDescribesWeight
import com.bp.dinodata.data.quantities.Length
import com.bp.dinodata.data.quantities.LengthRange
import com.bp.dinodata.data.quantities.Units
import com.bp.dinodata.data.quantities.Weight
import com.bp.dinodata.data.quantities.WeightRange


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
    fun setTaxonomy(taxonomy: String): GenusBuilder
    fun setStartMya(startMya: String): GenusBuilder
    fun setEndMya(endMya: String): GenusBuilder

    fun addImageUrlMap(imageData: Map<String, MultiImageUrlData>): GenusBuilder
}






class GenusBuilderImpl(
    private val name: String
): GenusBuilder {
    private var nameMeaning: String? = null
    private var namePronunciation: String? = null
    private var diet: Diet? = null
    private var yearsLived: String? = null
    private var timePeriod: String? = null
    private var length: IDescribesLength? = null
    private var weight: IDescribesWeight? = null
    private var type: CreatureType = CreatureType.Other
    private var taxonomy: MutableList<String> = mutableListOf()
    private var startMya: Int? = null
    private var endMya: Int? = null

    private var multiImageUrlMap: Map<String, MultiImageUrlData> = emptyMap()


    companion object {
        const val TAG = "GenusBuilder"

        fun fromDict(dict: MutableMap<String, Any>): GenusBuilder? {
            val name = dict["name"]
            // Only proceed if a name is given
            return name?.let {
                val builder = GenusBuilderImpl(name.toString())
                dict["time_period"]?.let { builder.setTimePeriod(it.toString()) }
                dict["diet"]?.let { builder.setDiet(it.toString()) }
                dict["years_lived"]?.let { builder.setYearsLived(it.toString()) }
                dict["weight"]?.let { builder.setWeight(it.toString()) }
                dict["length"]?.let { builder.setLength(it.toString()) }
                dict["type"]?.let { builder.setCreatureType(it.toString()) }
                dict["pronunciation"]?.let { builder.setNamePronunciation(it.toString()) }
//                dict["namePronunciation"]?.let { builder.setNamePronunciation(it.toString()) }
                dict["name_meaning"]?.let { builder.setNameMeaning(it.toString()) }
                dict["taxonomy"]?.let { builder.setTaxonomy(it.toString()) }
                builder
            }
        }
    }

    override fun setStartMya(startMya: String): GenusBuilder {
        try {
            this.startMya = startMya.toInt()
        }
        catch (ex: NumberFormatException) {
            Log.d(TAG, "Unable to convert \'$startMya\' to integer")
        }
        return this
    }

    override fun setEndMya(endMya: String): GenusBuilder {
        try {
            this.endMya = endMya.toInt()
        }
        catch (ex: NumberFormatException) {
            Log.d(TAG, "Unable to convert \'$endMya\' to integer")
        }
        return this
    }

    override fun addImageUrlMap(imageData: Map<String, MultiImageUrlData>): GenusBuilder {
        this.multiImageUrlMap = imageData
        return this
    }

    override fun setDiet(dietStr: String): GenusBuilder {
        this.diet = when(dietStr.trim().lowercase()) {
            "herbivorous",
            "herbivore"
                -> Diet.Herbivore
            "carnivorous",
            "carnivore"
                -> Diet.Carnivore
            "piscivorous",
            "piscivore"
                -> Diet.Piscivore
            "omnivorous",
            "omnivore"
                -> Diet.Omnivore
            else -> {
                Log.d("GenusBuilder", "Saw unknown diet value \'${dietStr}\'")
                Diet.Unknown
            }
        }
        return this
    }

    override fun setCreatureType(type: String): GenusBuilder {
        this.type = when (type.lowercase()) {
            "large theropod"    -> CreatureType.LargeTheropod
            "small theropod"    -> CreatureType.SmallTheropod
            "ceratopsian"       -> CreatureType.Ceratopsian
            "ankylosaurid", "armoured dinosaur"        -> CreatureType.Ankylosaur
            "stegosaur"         -> CreatureType.Stegosaur
            "pachycephalosaur"  -> CreatureType.Pachycephalosaur
            "sauropod"          -> CreatureType.Sauropod
            "spinosaur"         -> CreatureType.Spinosaur
            "hadrosaur"         -> CreatureType.Hadrosaur
            "ornithomimid"      -> CreatureType.Ornithomimid
            "aquatic"           -> CreatureType.Aquatic
            "pterosaur"         -> CreatureType.Pterosaur
            "cenezoic"          -> CreatureType.Cenezoic
            "synapsid"          -> CreatureType.Synapsid
            else                -> CreatureType.Other
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

    override fun setTaxonomy(taxonomy: String): GenusBuilder {
        for (taxon in taxonomy.trim('[',']').split(" ")) {
            val cleanTaxon = taxon.trim(',', ' ', ';')
            this.taxonomy.add(cleanTaxon)
        }
        return this
    }

    override fun setNameMeaning(meaning: String): GenusBuilder {
        nameMeaning = meaning
        return this
    }

    override fun setNamePronunciation(pronunciation: String): GenusBuilder {
        namePronunciation = pronunciation.trim('\'')
        return this
    }

    private fun tryParseLength(lengthValue: String, units: Units.Length) {
        try {
            if ("-" in lengthValue) {
                // Parse a range e.g. "98.1-100"
                val splits = lengthValue.split("-")
                val lower = splits[0].trim().toFloat()
                val upper = splits[1].trim().toFloat()
                this.length = LengthRange(lower, upper, units)
            }
            else {
                // Parse a single value
                val floatValue = lengthValue.toFloat()
                this.length = Length.make(floatValue, units)
            }
        }
        catch (nfe:NumberFormatException) {
            Log.e("GenusBuilder", "Failed to parse \'$lengthValue\' as float")
        }
    }

    override fun setLength(length: String): GenusBuilder {
        if (length.endsWith("metres", ignoreCase = true)) {
            // Drop the units from the end
            val valueOnly = length.substringBefore("metres").trim()
            tryParseLength(valueOnly, Units.Length.Metres)
        }
        else if (length.endsWith("cm", ignoreCase = true)) {
            val valueOnly = length.substringBefore("cm").trim()
            tryParseLength(valueOnly, Units.Length.Centimetres)
        }
        else if (length.endsWith("ft", ignoreCase = true)) {
            val valueOnly = length.substringBefore("ft").trim()
            tryParseLength(valueOnly, Units.Length.Feet)
        }
        else if (length.endsWith("m", ignoreCase = true)) {
            val valueOnly = length.substringBefore("m").trim()
            tryParseLength(valueOnly, Units.Length.Metres)
        }
        else {
            Log.d(TAG, "Unable to parse length units for string \'$length\'")
        }
        return this
    }

    private fun tryParseWeight(weightValue: String, units: Units.Weight) {
        try {
            if ("-" in weightValue) {
                // Parse a range e.g. "100-98.1"
                val splits = weightValue.split("-")
                val lower = splits[0].toFloat()
                val upper = splits[1].toFloat()
                this.weight = WeightRange(lower, upper, units)
            }
            else {
                // Parse a single value
                val floatValue = weightValue.toFloat()
                this.weight = Weight.make(floatValue, units)
            }
        }
        catch (nfe:NumberFormatException) {
            Log.e(TAG, "Failed to parse \'$weightValue\' as float")
            return
        }

        if (units == Units.Weight.Kg && this.weight?.isAtLeast(1000f) == true) {
            this.weight = this.weight?.convert(Units.Weight.Tonnes)
        }
        else if (units == Units.Weight.Tonnes && this.weight?.isAtMost(1f) == true) {
            this.weight = this.weight?.convert(Units.Weight.Kg)
        }
    }

    override fun setWeight(weight: String): GenusBuilder {
        if (weight.endsWith("tonnes", ignoreCase = true)) {
            val valueOnly = weight.substringBefore("tonnes").trim()
            tryParseWeight(valueOnly, Units.Weight.Tonnes)
        }
        else if (weight.endsWith("kg", ignoreCase = true)) {
            val valueOnly = weight.substringBefore("kg").trim()
            tryParseWeight(valueOnly, Units.Weight.Kg)
        }
        else if (weight.endsWith("tons", ignoreCase = true)) {
            val valueOnly = weight.substringBefore("tons").trim()
            tryParseWeight(valueOnly, Units.Weight.TonsImperial)
        }
        else {
            Log.d(TAG, "Unable to parse weight units for string \'$weight\'")
        }
        return this
    }

    override fun build(): Genus {

        if (yearsLived == null) {
            yearsLived = if (startMya != null && endMya != null) {
                "$startMya-$endMya MYA"
            } else {
                (startMya?.toString() ?: "") + (endMya?.toString() ?: "")
            }
        }

        return Genus(
            name = name,
            diet = diet,
            length = length,
            weight = weight,
            yearsLived = yearsLived,
            timePeriod = timePeriod,
            nameMeaning = nameMeaning,
            namePronunciation = namePronunciation,
            type = type,
            taxonomy = taxonomy,
            imageUrlDataMap = multiImageUrlMap
        )
    }
}