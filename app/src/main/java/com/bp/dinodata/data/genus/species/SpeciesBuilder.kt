package com.bp.dinodata.data.genus.species

import android.icu.util.Calendar
import android.util.Log
import com.bp.dinodata.data.IBuilder
import com.bp.dinodata.data.IDictParser
import com.bp.dinodata.data.genus.ISpecies
import com.bp.dinodata.data.genus.Species


interface ISpeciesBuilder: IBuilder<ISpecies>, IDictParser<ISpecies> {
    fun setDiscoverer(text: String?): ISpeciesBuilder
    fun setYearOfDiscovery(text: String?): ISpeciesBuilder
    fun setTypeSpecies(value: Any?): ISpeciesBuilder
}

class SpeciesBuilder(
    name: String,
    private var discoveredBy: String? = null,
    private var discoveryYear: Int? = null,
    private var isType: Boolean = false,
    private var genusName: String? = null
): ISpeciesBuilder {
    private var _name = name

    companion object {
        private val currentYear = Calendar.getInstance().get(Calendar.YEAR)

        /**
         * Verifies that the given integer corresponds to a suitable year of discovery,
         * within the last 300 years.
         * */
        fun isValidYear(year: Int): Boolean {
            return year in 1700..currentYear
        }
    }

    override fun clear(): IBuilder<ISpecies> {
        discoveredBy = null
        discoveryYear = null
        isType = false
        return this
    }

    private fun setName(newName: String): ISpeciesBuilder {
        this._name = newName
        return this
    }

    override fun setDiscoverer(text: String?): ISpeciesBuilder {
        text?.let { discoveredBy = it }
        return this
    }

    override fun setYearOfDiscovery(text: String?): ISpeciesBuilder {
        text?.toIntOrNull()?.let { year ->
            if (isValidYear(year))
                discoveryYear = year
            else
                Log.i("SpeciesBuilder", "Attempt to set invalid year \'$year\'")
        }

        return this
    }

    override fun setTypeSpecies(value: Any?): ISpeciesBuilder {
        this.isType =
            if (value is Boolean)
                value
            else
                value is String && value == "true"
        return this
    }

    override fun fromDict(dataMap: Map<*, *>): ISpeciesBuilder? {
        val name = dataMap["name"]

        if (name == null) {
            Log.i("SpeciesBuilder", "DataMap did not contain key \'name\'")
            return null
        }

        // Indicates if this species is the TypeSpecies for its genus
        val isType = dataMap["is_type"]

        val discoveredBy = dataMap["discovered_by"]?.toString()
        val discoveryYear = dataMap["discovered_year"]?.toString()

        return this.setName(name.toString())
                   .setDiscoverer(discoveredBy)
                   .setYearOfDiscovery(discoveryYear)
                   .setTypeSpecies(isType)
    }

    override fun build(): ISpecies {
        return Species(
            name = _name,
            discoveredBy = discoveredBy,
            discoveryYear = discoveryYear,
            isType = isType,
            genusName = genusName
        )
    }
}