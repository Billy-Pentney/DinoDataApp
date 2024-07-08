package com.bp.dinodata.data

class SingleImageUrlData(
    val id: String,
    val imageSizes: List<String>,
    val thumbSizes: List<String>
)

class MultiImageUrlData(
    val name: String,
    private val urlData: List<SingleImageUrlData>
) {
    fun getFirstUrlData(): SingleImageUrlData? {
        return urlData.getOrNull(0)
    }
}

object ParseImageUrlData {

    fun parseImageUrlData(imageData: Map<String, Any>): SingleImageUrlData? {
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
                    parseImageUrlData(it)
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


