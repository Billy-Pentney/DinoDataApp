package com.bp.dinodata.presentation.utils

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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

fun calculateOffset(tapOffset: Offset, winSize: IntSize, zoom: Float): Offset {
    val windowWidth = winSize.width / zoom
    val windowHeight = winSize.height / zoom

    val maxX = winSize.width - windowWidth / 2
    val maxY = winSize.height - windowHeight / 2

    var centX = maxOf(windowWidth / 2, minOf(tapOffset.x, maxX))
    var centY = maxOf(windowHeight / 2, minOf(tapOffset.y, maxY))

    // get a value in -0.5 to 0.5
    centX = 1 - centX / windowWidth
    centY = 1 - centY / windowHeight
    return Offset(centX * winSize.width, centY * winSize.height)
}

fun constrainOffset(size: IntSize, offset: Offset, zoom: Float): Offset {
    val maxX = (size.width * (zoom - 1)) / 2
    val minX = -maxX
    val offsetX = maxOf(minX, minOf(maxX, offset.x))
    val maxY = (size.height * (zoom - 1)) / 2
    val minY = -maxY
    val offsetY = maxOf(minY, minOf(maxY, offset.y))
    return Offset(offsetX, offsetY)
}

@Composable
fun ZoomableBox(
    modifier: Modifier = Modifier,
    minZoom: Float = 1f,
    maxZoom: Float = 3f,
    amountZoomOnDoubleTap: Float = 1f,
    initialZoom: Float = minZoom,
    initialOffset: Offset = Offset.Zero,
    alignment: Alignment? = null,
    showZoomText: Boolean = true,
    content: @Composable ZoomableBoxScope.() -> Unit
) {
    var zoom by remember { mutableFloatStateOf(initialZoom.coerceIn(minZoom, maxZoom)) }
    var sizePx by remember { mutableStateOf(IntSize.Zero) }
    var sizeDp by remember { mutableStateOf(Pair(0.dp, 0.dp)) }
    var offset by remember { mutableStateOf(Offset.Zero) }

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
//        AnimatedVisibility(
//            visible = zoom > 1f,
//            modifier = Modifier
//                .align(Alignment.TopEnd)
//                .zIndex(1f),
//            enter = fadeIn(),
//            exit = fadeOut()
//        ) {
//            IconButton(onClick = {
//                zoom = 1f
//                offset = Offset.Zero
//            },
//            ) {
//                Icon(
//                    Icons.Filled.ZoomOutMap, "reset map zoom"
//                )
//            }
//        }
        if (showZoomText) {
            AnimatedVisibility(
                visible = zoom > minZoom,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp)
            ) {
                Text(
                    "%.1fx zoom".format(zoom),
                    modifier = Modifier.alpha(0.5f)
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
                    translationX = offset.x,
                    translationY = offset.y
                )
                .onSizeChanged { sizePx = it }
                .pointerInput(Unit) {
                    detectTransformGestures { centroidOffset, pan, gestureZoom, _ ->
                        zoom = maxOf(minZoom, minOf(maxZoom, zoom * gestureZoom))
                        val newOffset = offset + Offset(zoom * pan.x, zoom * pan.y)
                        offset = constrainOffset(size, newOffset, zoom)
                        Log.d("Zoomable", "Zoom Pan, offset: $offset")
                    }
                }
                .pointerInput(Unit) {
                    detectTapGestures(
                        onDoubleTap = { tapOffset ->
                            if (zoom < maxZoom) {
                                val newZoom = minOf(maxZoom, zoom + amountZoomOnDoubleTap)
                                val offsetInWindow = calculateOffset(tapOffset, size, 2f)

                                Log.d("Zoomable", "Double Tap, window offset $offsetInWindow")

                                val offsetTotal = offsetInWindow

                                offset = constrainOffset(size, offsetTotal, newZoom)
                                zoom = newZoom
                            } else {
                                offset = Offset.Zero
                                zoom = minZoom
                            }
                        }
                    )
                },
            contentAlignment = alignment ?: Alignment.TopStart
        ) {
            val scope = ZoomableBoxScopeImpl(sizePx, sizeDp, zoom, offset)
            scope.content()
        }
    }
}

interface ZoomableBoxScope {
    val sizePx: IntSize
    val sizeDp: Pair<Dp, Dp>
    val scale: Float
    val offset: Offset
}

private data class ZoomableBoxScopeImpl(
    override val sizePx: IntSize,
    override val sizeDp: Pair<Dp, Dp>,
    override val scale: Float,
    override val offset: Offset,
) : ZoomableBoxScope


