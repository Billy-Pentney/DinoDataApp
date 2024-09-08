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

    /**
     * Build a search from the raw query text as received from the UI.
     * Completed search terms which are separated by whitespace are parsed into ISearchTerm
     * objects.
     * @param query The raw string which should be split into search terms.
     */
    private fun parseSearchTerms(query: String) {
        // Strip whitespace from both ends, so we can retain it
        // without it affecting the splitting into terms
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
            // No whitespace at end, so convert all terms except the last one

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

    fun build(): IMutableSearch<IGenus> {
        if (currentTerm.toOriginalText().isEmpty() && searchTerms.isEmpty()) {
            return BlankSearch()
        }

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