package com.bp.dinodata.data.genus

import android.util.Log
import com.bp.dinodata.data.CreatureType
import com.bp.dinodata.data.DataParsing
import com.bp.dinodata.data.Diet
import com.bp.dinodata.data.MultiImageUrlData
import com.bp.dinodata.data.TimePeriod
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
    fun setYearsLived(timeRange: String?): GenusBuilder
    fun setTimePeriod(period: String?): GenusBuilder
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
    private val name: String,
    private var nameMeaning: String? = null,
    private var namePronunciation: String? = null,
    private var diet: Diet = Diet.Unknown,
    private var yearsLived: String? = null,
    private var timePeriod: TimePeriod? = null,
    private var length: IDescribesLength? = null,
    private var weight: IDescribesWeight? = null,
    private var type: CreatureType = CreatureType.Other,
    private var taxonomy: MutableList<String> = mutableListOf(),
    private var startMya: Int? = null,
    private var endMya: Int? = null,
): GenusBuilder {

    private var multiImageUrlMap: Map<String, MultiImageUrlData> = emptyMap()

    constructor(genus: IGenus): this(
        name = genus.getName(),
        nameMeaning = genus.getNameMeaning(),
        namePronunciation = genus.getNamePronunciation(),
        diet = genus.getDiet(),
        yearsLived = genus.getYearsLived(),
        timePeriod = genus.getTimePeriod(),
        length = genus.getLength(),
        weight = genus.getWeight(),
        type = genus.getCreatureType(),
        taxonomy = genus.getListOfTaxonomy().toMutableList()
    )

    companion object {
        const val TAG = "GenusBuilder"

        fun fromDict(dict: MutableMap<String, Any>): GenusBuilder? {
            val name = dict["name"] ?: return null
            // Only proceed if a name is given

            val builder = GenusBuilderImpl(name.toString())

            // TODO - fix parsing of time-period and ages
            dict["time_period_epoch"]?.let {
                if (it is ArrayList<*>) {
                    builder.setTimePeriod(it[0].toString())
                }
            }

            dict["diet"]?.let { builder.setDiet(it.toString()) }
            dict["years_lived_raw"]?.let { builder.setYearsLived(it.toString()) }
            dict["weight"]?.let { builder.setWeight(it.toString()) }
            dict["length"]?.let { builder.setLength(it.toString()) }
            dict["type"]?.let { builder.setCreatureType(it.toString()) }
            dict["pronunciation"]?.let { builder.setNamePronunciation(it.toString()) }
            dict["name_meaning"]?.let { builder.setNameMeaning(it.toString()) }
            dict["meaning"]?.let { builder.setNameMeaning(it.toString()) }
            dict["taxonomy"]?.let { builder.setTaxonomy(it.toString()) }
            return builder
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
        this.diet = DataParsing.matchDiet(dietStr) ?: Diet.Unknown
        return this
    }

    override fun setCreatureType(type: String): GenusBuilder {
        this.type = DataParsing.matchCreatureType(type) ?: CreatureType.Other
        return this
    }

    override fun setYearsLived(timeRange: String?): GenusBuilder {
        yearsLived = timeRange
        return this
    }

    override fun setTimePeriod(period: String?): GenusBuilder {
        timePeriod = period?.let { DataParsing.matchTimePeriod(it) }
        return this
    }

    override fun splitTimePeriodAndYears(periodAndYears: String): GenusBuilder {
        val splits = periodAndYears.split(",")
        if (splits.size < 2) {
            Log.e("GenusBuilder", "Cannot split periodAndYears at ','")
            return this
        }
        return setTimePeriod(splits[0]).setYearsLived(splits[1])
    }

    override fun setTaxonomy(taxonomy: String): GenusBuilder {
        for (taxon in taxonomy.trim('[',']').split(" ")) {
            val cleanTaxon = taxon.trim(',', ' ', ';')
            this.taxonomy.add(cleanTaxon)
        }
        return this
    }

    override fun setNameMeaning(meaning: String): GenusBuilder {
        nameMeaning = meaning.trim('\'')
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