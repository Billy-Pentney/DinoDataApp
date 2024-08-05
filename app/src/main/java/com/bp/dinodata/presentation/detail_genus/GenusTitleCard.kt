package com.bp.dinodata.presentation.detail_genus

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bp.dinodata.R
import com.bp.dinodata.data.genus.IGenusWithImages
import com.bp.dinodata.data.genus.ILocalPrefs
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun GenusTitleCardAndControls(
    genus: IGenusWithImages,
    onPlayNamePronunciation: () -> Unit,
    canPlayPronunciation: Boolean,
    isFavourite: Boolean,
    showLargeImageDialog: () -> Unit,
    colorScheme: ColorScheme,
    setColorPickerDialogVisibility: (Boolean) -> Unit,
    toggleItemAsFavourite: (Boolean) -> Unit,
    showControls: (Boolean) -> Unit,
    updateVisibleImageIndex: (Int) -> Unit,
    modifier: Modifier = Modifier,
    visibleImageIndex: Int = 0,
    innerPadding: Dp = 8.dp,
    paddingValues: PaddingValues = PaddingValues(),
    controlsExpanded: Boolean = false,
) {
    val genusImageUrl = genus.getImageUrl(visibleImageIndex)
    val totalImages = genus.getNumDistinctImages()

    var isUserFavourited by remember { mutableStateOf(isFavourite) }

    Log.d("GenusDetail", "Showing image at url: $genusImageUrl")

    Card(
        modifier = Modifier.padding(paddingValues),
        shape = RoundedCornerShape(0.dp, 0.dp, 16.dp, 16.dp),
        colors = CardDefaults.cardColors(
            contentColor = MaterialTheme.colorScheme.onSurface,
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        )
    ) {
        MaterialTheme(colorScheme = colorScheme) {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(
                    topStart = 0f,
                    topEnd = 0f,
                    bottomEnd = 50f,
                    bottomStart = 50f
                ),
                modifier = modifier
                    .fillMaxWidth()
                    .animateContentSize()
                    .padding(paddingValues)
            ) {
                Box(modifier = Modifier.height(IntrinsicSize.Min)) {
                    Box(
                        contentAlignment = Alignment.BottomEnd,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 20.dp)
                            .fillMaxHeight(0.7f)
                    ) {
                        GlideImage(
                            model = genusImageUrl,
                            alignment = Alignment.CenterEnd,
                            contentScale = ContentScale.Fit,
                            failure = placeholder(R.drawable.unkn),
                            contentDescription = null,
                            modifier = Modifier
                                .clickable {
                                    if (genusImageUrl != null) {
                                        Log.i("GenusTitleCard", "User clicked image; opening dialog...")
                                        showLargeImageDialog()
                                    }
                                    else {
                                        Log.i("GenusTitleCard", "User clicked image but no image to show!")
                                    }
                                }
                                .fillMaxHeight()
                                .padding(start = 40.dp, end = 20.dp, bottom = 20.dp)
                                .offset(x = 10.dp, y = 0.dp)
                                .fillMaxWidth()
                                .alpha(0.4f)
                                .animateContentSize()
                        )
                        if (totalImages > 1) {
                            Row {
                                IconButton(
                                    onClick = { updateVisibleImageIndex(-1) },
                                    enabled = visibleImageIndex > 0
                                ) {
                                    Icon(
                                        Icons.Filled.ChevronLeft,
                                        contentDescription = "switch to previous image"
                                    )
                                }
                                IconButton(
                                    onClick = { updateVisibleImageIndex(1) },
                                    enabled = visibleImageIndex < genus.getNumDistinctImages() - 1
                                ) {
                                    Icon(
                                        Icons.Filled.ChevronRight,
                                        contentDescription = "switch to next image"
                                    )
                                }
                            }
                        }
                    }
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .fillMaxWidth()
                            .padding(start = innerPadding),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        val pronunciation = genus.getNamePronunciation()

                        Column(
                            verticalArrangement = Arrangement.spacedBy(2.dp),
                            modifier = Modifier.padding(bottom = innerPadding)
                        ) {
                            // Show a Star if this is a favourite
                            AnimatedVisibility(
                                isUserFavourited,
                                enter = fadeIn(),
                                exit = fadeOut()
                            ) {
                                Icon(
                                    Icons.Filled.Star,
                                    null,
                                    modifier = Modifier.padding(bottom = 2.dp)
                                )
                            }

                            Text(
                                genus.getName(),
                                fontWeight = FontWeight.Bold,
                                fontSize = 26.sp,
                                fontStyle = FontStyle.Italic,
                            )

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .alpha(0.7f)
                                    .padding(top = 2.dp)
                            ) {
                                if (canPlayPronunciation) {
                                    IconButton(
                                        onClick = onPlayNamePronunciation,
                                        modifier = Modifier
                                            .size(24.dp)
                                    ) {
                                        Icon(
                                            Icons.AutoMirrored.Filled.VolumeUp,
                                            contentDescription = stringResource(R.string.desc_play_tts_audio),
                                        )
                                    }
                                }
                                Text(
                                    pronunciation ?: stringResource(R.string.text_no_pronunciation),
                                    fontStyle = FontStyle.Italic
                                )
                            }
                        }

                        AnimatedVisibility(
                            visible = !controlsExpanded,
                            enter = fadeIn(), exit = fadeOut()
                        ) {
                            IconButton(onClick = { showControls(true) }) {
                                Icon(
                                    Icons.Filled.KeyboardArrowDown,
                                    contentDescription = "show preferences buttons"
                                )
                            }
                        }
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = controlsExpanded,
            enter = expandVertically(expandFrom = Alignment.Top) { _ -> 0 },
            exit = shrinkVertically(shrinkTowards = Alignment.Top) { _ -> 0 }
        ) {
            UpdateGenusLocalPreferencesButtons(
                setColorPickerDialogVisibility = setColorPickerDialogVisibility,
                toggleItemAsFavourite = {
                    isUserFavourited = it
                    toggleItemAsFavourite(it)
                },
                isFavourite = isUserFavourited,
                modifier = Modifier.padding(vertical = 8.dp),
                hideButtons = {
                    showControls(false)
                }
            )
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}