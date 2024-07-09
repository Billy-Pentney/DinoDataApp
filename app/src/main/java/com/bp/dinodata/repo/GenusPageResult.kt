package com.bp.dinodata.repo

import com.bp.dinodata.data.Genus

class GenusPageResult(
    val genera: List<Genus>,
    val allDataRetrieved: Boolean
)