package com.bp.dinodata.data

object ImageUrlBuilder {
    fun buildUrl(
        id: String,
        imgSize: String,
        type: PhylopicImageType
    ): String {
        val typeLower = type.toString().lowercase()
        return "https://images.phylopic.org/images/$id/$typeLower/$imgSize.png"
    }
}

enum class PhylopicImageType { Thumbnail, Raster }