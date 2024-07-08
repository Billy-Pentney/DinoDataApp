package com.bp.dinodata.data

object ImageUrlBuilder {
    fun buildUrl(
        id: String,
        imgSize: String,
        type: PhylopicImageType
    ): String {
        return "https://api.phylopic.org/images/${id}/${type}/$imgSize.png"
    }
}

enum class PhylopicImageType { Thumbnail, Raster }