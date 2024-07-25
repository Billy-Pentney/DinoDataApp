package com.bp.dinodata.presentation.utils

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.ZoomOutMap
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.bp.dinodata.presentation.detail_genus.LocationToAtlasMarker
import kotlinx.coroutines.launch

@Composable
fun ZoomableBox(
    modifier: Modifier = Modifier,
    initialZoom: Float = 1f,
    minZoom: Float = 1f,
    maxZoom: Float = 3f,
    alignment: Alignment? = null,
    content: @Composable ZoomableBoxScope.() -> Unit
) {
    var zoom by remember { mutableFloatStateOf(initialZoom.coerceIn(minZoom, maxZoom)) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    var sizePx by remember { mutableStateOf(IntSize.Zero) }

    var sizeDp by remember { mutableStateOf(Pair(0.dp, 0.dp)) }

    with(LocalDensity.current) {
        val xDp = sizePx.width.toDp()
        val yDp = sizePx.height.toDp()
        sizeDp = Pair(xDp, yDp)
    }

    Box (
        modifier = modifier
            .clip(RectangleShape)
            .fillMaxWidth(1f)
    ) {
        AnimatedVisibility(
            visible = zoom > 1f,
            modifier = Modifier.align(Alignment.TopEnd)
                .zIndex(1f),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            IconButton(onClick = {
                zoom = 1f
                offsetX = 0f
                offsetY = 0f
            },
            ) {
                Icon(
                    Icons.Filled.ZoomOutMap, "reset map zoom"
                )
            }
        }
        Box(
            modifier = Modifier
                .zIndex(0f)
                .onSizeChanged { sizePx = it }
                .graphicsLayer(
                    scaleX = zoom,
                    scaleY = zoom,
                    translationX = offsetX,
                    translationY = offsetY
                )
                .onSizeChanged { sizePx = it }
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, gestureZoom, _ ->
                        zoom = maxOf(minZoom, minOf(maxZoom, zoom * gestureZoom))
                        val maxX = (size.width * (zoom - 1)) / 2
                        val minX = -maxX
                        offsetX = maxOf(minX, minOf(maxX, offsetX + zoom * pan.x))
                        val maxY = (size.height * (zoom - 1)) / 2
                        val minY = -maxY
                        offsetY = maxOf(minY, minOf(maxY, offsetY + zoom * pan.y))
                    }
                },
            contentAlignment = alignment ?: Alignment.TopStart
        ) {
            val scope = ZoomableBoxScopeImpl(sizePx, sizeDp, zoom, offsetX, offsetY)
            scope.content()
        }
    }
}

interface ZoomableBoxScope {
    val sizePx: IntSize
    val sizeDp: Pair<Dp, Dp>
    val scale: Float
    val offsetX: Float
    val offsetY: Float
}

private data class ZoomableBoxScopeImpl(
    override val sizePx: IntSize,
    override val sizeDp: Pair<Dp, Dp>,
    override val scale: Float,
    override val offsetX: Float,
    override val offsetY: Float
) : ZoomableBoxScope


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZoomableImage(
    painter: Painter,
    modifier: Modifier = Modifier,
    imageModifier: Modifier = Modifier,
    contentDescription: String? = null,
    alpha: Float = 1f,
    contentScale: ContentScale = ContentScale.Fit,
    locations: Iterable<String>
) {
    val minZoom = 1f
    val maxZoom = 3f

    var mapSizePx by remember { mutableStateOf(IntSize.Zero) }
    var mapSizeDp by remember { mutableStateOf(Pair(0f, 0f)) }

    val onTapIconColor = remember { Color.Red }
    val iconSize = remember { 26.dp }
    val iconX = remember { iconSize / 2 }
    val iconY = remember { iconSize * 0.75f }

    val markerIcon = remember { Icons.Filled.LocationOn }
    val density = LocalDensity.current

    LaunchedEffect(mapSizePx) {
        with(density) {
            val xDp = mapSizePx.width.toDp()
            val yDp = mapSizePx.height.toDp()
            mapSizeDp = Pair(xDp.value, yDp.value)
        }
    }

    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = modifier
    ) {
        var zoom by remember { mutableFloatStateOf(1f) }
        var offsetX by remember { mutableFloatStateOf(0f) }
        var offsetY by remember { mutableFloatStateOf(0f) }
        var size by remember { mutableStateOf(IntSize.Zero) }

        Image(
            painter,
            contentDescription = contentDescription,
            contentScale = contentScale,
            modifier = Modifier
                .fillMaxWidth(1f)
                .aspectRatio(1.5f)
                .onSizeChanged { mapSizePx = it }
                .alpha(alpha)
                .graphicsLayer(
                    scaleX = zoom,
                    scaleY = zoom,
                    translationX = offsetX,
                    translationY = offsetY
                )
                .onSizeChanged { size = it }
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, gestureZoom, _ ->
                        zoom = (zoom * gestureZoom).coerceIn(minZoom, maxZoom)
                        val maxX = (size.width * (zoom - 1)) / 2
                        val minX = -maxX
                        offsetX = (offsetX + pan.x).coerceIn(minX, maxX)
                        val maxY = (size.height * (zoom - 1)) / 2
                        val minY = -maxY
                        offsetY = maxOf(minY, minOf(maxY, offsetY + pan.y))
                    }
                }
                .fillMaxSize()
        )

        for (locationName in locations) {
            val markerPos = LocationToAtlasMarker.getPosition(locationName)

            if (markerPos == null) {
//                undisplayedLocations.add(locationName)
                continue
            }

            val tooltipState = rememberTooltipState()

            // Compute the position of this marker within the map image
            val xPx = (markerPos.x * mapSizeDp.first - offsetX).dp - iconX
            val yPx = (markerPos.y * mapSizeDp.second - offsetY).dp - iconY

            // If the tooltip is visible, change the color
            val iconColor =
                if (tooltipState.isVisible) {
                    onTapIconColor
                }
                else {
                    markerPos.color
                }

            // Attempt to retrieve the localised region name (from strings.xml).
            // But if this fails, just use the given name in the Tooltip.
            val tooltipText = markerPos.textId?.let { stringResource(it) } ?: locationName

            Box(modifier = Modifier.offset(xPx, yPx)) {
                // Show a tooltip with this region's name when tapped
                TooltipBox(
                    positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                    tooltip = { PlainTooltip { Text(tooltipText) } },
                    state = tooltipState,
                ) {
                    Image(
                        markerIcon, null,
                        colorFilter = ColorFilter.tint(iconColor, BlendMode.SrcIn),
                        modifier = Modifier
                            .size(iconSize * zoom)
                            .clickable {
                                coroutineScope.launch {
                                    tooltipState.show(MutatePriority.UserInput)
                                }
                            }
                    )
                }
            }
        }
    }
}
