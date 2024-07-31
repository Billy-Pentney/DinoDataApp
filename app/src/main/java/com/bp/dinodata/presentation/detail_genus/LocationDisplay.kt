package com.bp.dinodata.presentation.detail_genus

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bp.dinodata.R
import com.bp.dinodata.presentation.utils.ZoomableBox
import com.bp.dinodata.theme.DinoDataTheme
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationAtlas(
    locations: Iterable<String>,
    modifier: Modifier = Modifier,
    initialZoom: Float = 1f
) {
    val onTapIconColor = remember { Color.Red }
    val defaultIconSize = remember { 26.dp }

    val shape = RoundedCornerShape(8.dp)
    val markerIcon = remember { Icons.Filled.LocationOn }

    val coroutineScope = rememberCoroutineScope()

    // Store a list of all location names which were *not*
    // matched to a marker and displayed.
    val undisplayedLocations = mutableListOf<String>()

    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        ZoomableBox(
            modifier = modifier
                .background(MaterialTheme.colorScheme.surface, shape)
                .clip(shape)
                .focusable()
                .fillMaxWidth()
                .aspectRatio(1.5f),
            initialZoom = initialZoom,
        ) {
            Image(
                painterResource(id = R.drawable.map_base),
                contentDescription = "atlas",
                alpha = 0.35f,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth(1f)
//                .border(2.dp, Color.Yellow)
            )

            val iconSize = defaultIconSize / (1 + (scale - 1) / 2)

            val iconX = iconSize / 2
            val iconY = iconSize * 0.9f

            for (locationName in locations) {
                val markerPos = AtlasMarkers.getPosition(locationName)

                if (markerPos == null) {
                    undisplayedLocations.add(locationName)
                    continue
                }

                val tooltipState = rememberTooltipState()

                // Compute the position of this marker within the map image
                val xDp = (markerPos.getX() * sizeDp.first.value).dp - iconX
                val yDp = (markerPos.getY() * sizeDp.second.value).dp - iconY

                // If the tooltip is visible, change the color
                val iconColor =
                    if (tooltipState.isVisible) {
                        onTapIconColor
                    } else {
                        markerPos.getMarkerColor()
                    }

                // Attempt to retrieve the localised region name (from strings.xml).
                // But if this fails, just use the given name in the Tooltip.
                val tooltipText = markerPos.getTextId()?.let { stringResource(it) } ?: locationName

                Box(modifier = Modifier.offset(xDp, yDp)) {
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
                                .size(iconSize)
                                .clickable {
                                    coroutineScope.launch {
                                        tooltipState.show(MutatePriority.UserInput)
                                    }
                                }
//                            .border(1.dp, Color.Red)
                        )
                    }
                }
            }
        }

        Text(
            stringResource(R.string.action_pinch_map_to_zoom),
            modifier = Modifier.alpha(0.5f),
            fontSize = 14.sp,
            fontStyle = FontStyle.Italic
        )

        if (undisplayedLocations.isNotEmpty()) {
            LabelAttributeRow(
                label = "Not shown",
                labelFontWeight = FontWeight.Normal,
                value = undisplayedLocations.joinToString()
            )
        }
    }
}

@Preview(widthDp = 400, heightDp = 500)
@Composable
fun PreviewLocationAtlas() {
    DinoDataTheme (darkTheme = true) {
        Surface (
            modifier = Modifier.width(1080.dp)
        ) {
            LocationAtlas(
                locations = AtlasMarkers.getKeysForRegion(
                    AtlasMarkers.ASIA
                )
            )
        }
    }
}