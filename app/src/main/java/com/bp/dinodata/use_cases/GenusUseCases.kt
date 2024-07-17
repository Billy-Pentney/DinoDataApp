package com.bp.dinodata.use_cases

import android.util.Log
import com.bp.dinodata.data.DataParsing
import com.bp.dinodata.data.genus.Genus
import com.bp.dinodata.data.genus.IGenus
import com.bp.dinodata.data.ResultsByLetter
import com.bp.dinodata.data.filters.CreatureTypeFilter
import com.bp.dinodata.data.filters.DietFilter
import com.bp.dinodata.data.filters.FilterBuilderImpl
import com.bp.dinodata.data.filters.IFilter
import com.bp.dinodata.data.filters.NameFilter
import com.bp.dinodata.data.filters.TaxonFilter
import com.bp.dinodata.data.filters.TimePeriodFilter
import com.bp.dinodata.repo.IGenusRepository

class GenusUseCases(
    private val genusRepository: IGenusRepository
) {
//    fun getAllGenera(
//        callback: (List<Genus>) -> Unit,
//        onError: (Exception) -> Unit
//    ) {
//        genusRepository.getAllGenera(
//            callback,
//            onError
//        )
//    }

    fun getGeneraGroupedByLetter(
        callback: (ResultsByLetter<IGenus>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        genusRepository.getAllGenera(
            { callback(ResultsByLetter(it)) },
            onError
        )
    }

    fun getGenusByName(
        genusName: String,
        callback: (Genus?) -> Unit,
        onFailure: () -> Unit,
    ) {
        genusRepository.getGenus(genusName, callback, onFailure)
    }

    fun makeFilterFromQuery(query: String, capitalSensitive: Boolean): IFilter<IGenus> {
        // Start by splitting the query into terms
        val queryWords = query.split(" ")
        val filterBuilder = FilterBuilderImpl<IGenus>()

        for (term in queryWords) {
            // Attempt to split this term into a prefix and its target, e.g. "diet:carnivore"
            val splits = term.split(":")
            if (splits.size != 2) {
                // If not possible or invalid number of splits, treat it as a name
                filterBuilder.addFilter(NameFilter(term, capitalSensitive))
            }
            else {
                // If here, then we know we have a term of the form "X:Y"
                val key = splits[0]
                val value = splits[1]

                // We may have multiple accepted values e.g. "X:a+b+c", so we split
                val values = value.split("+")

                when (key) {
                    "taxon" -> { filterBuilder.addFilter(TaxonFilter(value)) }
                    "type" -> {
                        val types = values.mapNotNull { DataParsing.matchCreatureType(it) }
                        if (types.isNotEmpty()) {
                            filterBuilder.addFilter(CreatureTypeFilter(types))
                        }
                    }
                    "diet" -> {
                        val diets = values.mapNotNull { DataParsing.matchDiet(it) }
                        if (diets.isNotEmpty()) {
                            filterBuilder.addFilter(DietFilter(diets))
                        }
                    }
                    "period" -> {
                        val periods = values.mapNotNull { DataParsing.matchTimePeriod(it) }
                        if (periods.isNotEmpty()) {
                            filterBuilder.addFilter(TimePeriodFilter(periods))
                        }
                    }
                    else -> {
                        Log.d("ListGenusViewModel", "Unknown query filter \'$term\'")
                    }
                }
            }
        }
        return filterBuilder.build()
    }

//    fun getNextPageOfGenera(
//        startAfter: String? = null,
//        callback: (PageResult<Genus>) -> Unit,
//        onException: (Exception) -> Unit
//    ) {
//        genusRepository.getNextPage(
//            startAfter = startAfter,
//            callback = callback,
//            onException = onException
//        )
//    }
}

