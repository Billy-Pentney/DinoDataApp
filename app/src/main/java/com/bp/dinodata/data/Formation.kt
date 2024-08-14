package com.bp.dinodata.data

import com.bp.dinodata.data.genus.IHasLocationInfo
import com.bp.dinodata.data.genus.IHasName
import com.bp.dinodata.data.MyBuilderUtils
import com.bp.dinodata.data.time_period.IDisplayableTimePeriod

interface IFormation: IHasName, IHasLocationInfo {
    fun hasCountries(): Boolean
}


data class Formation(
    private val name: String,
    val id: String = convertNameToId(name),
    private val continent: String? = null,
    private val location: String? = null,
    private val countries: List<String> = emptyList(),
    private val states: List<String> = emptyList(),
//    val timePeriods: List<IDisplayableTimePeriod>
): IFormation {

    companion object {
        fun convertNameToId(name: String): String {
            return name.replace(" ", "_").lowercase()
        }
    }

    override fun hasCountries(): Boolean = countries.isNotEmpty()

    override fun getName(): String = name
    override fun getLocations(): List<String> = countries
}




class FormationBuilder(
    val id: String
): IBuilder<IFormation> {

    private var name: String = ""
    private var continent: String? = null
    private var location: String? = null
    private var countries: List<String> = listOf()
    private var states: List<String> = listOf()

    override fun clear(): IBuilder<IFormation> {
        name = ""
        continent = null
        location = null
        countries = mutableListOf()
        states = mutableListOf()
        return this
    }

    fun setFromDict(dict: Map<*, *>): FormationBuilder {

        name = dict["formation"].toString()
        dict["continent"]?.let { continent = it.toString() }
        dict["location"]?.let { location = it.toString() }
        dict["country"]?.let { countries = MyBuilderUtils.parseToStringList(it) }
        dict["state"]?.let { states = MyBuilderUtils.parseToStringList(it) }
//        dict["age"]?.let { age = MyBuilderUtils.parseToStringList(it) }

        return this
    }

    companion object {
        fun makeFromDict(dict: Map<*, *>): FormationBuilder? {
            val id = dict["id"]
            return id?.let {
                FormationBuilder(id=it.toString())
                    .setFromDict(dict)
            }
        }
    }

    override fun build(): IFormation {
        return Formation(
            name,
            id,
            continent,
            location,
            countries,
            states
        )
    }



}