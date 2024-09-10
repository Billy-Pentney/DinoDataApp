package com.bp.dinodata.data.filters

import com.bp.dinodata.data.genus.IGenus
import com.bp.dinodata.data.genus.IHasName
import com.bp.dinodata.data.taxon.ITaxon

class TextFilter(
    private val queryText: String,
    private val capitalSensitive: Boolean = false
): IFilter<IHasName> {
    override fun acceptsItem(item: IHasName): Boolean {
        return item.getName()
            .lowercase()
            .contains(queryText, ignoreCase = !capitalSensitive)
    }

    override fun toString(): String {
        return "\'$queryText\' in NAME"
    }
}

class TextFilterWithRegex(
    private val queryText: String,
    private val acceptPartialMatches: Boolean = true
): IFilter<IHasName> {

    private var regexStr: String = queryText
        // Escape any regex wildcards
        .replace(".", "\\.")
        // Substitute our wildcard with the regex wildcard
        .replace("*", ".*")

    private val regexPattern: Regex = regexStr.toRegex()

    override fun acceptsItem(item: IHasName): Boolean {
        val itemName = item.getName().lowercase()
        return if (acceptPartialMatches) {
            // Accept any string which includes the target pattern
            regexPattern.find(itemName) != null
        } else {
            // Only accept string which are an exact match for the full pattern
            regexPattern.matches(itemName)
        }
    }
}




/** Check for the given text in either the item name, or the item's children */
class TitleOrSpeciesFilter(
    private val queryText: String,
    private val capitalSensitive: Boolean = false,
): IFilter<ITaxon> {
    override fun acceptsItem(item: ITaxon): Boolean {
        val titleContains = item.getName()
            .lowercase()
            .contains(queryText, ignoreCase = !capitalSensitive)

        val speciesContains = item.getChildrenTaxa().any {
            it.getName()
                .lowercase()
                .contains(queryText, ignoreCase = !capitalSensitive)
        }

        return titleContains || speciesContains
    }

    override fun toString(): String {
        return "\'$queryText\' in NAME or in SPECIES"
    }
}