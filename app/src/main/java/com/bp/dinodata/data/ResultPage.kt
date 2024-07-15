package com.bp.dinodata.data

import androidx.compose.runtime.Immutable
import kotlin.jvm.Throws

interface IResultsByLetter<T: IHasName>: Iterable<T> {
    @Throws(InvalidKey::class)
    fun getGroupByLetter(letter: Char): List<T>
    fun getGroupByIndex(i: Int): List<T>?

    fun getSize(): Int
    fun getNumGroups(): Int

    fun getKey(index: Int): Char
    fun getKeys(): List<Char>
}

class InvalidKey(letter: Char): Exception("Invalid key $letter. Should be A-Z.")


class ResultsByLetter<T: IHasName>(
    private val results: List<T> = emptyList(),
    private val upperCaseKeys: Boolean = true
): IResultsByLetter<T> {

    private fun dropKeyCase(char: Char): Char {
        return if (upperCaseKeys) {
            char.uppercaseChar()
        } else {
            char.lowercaseChar()
        }
    }

    private val alphabetMapping: Map<Char, List<T>> = results.groupBy{
        dropKeyCase(it.getName()[0])
    }

    private val numResults = results.size

    override fun getNumGroups(): Int = alphabetMapping.size
    override fun getSize(): Int = numResults

    @Throws(InvalidKey::class)
    override fun getGroupByLetter(letter: Char): List<T> {
        val letterNoCase = dropKeyCase(letter)
        if (letterNoCase !in 'A'..'Z') {
            throw InvalidKey(letterNoCase)
        }
        return alphabetMapping[letterNoCase] ?: emptyList()
    }

    override fun getGroupByIndex(i: Int): List<T>? {
        val keys = getKeys()
        val key = keys[i % keys.size]
        return alphabetMapping[key]
    }

    override fun getKeys(): List<Char> = alphabetMapping.keys.sorted()
    override fun getKey(index: Int): Char = alphabetMapping.keys.sorted()[index]

    override fun iterator(): Iterator<T> = results.iterator()

    /* Iterator data */
//    private var currLetter: Int = 0
//    private var currResultInLetter: Int = 0
//    override fun hasNext(): Boolean {
//        if (numResults == 0) {
//            return false
//        }
//        alphabetMapping.iterator()
//        return currLetter < numLettersWithResults-1
//    }
//
//    override fun next(): T {
//        TODO("Not yet implemented")
//    }
}

//fun <T: IHasName> ResultsByLetter<T>.emptyList(): IResultsByLetter<T> = ResultsByLetter()