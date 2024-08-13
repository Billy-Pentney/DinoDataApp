package com.bp.dinodata.data.search.terms

import androidx.compose.ui.graphics.vector.ImageVector
import com.bp.dinodata.data.filters.IFilter

/**
 * A term within a data-search which can be used to filter data of the type T.
 * Implementations should be constructed via the SearchTermBuilder. */
interface ISearchTerm<T>: IFilter<T> {
    /** Describes the type of property that this search operates on. */
    fun getType(): SearchTermType
    /** Return a list of possible auto-fill strings which have a matching prefix with the term text. */
    fun generateSearchSuggestions(): List<String>
    /** Convert this term to a filter which runs on objects which derive type T. */
    fun toFilter(): IFilter<in T>
    /** Get the original text used to construct this term. */
    fun toOriginalText(): String
    /** Return the drawable Id for the resource representing this term. */
    fun getIconId(): ImageVector?

    fun getQueries(): List<String>
}


enum class SearchTermType {
    Text,
    CreatureType,
    Diet,
    TimePeriod,
    Taxon,
    Color,
    Location,
    Favourite
}