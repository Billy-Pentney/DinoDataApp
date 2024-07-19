package com.bp.dinodata.data.genus

import com.bp.dinodata.data.MultiImageUrlData


interface GenusBuilder {
    fun build(): Genus

    fun setDiet(dietStr: String?): GenusBuilder
    fun setYearsLived(timeRange: String?): GenusBuilder
    fun setTimePeriod(period: String?): GenusBuilder
    fun setNamePronunciation(pronunciation: String?): GenusBuilder
    fun setNameMeaning(meaning: String?): GenusBuilder
    fun setLength(length: String?): GenusBuilder
    fun setWeight(weight: String?): GenusBuilder
    fun setCreatureType(type: String?): GenusBuilder
    fun splitTimePeriodAndYears(periodAndYears: String?): GenusBuilder
    fun setTaxonomy(taxonomy: List<String>?): GenusBuilder
    fun setStartMya(startMya: String?): GenusBuilder
    fun setEndMya(endMya: String?): GenusBuilder

    fun addImageUrlMap(imageData: Map<String, MultiImageUrlData>): GenusBuilder
    fun setLocations(it: List<String>?): GenusBuilder
}






