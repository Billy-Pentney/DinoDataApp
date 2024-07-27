package com.bp.dinodata.presentation.list_genus

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.bp.dinodata.R
import com.bp.dinodata.data.genus.GenusBuilder
import com.bp.dinodata.data.genus.GenusWithPrefs
import com.bp.dinodata.presentation.utils.ThemeConverter
import com.bp.dinodata.data.genus.IDisplayInList
import com.bp.dinodata.data.genus.ILocalPrefs
import com.bp.dinodata.data.genus.LocalPrefs
import com.bp.dinodata.presentation.icons.DietIconSquare
import com.bp.dinodata.presentation.utils.convertCreatureTypeToSilhouette
import com.bp.dinodata.theme.DinoDataTheme
import com.bp.dinodata.theme.colorFavourite500
import com.bp.dinodata.theme.colorFavourite700

@Composable
fun GenusListItem(
    genus: IDisplayInList,
    onClick: () -> Unit = {},
    height: Dp = 54.dp,
    modifier: Modifier = Modifier
) {
    var silhouetteId by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(null) {
        silhouetteId = convertCreatureTypeToSilhouette(genus.getCreatureType())
    }

    val colorTint =
        if (genus is ILocalPrefs) {
            ThemeConverter.getColor(genus.getSelectedColorName())
        }
        else {
            null
        }
    
    val isFavourite = (genus is ILocalPrefs) && genus.isUserFavourite()

    val colorFirst =
        if (isFavourite) {
            colorFavourite500
        }
        else {
            Color.Transparent
        }
    
    val brush =
        Brush.linearGradient(
            0.3f to colorFirst,
            1.0f to (colorTint ?: Color.Transparent)
        )

    val shape = RoundedCornerShape(8.dp)
    val cardModifier: Modifier =
        if (isFavourite) {
            modifier.border(2.dp, colorFavourite700, shape)
        }
        else {
            modifier
        }
    
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = shape,
        modifier = cardModifier
            .fillMaxWidth()
            .height(height),
        onClick = onClick
    ) {
        Box(
            contentAlignment = Alignment.BottomEnd,
            modifier = Modifier
                .fillMaxSize()
                .background(brush, alpha = 0.75f)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = modifier
                    .fillMaxSize()
                    .padding(horizontal=10.dp, vertical=6.dp)
            ) {
                DietIconSquare(
                    diet = genus.getDiet(),
                    modifier = Modifier.padding()
                )
                Column (
                    modifier = Modifier.fillMaxHeight(),
                    verticalArrangement = Arrangement.Center
                ){
                    if (isFavourite) {
                        Row (
                            modifier = Modifier
                                .alpha(0.6f)
                                .padding(top = 2.dp)
                                .height(13.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Image(
                                Icons.Filled.Star, "",
                                colorFilter = ColorFilter.tint(
                                    Color.White, BlendMode.SrcIn
                                ),
                                modifier = Modifier.fillMaxHeight()
                            )
                            Text(
                                stringResource(R.string.label_favourite),
                                fontSize = 11.sp,
                                color = Color.White,
                                maxLines = 1,
                                lineHeight = 12.sp
                            )
                        }
                    }
                    Text(
                        genus.getName(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 21.sp,
                        fontStyle = FontStyle.Italic,
//                        modifier = Modifier.fillMaxHeight()
                    )
                }
            }
            AnimatedVisibility(
                silhouetteId != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Image(
                    painter = painterResource(id = silhouetteId!!),
                    contentDescription = "creature type",
                    modifier = Modifier
                        .alpha(0.4f)
                        .zIndex(1f)
                        .padding(top = 5.dp)
                        .fillMaxHeight()
                        .absoluteOffset(x = 25.dp, y = 0.dp)
                        .clipToBounds(),
                    alignment = Alignment.BottomEnd,
                    contentScale = ContentScale.FillHeight,
//                    colorFilter = ColorFilter.tint(
//                        MaterialTheme.colorScheme.onSurface,
//                        BlendMode.Screen
//                    )
                )
            }
        }
    }
}



@Preview
@Composable
fun PreviewGenusListItem() {

    val genus = GenusBuilder("Styracosaurus")
        .setDiet("herbivore")
        .setCreatureType("ceratopsian")
        .build()
    
    val genusWithPrefs = GenusWithPrefs(
        genus, LocalPrefs(
            _color = "GREEN",
            _isFavourite = true
        )
    )

    val genusWithPrefs2 = GenusWithPrefs(
        genus, LocalPrefs(_color = null)
    )

    DinoDataTheme {
        Column (verticalArrangement = Arrangement.spacedBy(4.dp)) {
            GenusListItem(genus = genusWithPrefs)
            GenusListItem(genus = genusWithPrefs2)
        }
    }

}
