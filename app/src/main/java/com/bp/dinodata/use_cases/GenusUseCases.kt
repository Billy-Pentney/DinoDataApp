package com.bp.dinodata.use_cases

import android.util.Log
import com.bp.dinodata.data.IResultsByLetter
import com.bp.dinodata.data.ResultsByLetter
import com.bp.dinodata.data.filters.IFilter
import com.bp.dinodata.data.genus.GenusWithPrefs
import com.bp.dinodata.data.genus.IGenus
import com.bp.dinodata.data.genus.IGenusWithPrefs
import com.bp.dinodata.data.search.GenusSearch
import com.bp.dinodata.data.search.IMutableSearch
import com.bp.dinodata.data.search.ISearch
import com.bp.dinodata.data.taxon.TaxonCollection
import com.bp.dinodata.data.taxon.TaxonCollectionBuilder
import com.bp.dinodata.presentation.DataState
import com.bp.dinodata.repo.IGenusRepository
import com.bp.dinodata.repo.ILocalPreferencesRepository
import com.bp.dinodata.repo.ITaxonomyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.map

class GenusUseCases(
    private val genusRepository: IGenusRepository,
    private val localPrefRepository: ILocalPreferencesRepository,
    private val taxonRepository: ITaxonomyRepository
) {
    private val taxonomyMap = taxonRepository.getTaxonToParentMapFlow()
    private val generaList = genusRepository.getAllGeneraFlow()
    private val prefs = localPrefRepository.getGenusLocalPrefsFlow()
    private val locationsFlow = genusRepository.getLocationsFlow()
    private val generaNamesFlow = genusRepository.getAllGeneraNamesFlow()
    private val taxaNamesFlow = genusRepository.getAllTaxaFlow()


    fun getGenusWithPrefsFlow(): Flow<List<IGenusWithPrefs>> {
        return generaList.combine(prefs) { generaList, prefsMap ->
            generaList.map {
                // Pair up the genus with its local preferences
                val name = it.getName()
                GenusWithPrefs(it, prefsMap[name])
            }
        }.flowOn(Dispatchers.Default)
    }

    fun getGenusWithPrefsByLetterFlow(): Flow<DataState<IResultsByLetter<IGenusWithPrefs>>> {
        return getGenusWithPrefsFlow().map {
            DataState.Success(ResultsByLetter(it))
        }
    }

    /** Build the taxonomy collection from the parent mapping and the genera */
    fun getTaxonomyListFlow(): Flow<DataState<TaxonCollection>> {
        return taxonomyMap.combine(generaList) { taxonParentsMap, genera ->
            val collectionBuilder = TaxonCollectionBuilder(taxonParentsMap)
            collectionBuilder.addGenera(genera)
            DataState.Success(collectionBuilder.build())
        }
    }

    /**
     * Apply the given search to the list of genera, returning a DataState which encapsulates
     * the result of the search if successful, or indicates failure otherwise.
     * @param search A search which can be applied to genus objects.
     * @return A Flow providing a DataState, representing the results of the search.
     */
    fun applyGenusSearch(search: IFilter<IGenus>): Flow<DataState<List<IGenus>>> {
        return getGenusWithPrefsFlow().map {
            DataState.Success(search.applyTo(it))
        }
    }

    fun makeNewGenusSearchFlow(
        searchTextFlow: Flow<String>,
        searchObjFlow: Flow<IMutableSearch<IGenus>>
    ): Flow<ISearch<IGenus>> {
        return combine(
            searchObjFlow, searchTextFlow, locationsFlow, generaNamesFlow, taxaNamesFlow
        ) { search, query, locations, genera, taxa ->
            Log.d("GenusUseCases","Making new search with query \"${query}\"")
            search.updateQuery(query, locations, genera, taxa)
        }
    }

}

