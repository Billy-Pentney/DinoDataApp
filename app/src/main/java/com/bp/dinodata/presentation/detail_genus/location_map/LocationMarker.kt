package com.bp.dinodata.presentation.detail_genus.location_map

import androidx.compose.ui.graphics.Color

interface IMarker{
    fun getX(): Float
    fun getY(): Float
    fun getMarkerColor(): Color
    fun getTextId(): Int?
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
    private val x: Float,
    private val y: Float,
    private val textId: Int? = null,
    private val color: Color = Color(0xBBFFFFFF)
): IMarker {
    override fun getX(): Float = x
    override fun getY(): Float = y
    override fun getMarkerColor(): Color = color
    override fun getTextId(): Int? = textId
}