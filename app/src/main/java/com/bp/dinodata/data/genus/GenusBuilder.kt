package com.bp.dinodata.data.genus

import android.util.Log
import com.bp.dinodata.data.CreatureType
import com.bp.dinodata.data.enum_readers.CreatureTypeConverter
import com.bp.dinodata.data.Diet
import com.bp.dinodata.data.enum_readers.DietConverter
import com.bp.dinodata.data.IBuilder
import com.bp.dinodata.data.MyBuilderUtils
import com.bp.dinodata.data.enum_readers.EpochConverter
import com.bp.dinodata.data.genus.species.SpeciesBuilder
import com.bp.dinodata.data.quantities.IDescribesLength
import com.bp.dinodata.data.quantities.IDescribesMass
import com.bp.dinodata.data.quantities.Length
import com.bp.dinodata.data.quantities.Mass
import com.bp.dinodata.data.time_period.IDisplayableTimePeriod
import com.bp.dinodata.data.time_period.epochs.IProvidesEpoch
import com.bp.dinodata.data.time_period.intervals.TimeInterval
import com.bp.dinodata.data.time_period.intervals.TimeMarker
import com.bp.dinodata.data.time_period.epochs.EpochRetriever

class GenusBuilder(
    private val name: String,
    private var nameMeaning: String? = null,
    private var namePronunciation: String? = null,
    private var diet: Diet = Diet.Unknown,
    private var timePeriods: MutableList<IDisplayableTimePeriod> = mutableListOf(),
    private var length: IDescribesLength? = null,
    private var weight: IDescribesMass? = null,
    private var type: CreatureType = CreatureType.Other,
    private var taxonomy: List<String> = emptyList(),
    private var locations: List<String> = emptyList(),
    private var formations: List<String> = emptyList(),
    private var startMya: Float? = null,
    private var endMya: Float? = null,
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
        private const val START_MYA_KEY = "start_mya"
        private const val END_MYA_KEY = "end_mya"
        private const val FORMATIONS_KEY = "formations"
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
        timePeriods = mutableListOf()
        weight = null
        length = null
        type = CreatureType.Other
        taxonomy = emptyList()
        locations = emptyList()
        formations = emptyList()
        species = emptyList()
        startMya = null
        endMya = null
        return this
    }

    override fun fromDict(dataMap: Map<*, *>): IBuilder<IGenus>? {
        val name = dataMap[NAME_KEY] ?: return null
        // Only proceed if a name is given

        val builder = this

        dataMap[TIME_EPOCHS_KEY]?.let {
            val timePeriods = MyBuilderUtils.parseToStringList(it)
            builder.setTimePeriods(timePeriods)
        }

        dataMap[DIET_KEY]?.let { builder.setDiet(it.toString()) }
        dataMap[START_MYA_KEY]?.let { builder.setStartMya(it.toString()) }
        dataMap[END_MYA_KEY]?.let { builder.setEndMya(it.toString()) }

        dataMap[WEIGHT_KEY]?.let { builder.setWeight(it.toString()) }
        dataMap[LENGTH_KEY]?.let { builder.setLength(it.toString()) }
        dataMap[CREATURE_TYPE_KEY]?.let { builder.setCreatureType(it.toString()) }
        dataMap[PRONOUNCE_KEY]?.let { builder.setNamePronunciation(it.toString()) }
        dataMap[MEANING_KEY]?.let { builder.setNameMeaning(it.toString()) }
        dataMap[TAXONOMY_KEY]?.let { builder.setTaxonomy(it) }
        dataMap[LOCATIONS_KEY]?.let {
            val locations = MyBuilderUtils.parseToStringList(it)
            builder.setLocations(locations)
        }
        dataMap[FORMATIONS_KEY]?.let {
            val formations = MyBuilderUtils.parseToStringList(it)
            builder.setFormations(formations)
        }
        dataMap[SPECIES_KEY]?.let { builder.setSpecies(it) }
        return builder
    }

    override fun setStartMya(startMya: String?): IGenusBuilder {
        if (startMya == null || startMya == "Unknown") {
            return this
        }
        try {
            this.startMya = startMya.toFloat()
        }
        catch (ex: NumberFormatException) {
            Log.d(TAG, "Unable to convert \'$startMya\' to integer")
        }
        return this
    }

    override fun setEndMya(endMya: String?): IGenusBuilder {
        if (endMya == null || endMya == "Unknown") {
            return this
        }
        try {
            this.endMya = endMya.toFloat()
        }
        catch (ex: NumberFormatException) {
            Log.d(TAG, "Unable to convert \'$endMya\' to integer")
        }
        return this
    }

    override fun setLocations(locations: List<String>): IGenusBuilder {
        this.locations = locations
        return this
    }

    override fun setFormations(formations: List<String>): IGenusBuilder {
        this.formations = formations
        return this
    }

    override fun setSpecies(speciesData: Any): IGenusBuilder {
        if (speciesData !is List<*>) {
            Log.i("GenusBuilder", "Found non-list species data")
            return this
        }

        val speciesBuilder = SpeciesBuilder("", genusName = this.name)
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

    override fun setDiet(diet: Diet): IGenusBuilder {
        this.diet = diet
        return this
    }

    override fun setCreatureType(type: String?): IGenusBuilder {
        this.type = type?.let { CreatureTypeConverter.matchType(it) } ?: CreatureType.Other
        return this
    }

    override fun setCreatureType(type: CreatureType): IGenusBuilder {
        this.type = type
        return this
    }

    override fun setYearsLived(timeRange: String?): IGenusBuilder {
//        Log.i("GenusBuilder", "Setting years-lived to \'$timeRange\'")
//        yearsLived = timeRange
        return this
    }

    override fun setTimePeriod(period: String?): IGenusBuilder {
//        timePeriod = period?.let { EpochConverter.matchType(it) }
        return this
    }

    override fun setTimePeriods(periodStrings: List<String>): IGenusBuilder {
        val periods = periodStrings.mapNotNull {
            EpochConverter.matchType(it)
        }
        timePeriods.addAll(periods)
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

        for (entry in Mass.UnitsMap) {
            val unitStr = entry.key
            val units = entry.value

            if (weight.endsWith(unitStr, ignoreCase = true)) {
                // Drop the units from the end
                val valueOnly = weight.substringBefore(unitStr).trim()
                val maybeMass = Mass.tryMake(valueOnly, units)
                if (maybeMass != null) {
                    this.weight = maybeMass
                    return this
                }
            }
        }

        Log.d(TAG, "Unable to parse weight units for string \'$weight\'")
        return this
    }

    override fun build(): Genus {

        val timeInterval = startMya?.let { start ->
            endMya?.let { end ->
                if (start == end) {
                    // A single point, rather than an interval
                    TimeMarker(start)
                }
                else {
                    // A closed (inclusive) interval from start to end
                    TimeInterval(start, end)
                }
            }
        }

        // Attempt to deduce the Time-Period from the Years Lived
        if (timeInterval != null) {
            // Get all the epochs which overlap with this interval
            val deducedEpochs = EpochConverter.getEpochsIdsFor(timeInterval)
            // Get the ids of all current periods (to avoid duplicates)
            val knownEpochIds = timePeriods.mapNotNull {
                if (it is IProvidesEpoch) {
                    it.getEpochId()
                }
                else {
                    null
                }
            }
            // Only keep the epochs which haven't been specified already
            val newEpochs = deducedEpochs.minus(knownEpochIds.toSet())
            timePeriods.addAll(newEpochs.map { EpochRetriever.getEpoch(it) })
        }

        // We may have parsed the time-periods in a non-chronological order
        // so sort them to enforce it
        val sortedTimePeriods = timePeriods.sortedByDescending { it.getStartTimeInMYA() }

        return Genus(
            name = name,
            diet = diet,
            length = length,
            weight = weight,
            timeInterval = timeInterval,
            timePeriods = sortedTimePeriods,
            nameMeaning = nameMeaning,
            namePronunciation = namePronunciation,
            type = type,
            taxonomy = taxonomy,
            locations = locations,
            species = species,
            formations = formations
        )
    }
}