package com.bp.dinodata.data.search

import com.bp.dinodata.data.enum_readers.DietConverter
import com.bp.dinodata.data.enum_readers.EpochConverter
import com.bp.dinodata.data.genus.IGenus
import com.bp.dinodata.data.search.terms.BasicSearchTerm
import com.bp.dinodata.data.search.terms.ISearchTerm

class GenusSearchBuilder(
    query: String,
    terms: List<ISearchTerm<in IGenus>> = emptyList(),
    private var capSensitive: Boolean = false,
    private val locations: List<String> = emptyList(),
    private val taxa: List<String> = emptyList(),
    private val possibleDiets: List<String> = DietConverter.getListOfOptions(),
    private val possibleTimePeriods: List<String> = EpochConverter.getListOfOptions()
) {
    private val searchTermBuilder = SearchTermBuilder(
        taxaList = taxa,
        possibleDiets = possibleDiets,
        locations = locations,
        possibleTimePeriods = possibleTimePeriods
    )

    private var currentTerm: ISearchTerm<in IGenus> = BasicSearchTerm(query)
    private var searchTerms: MutableList<ISearchTerm<in IGenus>> = terms.toMutableList()

    init {
        parseSearchTerms(query)
    }

    companion object {
        private val SEARCH_TERM_REGEX = "(?:(?:(\\w+):(\\w+(?:\\+\\w+)*))|(\\w+))+".toRegex()
    }

//    private fun parseSearchTermsTwo(query: String) {
//        val matches = SEARCH_TERM_REGEX.findAll(query)
//
//        for (match in matches) {
//            val key = match.groups[1]?.value
//            val values = match.groups[2]?.value
//            val rawText = match.groups[3]?.value
//
//            val term: ISearchTerm<in IGenus>? =
//                if (key != null && values != null) {
//                    searchTermBuilder.fromKeyValuePair(key, values)
//                }
//                else if (rawText != null) {
//                    searchTermBuilder.fromText(rawText)
//                }
//                else {
//                    null
//                }
//
//            term?.let{ searchTerms.add(it) }
//        }
//
//        if (!query.endsWith(" ")) {
//            if (searchTerms.isNotEmpty()) {
//                currentTerm = searchTerms.removeLast()
//            }
//            else {
//                currentTerm = BasicSearchTerm("")
//            }
//        }
//    }

    /**
     * Build a search from the raw query text as received from the UI.
     * @param query The raw string which is visible via the Search Bar.
     */
    private fun parseSearchTerms(query: String) {
        // Strip whitespace from both ends, so we can retain it
        // without it affecting the splitting into terms
//        val prefix = query.takeWhile { it.isWhitespace() }
        val suffix = query.takeLastWhile { it.isWhitespace() }

        if (suffix.isNotEmpty()) {
            // The string finished with a whitespace, so we commit all terms
            // that occur *before* that whitespace
            val committedTermString = query.trimEnd()
            val splits = committedTermString.split(" +".toRegex())

            // Convert the strings to Term objects.
            // Separate the last term, so we can decide whether it is "complete".
            val newTerms = splits
                .filter { it.trim().isNotEmpty() }
                .map { searchTermBuilder.fromText(it) }

            searchTerms.addAll(newTerms)
            currentTerm = searchTermBuilder.fromText("")
        }
        else {
            // No whitespace at end, so convert all terms
            val splits = query
                .trim()
                .split(" +".toRegex())
                .filter { it.isNotEmpty() }

            // Convert the strings to Term objects.
            // Separate the last term as it has *not* been finalised with a space.
            val newTerms = splits
                .dropLast(1)
                .map { searchTermBuilder.fromText(it) }

            searchTerms.addAll(newTerms)

            val remainder: String? = splits.lastOrNull()

            // No spaces found in the center part
            if (remainder != null) {
                currentTerm = searchTermBuilder.fromText(remainder)
            }
        }
    }

    fun build(): GenusSearch {
        return GenusSearch(
            currentTerm = currentTerm,
            terms = searchTerms,
            locations = locations,
            taxa = taxa,
            possibleTimePeriods = possibleTimePeriods,
            possibleDiets = possibleDiets
        )
    }
}