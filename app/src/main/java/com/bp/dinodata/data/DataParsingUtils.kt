package com.bp.dinodata.data


object DataParsing {
    /**
     * Given an initial text and a list of target strings, return a list of potential
     * suffixes which could be appended to the text to produce a string in the list.
     * @param matchText The text to be used as a prefix
     * @param targetStrings A list of all strings which should be matched against.
     * @return A list of strings such that appending any of these strings to the matchText
     * creates a string in targetStrings.
     * */
    fun getLongestPotentialSuffixes(matchText: String, targetStrings: Iterable<String>): List<String> {
        return targetStrings
            .filter { it.startsWith(matchText) }
//            .sortedByDescending { it.length }
            .map { it.removePrefix(matchText) }
    }
}