package com.bp.dinodata.presentation.utils

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ZoomOutMap
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlin.math.roundToInt

@Composable
fun ZoomableBox(
    modifier: Modifier = Modifier,
    initialZoom: Float = 1f,
    minZoom: Float = 1f,
    maxZoom: Float = 3f,
    amountZoomOnDoubleTap: Float = 2f,
    alignment: Alignment? = null,
    showZoomText: Boolean = true,
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

    val calculateOffset = { size: IntSize, offset: Offset ->
        val maxX = (size.width * (zoom - 1)) / 2
        val minX = -maxX
        offsetX = maxOf(minX, minOf(maxX, offset.x))
        val maxY = (size.height * (zoom - 1)) / 2
        val minY = -maxY
        offsetY = maxOf(minY, minOf(maxY, offset.y))
    }

    Box (
        modifier = modifier
            .clip(RectangleShape)
            .fillMaxWidth(1f)
    ) {
        AnimatedVisibility(
            visible = zoom > 1f,
            modifier = Modifier
                .align(Alignment.TopEnd)
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
        if (showZoomText) {
            AnimatedVisibility(
                visible = zoom > minZoom,
                modifier = Modifier.align(Alignment.BottomStart).padding(8.dp)
            ) {
                Text(
                    "${zoom.roundToInt()}x zoom",
                    modifier = Modifier.alpha(0.3f)
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
                    detectTransformGestures { centroidOffset, pan, gestureZoom, _ ->
                        zoom = maxOf(minZoom, minOf(maxZoom, zoom * gestureZoom))
                        val newOffset = Offset(offsetX+zoom*pan.x, offsetY+zoom*pan.y)
                        calculateOffset(size, newOffset)
                        Log.d("Zoomable", "Zoom Pan, offset: ${offsetX}, ${offsetY}")
                    }
                }
                .pointerInput(Unit) {
                    detectTapGestures(
                        onDoubleTap = { tapOffset ->
                            if (zoom == minZoom) {
                                val newZoom = minOf(maxZoom, zoom + 1)
                                val windowWidth = size.width / newZoom
                                val windowHeight = size.height / newZoom

                                var centX = maxOf(windowWidth / 2, minOf(tapOffset.x, size.width - windowWidth / 2))
                                var centY = maxOf(windowHeight / 2, minOf(tapOffset.y, size.height - windowHeight / 2))

                                // get a value in -0.5 to 0.5
                                centX = 1 - centX / windowWidth
                                centY = 1 - centY / windowHeight

                                offsetX = centX * size.width
                                offsetY = centY * size.height

                                Log.d("Zoomable",
                                    "Zoom Double Tap @ ${tapOffset.x}, ${tapOffset.y};" +
//                                            " relative to center: $tapX, $tapY " +
                                            " offset @ $offsetX, $offsetY")

                                zoom = newZoom
                            } else {
                                offsetX = 0f
                                offsetY = 0f
                                zoom = minZoom
                            }

                        }
                    )
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


