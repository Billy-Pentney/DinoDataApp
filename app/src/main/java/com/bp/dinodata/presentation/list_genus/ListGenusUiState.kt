package com.bp.dinodata.presentation.list_genus

import com.bp.dinodata.data.IResultsByLetter
import com.bp.dinodata.data.genus.IGenus
import com.bp.dinodata.data.genus.IGenusWithPrefs
import com.bp.dinodata.presentation.DataState
import com.bp.dinodata.presentation.map

enum class ListGenusContentMode {
    Search, Pager
}

interface IListGenusUiState: IListGenusPageUiState {
    fun getDataState(): DataState<IResultsByLetter<IGenusWithPrefs>>

    /**
     * Retrieve the list of genera which are currently visible
     * */
    fun getVisibleData(): DataState<out List<IGenus>>
}


/**
 * Manages the state of the UI for the ListGenusScreen.
 */
data class ListGenusUiState(
    val allPageData: DataState<IResultsByLetter<IGenusWithPrefs>> = DataState.Idle(),
    private val firstVisibleItem: Int = 0,
    private val firstVisibleItemOffset: Int = 0,
    val pageSelectionVisible: Boolean = true,
    private val selectedPageIndex: Int = 0
): IListGenusUiState {

    private val letterKeys: List<Char> = getDataOrNull()?.getKeys() ?: emptyList()

    private fun getDataOrNull(): IResultsByLetter<IGenusWithPrefs>? {
        if (allPageData is DataState.Success) {
            return allPageData.data
        }
        return null
    }

    override fun getDataState(): DataState<IResultsByLetter<IGenusWithPrefs>> {
        return allPageData
    }

    override fun getVisibleData(): DataState<out List<IGenus>> {
        return allPageData.map { it.getGroupByIndex(selectedPageIndex) ?: emptyList() }
    }

    companion object {
        const val DEFAULT_HINT_TEXT = "start typing for suggestions..."
    }


    override fun getPagerKeys(): List<String> {
        // These are the keys which identify the page of elements to show.
        // Currently, this is the letters A-Z
        return letterKeys.map { char -> char.toString() }
    }

    override fun getCurrentPagerIndex(): Int = selectedPageIndex

    override fun getPageByIndex(index: Int): List<IGenusWithPrefs>? {
        return getDataOrNull()?.getGroupByIndex(index)
    }

}