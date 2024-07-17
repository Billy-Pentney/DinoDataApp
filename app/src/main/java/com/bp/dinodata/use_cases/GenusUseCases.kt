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
        val queryWords = query.split(" ")
        val filterBuilder = FilterBuilderImpl<IGenus>()
        for (word in queryWords) {
            val splits = word.split(":")
            if (splits.size != 2) {
                filterBuilder.addFilter(NameFilter(word, capitalSensitive))
            }
            else {
                val prefix = splits[0]
                val subquery = splits[1]
                when (prefix) {
                    "taxon" -> {
                        filterBuilder.addFilter(TaxonFilter(subquery))
                    }
                    "type" -> {
                        val type = DataParsing.matchCreatureType(subquery)
                        filterBuilder.addFilter(CreatureTypeFilter(listOf(type)))
                    }
                    "diet" -> {
                        val diet = DataParsing.matchDiet(subquery)
                        filterBuilder.addFilter(DietFilter(listOf(diet)))
                    }
                    else -> {
                        Log.d("ListGenusViewModel", "Unknown query filter \'$word\'")
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

