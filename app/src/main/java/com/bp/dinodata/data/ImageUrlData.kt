package com.bp.dinodata.data

import android.util.Log

class SingleImageUrlData(
    private val id: String,
    imageSizes: List<String>,
    thumbSizes: List<String>
) {
    private val imageSizesAsc = imageSizes.sortedBy { extractFirstDimension(it) }
    private val thumbSizesAsc = thumbSizes.sortedBy { extractFirstDimension(it) }

    val numImages: Int
        get() = imageSizesAsc.size
    val numThumbnails: Int
        get() = thumbSizesAsc.size

    fun getImageUrl(index: Int): String? {
        if (index >= numImages) {
            return null
        }
        return ImageUrlBuilder.buildUrl(id, imageSizesAsc[index], type=PhylopicImageType.Raster)
    }

    fun getThumbnailUrl(index: Int): String? {
        if (index >= numThumbnails) {
            return null
        }
        return ImageUrlBuilder.buildUrl(id, thumbSizesAsc[index], type=PhylopicImageType.Thumbnail)
    }

    companion object {
        fun extractFirstDimension(size: String): Int {
            // Extract the first dimension from a string of the form "AxB", where A and B are integers
            // e.g. "4501x1928" -> 4501

            val splits = size.split("x")
            return try {
                splits[0].toInt()
            } catch (exception: NumberFormatException) {
                Log.d("ImageUrlData", "Unable to parse dimensions $size")
                0
            }
        }
    }
}

class MultiImageUrlData(
    val name: String,
    private val urlData: List<SingleImageUrlData>
) {
    fun getFirstUrlData(): SingleImageUrlData? {
        return urlData.getOrNull(0)
    }

    fun getAllImageData(): List<SingleImageUrlData> = urlData

    val totalImages: Int
        get() = urlData.sumOf { it.numImages }

    val totalThumbnails: Int
        get() = urlData.sumOf { it.numThumbnails }

    val distinctImages: Int = urlData.size
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


