package com.bp.dinodata.presentation.detail_genus

import androidx.compose.ui.graphics.Color
import com.bp.dinodata.R

interface IMarker{
    fun getLatitude(): Float
    fun getLongitude(): Float
}


/**
 * A marker refers to a point which is displayed on the global map. This could be a country
 * or state (or neither), but it must have a specific point.
 * @property x the latitude of the marker, ranges from 0.0-1.0
 * @property y the longitude of the marker, ranges from 0.0-1.0
 * @property textId (optional) the Id of the string for this region; this is used to display
 * localised country names e.g. "Russe" rather than "Russia"
 * @property color (optional) the color of this marker; by default, uses a slightly
 * translucent white.
 */
data class Marker(
    val x: Float,
    val y: Float,
    val textId: Int? = null,
    val color: Color = Color(0xBBFFFFFF)
): IMarker {
    override fun getLatitude(): Float = x
    override fun getLongitude(): Float = y
}

object LocationToAtlasMarker {

    private val keyRedirect = mapOf(
        "United Kingdom" to "UK",
        "United States" to "USA",
        "North America" to "USA"
    )

    private val locationToPosition = mapOf(
        "Canada" to Marker(0.192f, 0.247f, R.string.location_canada),
        "UK" to Marker(0.466f, 0.258f, R.string.location_uk_full),
        "Australia" to Marker(0.83f, 0.65f, R.string.location_australia),
        "USA" to Marker(0.225f, 0.35f, R.string.location_usa_full),
        "Mexico" to Marker(0.206f, 0.438f, R.string.location_mexico),
        "Brazil" to Marker(0.348f, 0.59f, R.string.location_brazil),
        "Spain" to Marker(0.463f, 0.337f, R.string.location_spain),
        "Japan" to Marker(0.835f, 0.355f, R.string.location_japan),
        "Madagascar" to Marker(0.597f, 0.63f, R.string.location_madagascar),
        "India" to Marker(0.685f, 0.425f, R.string.location_india),
        "Egypt" to Marker(0.548f, 0.411f, R.string.location_egypt),
        "Morocco" to Marker(0.454f, 0.376f, R.string.location_morocco),
        "Algeria" to Marker(0.485f, 0.411f, R.string.location_algeria),
        "Niger" to Marker(0.507f, 0.452f, R.string.location_niger),
        "Portugal" to Marker(0.449f, 0.334f, R.string.location_portugal),
        "Argentina" to Marker(0.313f, 0.685f, R.string.location_argentina),
        "China" to Marker(0.74f, 0.384f, R.string.location_china),
        "Antarctica" to Marker(0.589f, 0.932f, R.string.location_antarctica),
        "France" to Marker(0.477f, 0.293f, R.string.location_france),
        "Lesotho" to Marker(0.548f, 0.677f, R.string.location_lesotho),
        "Uruguay" to Marker(0.325f, 0.695f, R.string.location_uruguay),
        "Russia" to Marker(0.6f, 0.24f, R.string.location_russia),
        "Korea" to Marker(0.81f, 0.35f, R.string.location_korea),
        "Romania" to Marker(0.541f, 0.308f, R.string.location_romania),
        "Belgium" to Marker(0.483f, 0.269f, R.string.location_belgium),
        "Mongolia" to Marker(0.75f, 0.3f, R.string.location_mongolia),
        "Kazakhstan" to Marker(0.67f, 0.29f, R.string.location_kazakhstan),
        "Germany" to Marker(0.5f, 0.27f, R.string.location_germany),
        "South Africa" to Marker(0.535f, 0.685f, R.string.location_south_africa),
        "Tunisia" to Marker(0.495f, 0.36f, R.string.location_tunisia),
        "Switzerland" to Marker(0.489f, 0.294f, R.string.location_switzerland),
        "Zimbabwe" to Marker(0.555f, 0.625f, R.string.location_zimbabwe),
        "Tanzania" to Marker(0.566f, 0.567f, R.string.location_tanzania),
        "Malawi" to Marker(0.565f, 0.605f, R.string.location_malawi)
    )

    fun getPosition(location: String): Marker? {
        val key = keyRedirect[location] ?: location
        return locationToPosition[key]
    }

    fun getAllKnownKeys(): List<String> {
        return locationToPosition.keys.toList()
    }
}