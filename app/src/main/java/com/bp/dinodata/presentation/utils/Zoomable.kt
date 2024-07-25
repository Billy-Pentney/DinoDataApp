package com.bp.dinodata.presentation.utils

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ZoomOutMap
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

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


