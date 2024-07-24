package com.bp.dinodata.data.genus

import android.util.Log
import com.bp.dinodata.data.CreatureType
import com.bp.dinodata.data.CreatureTypeConverter
import com.bp.dinodata.data.Diet
import com.bp.dinodata.data.DietConverter
import com.bp.dinodata.data.IBuilder
import com.bp.dinodata.data.TimePeriod
import com.bp.dinodata.data.TimePeriodConverter
import com.bp.dinodata.data.genus.species.SpeciesBuilder
import com.bp.dinodata.data.quantities.IDescribesLength
import com.bp.dinodata.data.quantities.IDescribesWeight
import com.bp.dinodata.data.quantities.Length
import com.bp.dinodata.data.quantities.Weight

class GenusBuilder(
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
    private var species: List<ISpecies> = emptyList()
): IGenusBuilder {

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
        private const val SPECIES_KEY = "species"
        const val TIME_AGES_KEY = "time_period_ages"

        fun fromDict(dataMap: Map<*, *>): IBuilder<IGenus>? {
            val name = (dataMap[NAME_KEY] as String?) ?: return null
            return GenusBuilder(name).fromDict(
                dataMap
            )
        }
    }

    override fun clear(): IGenusBuilder {
        nameMeaning = null
        namePronunciation = null
        diet = Diet.Unknown
        yearsLived = null
        timePeriod = null
        weight = null
        length = null
        type = CreatureType.Other
        taxonomy = emptyList()
        locations = emptyList()
        species = emptyList()
        startMya = null
        endMya = null
        return this
    }

    override fun fromDict(dataMap: Map<*, *>): IBuilder<IGenus>? {
        val name = dataMap[NAME_KEY] ?: return null
        // Only proceed if a name is given

        val builder = this

        // TODO - fix parsing of time-period and ages
        dataMap[TIME_EPOCHS_KEY]?.let {
            if (it is ArrayList<*>) {
                builder.setTimePeriod(it[0].toString())
            }
        }

        dataMap[DIET_KEY]?.let { builder.setDiet(it.toString()) }
        dataMap[YEARS_LIVED_KEY]?.let { builder.setYearsLived(it.toString()) }
        dataMap[WEIGHT_KEY]?.let { builder.setWeight(it.toString()) }
        dataMap[LENGTH_KEY]?.let { builder.setLength(it.toString()) }
        dataMap[CREATURE_TYPE_KEY]?.let { builder.setCreatureType(it.toString()) }
        dataMap[PRONOUNCE_KEY]?.let { builder.setNamePronunciation(it.toString()) }
        dataMap[MEANING_KEY]?.let { builder.setNameMeaning(it.toString()) }
        dataMap[TAXONOMY_KEY]?.let { builder.setTaxonomy(it) }
        dataMap[LOCATIONS_KEY]?.let { builder.setLocations(it) }

        dataMap[SPECIES_KEY]?.let {
            builder.setSpecies(it)
        }
        return builder
    }

    override fun setStartMya(startMya: String?): IGenusBuilder {
        try {
            this.startMya = startMya?.toInt()
        }
        catch (ex: NumberFormatException) {
            Log.d(TAG, "Unable to convert \'$startMya\' to integer")
        }
        return this
    }

    override fun setEndMya(endMya: String?): IGenusBuilder {
        try {
            this.endMya = endMya?.toInt()
        }
        catch (ex: NumberFormatException) {
            Log.d(TAG, "Unable to convert \'$endMya\' to integer")
        }
        return this
    }

//    override fun addImageUrlMap(imageData: Map<String, MultiImageUrlData>): GenusBuilder {
//        this.multiImageUrlMap = imageData
//        return this
//    }

    override fun setLocations(locations: Any): IGenusBuilder {
        if (locations is List<*>) {
            this.locations = locations.mapNotNull { it.toString() }
        }
        return this
    }

    override fun setSpecies(speciesData: Any): IGenusBuilder {
        if (speciesData !is List<*>) {
            Log.i("GenusBuilder", "Found non-list species data")
            return this
        }

        val speciesBuilder = SpeciesBuilder("")
        val species = mutableListOf<ISpecies>()
        for (speciesInfo in speciesData) {
            if (speciesInfo !is Map<*, *>) {
                continue
            }
            val speciesDataMap = speciesInfo as Map<*, *>?
            if (speciesDataMap != null) {
                speciesBuilder.fromDict(speciesDataMap)
                speciesBuilder.build().let {
                    Log.i(TAG, "Successfully parsed species ${it.getName()}!")
                    species.add(it)
                }
            }
        }
        this.species = species
        return this
    }

    override fun setDiet(dietStr: String?): IGenusBuilder {
        this.diet = dietStr?.let { DietConverter.matchType(it) } ?: Diet.Unknown
        return this
    }

    override fun setCreatureType(type: String?): IGenusBuilder {
        this.type = type?.let { CreatureTypeConverter.matchType(it) } ?: CreatureType.Other
        return this
    }

    override fun setYearsLived(timeRange: String?): IGenusBuilder {
        yearsLived = timeRange
        return this
    }

    override fun setTimePeriod(period: String?): IGenusBuilder {
        timePeriod = period?.let { TimePeriodConverter.matchType(it) }
        return this
    }

    override fun splitTimePeriodAndYears(periodAndYears: String?): IGenusBuilder {
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

    override fun setTaxonomy(taxonomy: Any): IGenusBuilder {
        if (taxonomy is List<*>) {
            taxonomy.let {
                this.taxonomy = taxonomy.mapNotNull {
                    if (it is String) {
                        it.trim(',', ' ', ';').substringBefore(" ")
                    }
                    else {
                        null
                    }
                }
            }
        }
        return this
    }

    override fun setNameMeaning(meaning: String?): IGenusBuilder {
        nameMeaning = meaning?.trim('\'')
        return this
    }

    override fun setNamePronunciation(pronunciation: String?): IGenusBuilder {
        namePronunciation = pronunciation?.trim('\'')
        return this
    }

    override fun setLength(length: String?): IGenusBuilder {
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

    override fun setWeight(weight: String?): IGenusBuilder {
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
            species = species
        )
    }
}