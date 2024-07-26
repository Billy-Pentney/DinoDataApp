package com.bp.dinodata.data.filters

import com.bp.dinodata.data.CreatureType
import com.bp.dinodata.data.Diet
import com.bp.dinodata.data.TimePeriod
import com.bp.dinodata.data.genus.IGenus
import com.bp.dinodata.data.genus.ILocalPrefs
import com.bp.dinodata.data.genus.IHasCreatureType
import com.bp.dinodata.data.genus.IHasDiet
import com.bp.dinodata.data.genus.IHasLocationInfo
import com.bp.dinodata.data.genus.IHasName
import com.bp.dinodata.data.genus.IHasTaxonomy
import com.bp.dinodata.data.genus.IHasTimePeriodInfo

/**
 * A filter defines one or more restrictions on items of type T.
 * This can be used to identify the elements of that type which meet some restriction
 * e.g. a search query.
 */
interface IFilter<T> {
    /** Returns true if and only if the given item matches this filter. */
    fun acceptsItem(item: T): Boolean
    fun<R:T> applyTo(list: Iterable<R>): List<T> {
        return list.filter { acceptsItem(it) }
    }
}


class NameFilter(
    private val nameQuery: String,
    private val capitalSensitive: Boolean
): IFilter<IHasName> {
    override fun acceptsItem(item: IHasName): Boolean {
        val itemName = item.getName()
        return if (!capitalSensitive) {
            itemName.lowercase().contains(nameQuery, true)
        } else {
            itemName.contains(nameQuery, true)
        }
    }

    override fun toString(): String {
        return "\'$nameQuery\' in NAME"
    }
}

class CreatureTypeFilter(
    private val requiredTypes: List<CreatureType>
): IFilter<IHasCreatureType> {
    override fun acceptsItem(item: IHasCreatureType): Boolean = item.getCreatureType() in requiredTypes
    override fun toString(): String {
        return "TYPE in [${requiredTypes.joinToString(",")}]"
    }
}

//class MeasurementFilter(
//    private val length: IDescribesLength
//): IFilter<IHasMeasurements> {
//    override fun acceptsItem(item: IHasMeasurements): Boolean {
//        val length = item.getLength()
//        length?.
//    }
//}

class TaxonFilter(
    private val acceptedTaxa: List<String>
): IFilter<IHasTaxonomy> {
    override fun acceptsItem(item: IHasTaxonomy): Boolean {
        val taxonList = item.getListOfTaxonomy().map { it.lowercase() }
        return taxonList.any {
            itemTaxon ->
            acceptedTaxa.any { it in itemTaxon }
        }
    }

    override fun toString(): String {
        return "${acceptedTaxa.map{"\'$it\'"}.joinToString(" or ")} in TAXONOMY"
    }
}

class DietFilter(
    private val acceptedDiets: List<Diet>
): IFilter<IHasDiet> {
    override fun acceptsItem(item: IHasDiet): Boolean {
        return item.getDiet() in acceptedDiets
    }
    override fun toString(): String {
        return "DIET in [${acceptedDiets.joinToString(",")}]"
    }
}

class TimePeriodFilter(
    private val acceptedTimePeriods: List<TimePeriod>
): IFilter<IHasTimePeriodInfo> {
    override fun acceptsItem(item: IHasTimePeriodInfo): Boolean {
        val itemTimePeriod = item.getTimePeriod()
        if (itemTimePeriod != null) {
            val baseTimePeriod = TimePeriod(itemTimePeriod.epoch)
            return itemTimePeriod in acceptedTimePeriods || baseTimePeriod in acceptedTimePeriods
        }
        return false
    }
}

class SelectedColorFilter(
    private val acceptedColors: List<String>
): IFilter<IGenus> {
    private val acceptsUncolored = acceptedColors.contains("NONE")

    override fun acceptsItem(item: IGenus): Boolean {
        if (item is ILocalPrefs) {
            val color = item.getSelectedColorName()
            return color in acceptedColors || (color == null && acceptsUncolored)
        }
        return false
    }
}


class LocationFilter(
    acceptedLocations: List<String>,
    private val capitalSensitive: Boolean = false
): IFilter<IHasLocationInfo> {

    private val _acceptedLocations: List<String> = preprocessList(acceptedLocations)

    private fun preprocessList(list: List<String>): List<String> {
        return if (capitalSensitive) {
            list
        } else {
            list.map { element -> element.lowercase().replace("_", " ") }
        }
    }

    override fun acceptsItem(item: IHasLocationInfo): Boolean {
        val locations = preprocessList(item.getLocations())
        return locations.any { it in _acceptedLocations }
    }
}

class FavouriteFilter(
    private val acceptFavourites: Boolean? = null
): IFilter<IGenus> {
    override fun acceptsItem(item: IGenus): Boolean {
        if (acceptFavourites == null) {
            // Accept all items if an invalid argument is given
            return true
        }
        // Otherwise, accept mutually-exclusively, i.e.
        // if the argument is true, accept only favourites; if false, then accept only non-favourites
        if (item is ILocalPrefs) {
            return (acceptFavourites && item.isUserFavourite()) ||
                    (!acceptFavourites && !item.isUserFavourite())
        }
        return false
    }
}

