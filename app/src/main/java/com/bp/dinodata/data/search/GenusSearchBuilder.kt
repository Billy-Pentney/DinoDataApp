package com.bp.dinodata.data.search

import com.bp.dinodata.data.enum_readers.DietConverter
import com.bp.dinodata.data.enum_readers.EpochConverter
import com.bp.dinodata.data.genus.IGenus
import com.bp.dinodata.data.search.terms.BasicSearchTerm
import com.bp.dinodata.data.search.terms.ISearchTerm

class GenusSearchBuilder(
    query: String,
    terms: List<ISearchTerm<in IGenus>> = emptyList(),
    private val possibleGeneraNames: List<String> = emptyList(),
    private val possibleLocations: List<String> = emptyList(),
    private val possibleTaxa: List<String> = emptyList(),
    private val possibleDiets: List<String> = DietConverter.getListOfOptions(),
    private val possibleTimePeriods: List<String> = EpochConverter.getListOfOptions()
) {
    private val searchTermBuilder = SearchTermBuilder(
        generaNames = possibleGeneraNames,
        possibleTaxa = possibleTaxa,
        possibleDiets = possibleDiets,
        locations = possibleLocations,
        possibleTimePeriods = possibleTimePeriods
    )

    private var currentTerm: ISearchTerm<in IGenus> = BasicSearchTerm(query)
    private var searchTerms: MutableList<ISearchTerm<in IGenus>> = terms.toMutableList()

    init {
        parseSearchTerms(query)
    }

    /**
     * Build a search from the raw query text as received from the UI.
     * Completed search terms which are separated by whitespace are parsed into ISearchTerm
     * objects.
     * @param query The raw string which should be split into search terms.
     */
    private fun parseSearchTerms(query: String) {

        // Check if this string ends in a whitespace
        // so we can determine whether the last term is "complete"
        val endsInWhitespace = query.lastOrNull()?.isWhitespace() ?: false

        val splits = query.split(" ")
                          .filter { it.trim().isNotEmpty() }

        val terms = splits.map {
            searchTermBuilder.fromText(it)
        }

        if (endsInWhitespace || terms.isEmpty()) {
            currentTerm = searchTermBuilder.fromText("")
            searchTerms.addAll(terms)
        }
        else {
            // Keep the last term open for editing
            currentTerm = terms.last()
            searchTerms.addAll(terms.dropLast(1))
        }
//
//
//
//        if (suffix.isNotEmpty()) {
//            // The string finished with a whitespace, so we commit all terms
//            // that occur *before* that whitespace
//            val committedTermString = query.trimEnd()
//            val splits = committedTermString.split(" ")
//
//            // Convert the strings to Term objects.
//            // Separate the last term, so we can decide whether it is "complete".
//            val newTerms = splits
//                .filter { it.trim().isNotEmpty() }
//                .map { searchTermBuilder.fromText(it) }
//
//            searchTerms.addAll(newTerms)
//            currentTerm = searchTermBuilder.fromText("")
//        }
//        else {
//            // No whitespace at end, so convert all terms except the last one
//
//            val splits = query
//                .trim()
//                .split(" +".toRegex())
//                .filter { it.isNotEmpty() }
//
//            // Convert the strings to Term objects.
//            // Separate the last term as it has *not* been finalised with a space.
//            val newTerms = splits
//                .dropLast(1)
//                .map { searchTermBuilder.fromText(it) }
//
//            searchTerms.addAll(newTerms)
//
//            val remainder: String? = splits.lastOrNull()
//
//            // No spaces found in the center part
//            if (remainder != null) {
//                currentTerm = searchTermBuilder.fromText(remainder)
//            }
//        }
    }

    fun build(): IMutableSearch<IGenus> {
        if (currentTerm.toOriginalText().isEmpty() && searchTerms.isEmpty()) {
            return BlankSearch()
        }

        return GenusSearch(
            currentTerm = currentTerm,
            terms = searchTerms
        )
    }
}