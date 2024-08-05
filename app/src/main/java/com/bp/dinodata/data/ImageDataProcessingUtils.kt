package com.bp.dinodata.data

import android.util.Log

interface ImageUrlData {
    fun getUrlOfLargestImage(): String?
    fun getSmallestImageUrl(index: Int): String?
    fun getUrlOfLargestThumbnail(): String?
    fun getSmallestThumbnailUrl(index: Int): String?
    fun getImageName(): String
}

class SingleImageUrlData(
    private val name: String,
    private val id: String,
    imageSizes: List<String>,
    thumbSizes: List<String>
): ImageUrlData {
    private val imageSizesAsc = imageSizes.sortedBy { extractFirstDimension(it) }
    private val thumbSizesAsc = thumbSizes.sortedBy { extractFirstDimension(it) }

    val numImages: Int
        get() = imageSizesAsc.size
    val numThumbnails: Int
        get() = thumbSizesAsc.size

    override fun getSmallestImageUrl(index: Int): String? {
        if (index >= numImages) {
            return null
        }
        return ImageUrlBuilder.buildUrl(id, imageSizesAsc[index], type=PhylopicImageType.Raster)
    }

    override fun getSmallestThumbnailUrl(index: Int): String? {
        if (index >= numThumbnails) {
            return null
        }
        return ImageUrlBuilder.buildUrl(id, thumbSizesAsc[index], type=PhylopicImageType.Thumbnail)
    }

    override fun getImageName(): String = name
    override fun getUrlOfLargestImage(): String? = getSmallestImageUrl(imageSizesAsc.size-1)
    override fun getUrlOfLargestThumbnail(): String? = getSmallestThumbnailUrl(thumbSizesAsc.size-1)

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

object ImageDataProcessingUtils {

    fun fromMap(name: String, imageData: Map<String, Any>): SingleImageUrlData? {
        // Extract the data for each image

        val id = imageData["id"] as String?
        val imageSizes = imageData["img_sizes"] as List<String>?
        val thumbSizes = imageData["thumb_sizes"] as List<String>?

        if (id != null && imageSizes != null && thumbSizes != null) {
            return SingleImageUrlData(name, id, imageSizes, thumbSizes)
        }
        return null
    }

    fun mapToImageUrlDTOs(map: Map<String, Any>?): Map<String,MultiImageUrlData>? {
        if (map == null)
            return null

        val imageDataBySpecies = mutableMapOf<String, MultiImageUrlData>()

        for (speciesName in map.keys) {
            val listOfImageDatas = map[speciesName] as List<Map<String, Any>>?
            if (listOfImageDatas != null) {
                // Store the data for all images belonging to this species
                val imageUrlData = listOfImageDatas.mapNotNull {
                    fromMap(speciesName, it)
                }

                // Only make a DTO if there was at least one image found
                if (imageUrlData.isNotEmpty()) {
                    imageDataBySpecies[speciesName] = MultiImageUrlData(speciesName, imageUrlData)
                }
            }
        }

        return imageDataBySpecies
    }
}


