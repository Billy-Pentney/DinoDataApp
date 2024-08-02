package com.bp.dinodata.data


object DataParsing {
    fun getLongestPotentialSuffixes(matchText: String, targetStrings: Iterable<String>): List<String> {
        return targetStrings
            .filter { it.startsWith(matchText) }
            .sortedByDescending { it.length }
            .map { it.removePrefix(matchText) }
    }
}