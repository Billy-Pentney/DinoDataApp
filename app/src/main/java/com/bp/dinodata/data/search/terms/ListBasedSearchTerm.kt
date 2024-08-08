package com.bp.dinodata.data.search.terms

import android.util.Log
import androidx.compose.ui.graphics.vector.ImageVector
import com.bp.dinodata.data.DataParsing
import com.bp.dinodata.data.genus.IGenus

abstract class ListBasedSearchTerm(
    private val originalText: String,
    private val termType: SearchTermType,
    private val allPossibleValues: List<String> = emptyList(),
    private val imageIconVector: ImageVector? = null
): ISearchTerm<IGenus> {
    protected var queryArguments = listOf<String>()
    protected val filter = this.toFilter()

    init {
        // Isolate the key from the values
        val splits = originalText.split(":")

        if (splits.size > 1) {
            val values = splits[1]
            // We may have multiple accepted values e.g. "KEY:a+b+c", so we split
            queryArguments = values.split("+")
        }
        else {
            Log.e("ListBasedSearchTerm", "No values found in text \'$originalText\'")
        }
    }

    override fun toString(): String {
        val type = getType().toString()
        val args = queryArguments.map { "\'$it\'" }
        return "$type: ${args.joinToString(" or ")}"
    }

    override fun generateSearchSuggestions(): List<String> {
        if (queryArguments.isNotEmpty()) {
            val lastArgument = queryArguments.last()
            val unusedValues = allPossibleValues.minus(queryArguments.toSet())

            return if (lastArgument in allPossibleValues) {
                // Encourages to add additional argument
                listOf("+")
            } else {
                DataParsing.getLongestPotentialSuffixes(lastArgument, unusedValues)
            }
        }
        return emptyList()
    }
    override fun toOriginalText(): String = originalText
    override fun getType(): SearchTermType = termType
    override fun getIconId(): ImageVector? = imageIconVector

    override fun acceptsItem(item: IGenus): Boolean = this.filter.acceptsItem(item)
}