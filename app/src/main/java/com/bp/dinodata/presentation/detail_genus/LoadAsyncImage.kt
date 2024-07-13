package com.bp.dinodata.presentation.detail_genus

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder

@Composable
@OptIn(ExperimentalGlideComposeApi::class)
fun LoadAsyncImageOrReserveDrawable(
    imageUrl: String?,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    alignment: Alignment,
    contentScale: ContentScale,
    drawableIfImageFailed: Int,
    visible: Boolean = true
) {
    if (visible) {
        GlideImage(
            model = imageUrl,
            loading = placeholder(drawableIfImageFailed),
            failure = placeholder(drawableIfImageFailed),
            contentDescription = contentDescription,
            modifier = modifier,
            alignment = alignment,
            contentScale = contentScale,
        )
    }
}