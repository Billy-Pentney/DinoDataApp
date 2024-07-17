package com.bp.dinodata.repo

import com.bp.dinodata.data.genus.Genus

/**
 * Data-transfer object used to return the (successful) result of loading the next page of data.
 * 
 * @param data The list of data collected in this page.
 * @param isAllDataRetrieved Indicates if this page is the last one available, i.e. if it meets
 * the expected size given in the page request.
 * 
 */
open class PageResult<T>(
    val data: List<T>,
    val isAllDataRetrieved: Boolean
)

class GeneraPageResult(
    data: List<Genus>,
    isAllDataRetrieved: Boolean
): PageResult<Genus>(data, isAllDataRetrieved)