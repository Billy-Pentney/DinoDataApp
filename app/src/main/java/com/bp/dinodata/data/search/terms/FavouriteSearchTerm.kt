package com.bp.dinodata.data.search.terms

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector
import com.bp.dinodata.data.DataParsing
import com.bp.dinodata.data.filters.FavouriteFilter
import com.bp.dinodata.data.filters.IFilter
import com.bp.dinodata.data.genus.IGenus

/**
 * A search-term which filters by whether the given item is or is not a favourite of the user.
 */
class FavouriteSearchTerm(
    private val originalText: String
): ISearchTerm<IGenus> {
    private var queryValue: String = ""
    private var acceptFavourites: Boolean? = null

    init {
        // Isolate the key from the values
        val splits = originalText.split(":")

        if (splits.size > 1) {
            queryValue = splits[1]
            acceptFavourites = queryValue == "true"
        }
        else {
            Log.e("ListBasedSearchTerm", "No values found in text \'$originalText\'")
        }
    }

    override fun acceptsItem(item: IGenus): Boolean {
        return this.toFilter().acceptsItem(item)
    }

    override fun getType(): SearchTermType {
        return SearchTermType.Favourite
    }

    override fun generateSearchSuggestions(): List<String> {
        return DataParsing.getLongestPotentialSuffixes(
            queryValue, listOf("true", "false")
        )
    }

    override fun toFilter(): IFilter<in IGenus> {
        return FavouriteFilter(acceptFavourites)
    }

    override fun toOriginalText(): String = originalText
    override fun getIconId(): ImageVector = Icons.Filled.Star
    override fun getQueries(): List<String> {
        return listOf(acceptFavourites.toString())
    }

    override fun toString(): String {
        return "IsFavourite: $acceptFavourites"
    }
}