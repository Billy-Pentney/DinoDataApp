package com.bp.dinodata.presentation.detail_genus

import android.graphics.drawable.Drawable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import coil.Coil
import coil.compose.AsyncImage
import com.bp.dinodata.R
import com.bp.dinodata.data.genus.IHasCurrentSelectedImage
import com.bp.dinodata.presentation.utils.ZoomableBox
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun EnlargedImageDialog(
    genus: IHasCurrentSelectedImage,
    onHide: () -> Unit
) {
    val urlData = genus.getCurrentImageData()
    // We'll load the largest possible resolution, to enable zooming for detail
//    val largeUrl = urlData?.getUrlOfLargestImage()
    // This smallest image should have already been cached by Glide
    val smallUrl = urlData?.getSmallestImageUrl(0)

    var zoom by remember { mutableFloatStateOf(1f) }

//    val context = LocalContext.current
//
//    var smallRequestBuilder: RequestBuilder<Drawable>? = null
//
//    LaunchedEffect(null) {
//        smallRequestBuilder = Glide.with(context).load(smallUrl)
//    }

    Dialog(
        onDismissRequest = onHide,
        properties = DialogProperties(
            dismissOnClickOutside = true,
            dismissOnBackPress = true
        )
    ) {
        val shape = RoundedCornerShape(16.dp)
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shape = shape,
            modifier = Modifier
                .heightIn(max = 400.dp)
                .fillMaxSize()
                .padding(8.dp),
            shadowElevation = 8.dp
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = onHide,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .zIndex(1f)
                        .alpha(0.5f)
                        .padding(8.dp)
                ) {
                    Icon(
                        Icons.Filled.Close,
                        "close dialog"
                    )
                }

                ZoomableBox(
                    alignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .zIndex(0f),
                    showZoomText = false
                ) {
                    zoom = scale

                    GlideImage(
                        model = smallUrl,
                        alignment = Alignment.CenterEnd,
                        contentScale = ContentScale.Fit,
                        failure = placeholder(R.drawable.unkn),
//                        loading = GlideImage(model = smallUrl, contentDescription = null),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxHeight()
                            .fillMaxWidth()
                            .heightIn(max=600.dp)
                            .widthIn(max=1000.dp)
                            .animateContentSize()
                    )
                }

                val imageName = urlData?.getImageName()
                imageName?.let {
                    // Only show the image name if the image is not zoomed
                    AnimatedVisibility(
                        zoom == 1f,
                        enter = fadeIn(),
                        exit = fadeOut(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .padding(bottom = 8.dp)
                            .align(Alignment.BottomCenter)
                    ) {
                        Column (
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                imageName,
                                textAlign = TextAlign.Center,
                                fontSize = 16.sp,
                                fontStyle = FontStyle.Italic,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier
                                    .alpha(0.65f)
                                    .fillMaxWidth()
                            )
                            Text(
                                stringResource(R.string.credit_powered_by_phylopic),
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .alpha(0.4f)
                                    .fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}