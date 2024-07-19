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
import com.bp.dinodata.data.quantities.Weight

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
    private var taxonomy: List<String> = emptyList(),
    private var locations: List<String> = emptyList(),
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
        locations = genus.getLocations().toMutableList(),
        taxonomy = genus.getListOfTaxonomy().toMutableList()
    )

    companion object {
        const val TAG = "GenusBuilder"

        private const val NAME_KEY = "name"
        private const val DIET_KEY = "diet"
        private const val YEARS_LIVED_KEY = "years_lived_raw"
        private const val WEIGHT_KEY = "weight"
        private const val LENGTH_KEY = "length"
        private const val CREATURE_TYPE_KEY = "type"
        private const val PRONOUNCE_KEY = "pronunciation"
        private const val MEANING_KEY = "meaning"
        private const val TAXONOMY_KEY = "taxonomy"
        private const val LOCATIONS_KEY = "location"
        private const val TIME_EPOCHS_KEY = "time_period_epoch"
        const val TIME_AGES_KEY = "time_period_ages"

        fun fromDict(dict: MutableMap<String, Any>): GenusBuilder? {
            val name = dict[NAME_KEY] ?: return null
            // Only proceed if a name is given

            val builder = GenusBuilderImpl(name.toString())

            // TODO - fix parsing of time-period and ages
            dict[TIME_EPOCHS_KEY]?.let {
                if (it is ArrayList<*>) {
                    builder.setTimePeriod(it[0].toString())
                }
            }

            dict[DIET_KEY]?.let { builder.setDiet(it.toString()) }
            dict[YEARS_LIVED_KEY]?.let { builder.setYearsLived(it.toString()) }
            dict[WEIGHT_KEY]?.let { builder.setWeight(it.toString()) }
            dict[LENGTH_KEY]?.let { builder.setLength(it.toString()) }
            dict[CREATURE_TYPE_KEY]?.let { builder.setCreatureType(it.toString()) }
            dict[PRONOUNCE_KEY]?.let { builder.setNamePronunciation(it.toString()) }
            dict[MEANING_KEY]?.let { builder.setNameMeaning(it.toString()) }
            dict[TAXONOMY_KEY]?.let { builder.setTaxonomy(it as ArrayList<String>?) }
            dict[LOCATIONS_KEY]?.let { builder.setLocations(it as ArrayList<String>?) }
            return builder
        }
    }

    override fun setStartMya(startMya: String?): GenusBuilder {
        try {
            this.startMya = startMya?.toInt()
        }
        catch (ex: NumberFormatException) {
            Log.d(TAG, "Unable to convert \'$startMya\' to integer")
        }
        return this
    }

    override fun setEndMya(endMya: String?): GenusBuilder {
        try {
            this.endMya = endMya?.toInt()
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

    override fun setLocations(it: List<String>?): GenusBuilder {
        it?.let {
            this.locations = it
        }
        return this
    }

    override fun setDiet(dietStr: String?): GenusBuilder {
        this.diet = dietStr?.let { DataParsing.matchDiet(it) } ?: Diet.Unknown
        return this
    }

    override fun setCreatureType(type: String?): GenusBuilder {
        this.type = type?.let { DataParsing.matchCreatureType(it) } ?: CreatureType.Other
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

    override fun splitTimePeriodAndYears(periodAndYears: String?): GenusBuilder {
        periodAndYears?.let {
            val splits = periodAndYears.split(",")
            if (splits.size < 2) {
                Log.e("GenusBuilder", "Cannot split periodAndYears at ','")
                return this
            }
            return this.setTimePeriod(splits[0])
                       .setYearsLived(splits[1])
        }
        return this
    }

    override fun setTaxonomy(taxonomy: List<String>?): GenusBuilder {
        taxonomy?.let {
//            val taxaList = taxonomy.trim('[', ']').split(" ")
            val taxaList = taxonomy.map { it.trim(',', ' ', ';') }
            this.taxonomy = taxaList
        }
        return this
    }

    override fun setNameMeaning(meaning: String?): GenusBuilder {
        nameMeaning = meaning?.trim('\'')
        return this
    }

    override fun setNamePronunciation(pronunciation: String?): GenusBuilder {
        namePronunciation = pronunciation?.trim('\'')
        return this
    }

    override fun setLength(length: String?): GenusBuilder {
        if (length == null)
            return this

        for (entry in Length.UnitsMap) {
            val unitStr = entry.key
            val units = entry.value

            if (length.endsWith(unitStr, ignoreCase = true)) {
                // Drop the units from the end
                val valueOnly = length.substringBefore(unitStr).trim()
                val maybeLength = Length.tryMake(valueOnly, units)
                if (maybeLength != null) {
                    this.length = maybeLength
                    return this
                }
            }
        }

        // If here, then no known units were seen
        Log.d(TAG, "Unable to parse length units for string \'$length\'")
        return this
    }

    override fun setWeight(weight: String?): GenusBuilder {
        if (weight == null)
            return this

        for (entry in Weight.UnitsMap) {
            val unitStr = entry.key
            val units = entry.value

            if (weight.endsWith(unitStr, ignoreCase = true)) {
                // Drop the units from the end
                val valueOnly = weight.substringBefore(unitStr).trim()
                val maybeWeight = Weight.tryMake(valueOnly, units)
                if (maybeWeight != null) {
                    this.weight = maybeWeight
                    return this
                }
            }
        }

        Log.d(TAG, "Unable to parse weight units for string \'$weight\'")
        return this
    }

    override fun build(): Genus {

        // If years lived has not been provided but at least one year was given...
        if (yearsLived == null && (startMya ?: endMya) != null) {
            // Construct a string from the years
            yearsLived =
                if (startMya != null && endMya != null) {
                    "$startMya-$endMya MYA"
                } else {
                    (startMya?.toString() ?: "") + (endMya?.toString() ?: "") + " MYA"
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
            locations = locations,
            imageUrlDataMap = multiImageUrlMap
        )
    }
}