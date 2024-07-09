package com.bp.dinodata.data

class SingleImageUrlData(
    private val id: String,
    private val imageSizes: List<String>,
    private val thumbSizes: List<String>
) {
    fun numImages(): Int = imageSizes.size
    fun numThumbnails(): Int = thumbSizes.size

    fun getImageUrl(index: Int): String? {
        if (index > numImages()) {
            return null
        }

        return ImageUrlBuilder.buildUrl(id, imageSizes[index], type=PhylopicImageType.Raster)
    }

    fun getThumbnailUrl(index: Int): String? {
        if (index > numThumbnails()) {
            return null
        }
        return ImageUrlBuilder.buildUrl(id, thumbSizes[index], type=PhylopicImageType.Thumbnail)
    }
}

class MultiImageUrlData(
    val name: String,
    private val urlData: List<SingleImageUrlData>
) {
    fun getFirstUrlData(): SingleImageUrlData? {
        return urlData.getOrNull(0)
    }
}

object ImageUrlData {

    fun fromMap(imageData: Map<String, Any>): SingleImageUrlData? {
        // Extract the data for each image

        val id = imageData["id"] as String?
        val imageSizes = imageData["img_sizes"] as List<String>?
        val thumbSizes = imageData["thumb_sizes"] as List<String>?

        if (id != null && imageSizes != null && thumbSizes != null) {
            return SingleImageUrlData(id, imageSizes, thumbSizes)
        }
        return null
    }

    fun mapToImageUrlDTOs(map: Map<String, Any>?): Map<String,MultiImageUrlData>? {
        if (map == null)
            return null

        val imageDataBySpecies = mutableMapOf<String, MultiImageUrlData>()

        for (species in map.keys) {
            val listOfImageDatas = map[species] as List<Map<String, Any>>?
            if (listOfImageDatas != null) {
                // Store the data for all images belonging to this species
                val imageUrlData = listOfImageDatas.mapNotNull {
                    fromMap(it)
                }

                // Only make a DTO if there was at least one image found
                if (imageUrlData.isNotEmpty()) {
                    imageDataBySpecies[species] = MultiImageUrlData(species, imageUrlData)
                }
            }
        }

        return imageDataBySpecies
    }
}


