package com.bp.dinodata.presentation

import android.util.Log
import android.view.WindowInsets.Side
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.request.ImageRequest
import com.bp.dinodata.R
import com.bp.dinodata.data.Genus
import com.bp.dinodata.data.GenusBuilderImpl
import com.bp.dinodata.data.MultiImageUrlData
import com.bp.dinodata.data.SingleImageUrlData
import com.bp.dinodata.presentation.icons.DietIconThin
import com.bp.dinodata.presentation.icons.TimePeriodIcon
import com.bp.dinodata.presentation.utils.convertCreatureTypeToSilhouette
import com.bp.dinodata.theme.DinoDataTheme
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import kotlin.math.exp


@Composable
fun LabelAttributeRow(
    label: String,
    value: String?,
    units: String? = null,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.SpaceBetween,
    valueStyle: FontStyle = FontStyle.Normal,
    valueTextAlign: TextAlign = TextAlign.End
) {
    Row (
        horizontalArrangement = horizontalArrangement,
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("$label: ", modifier=Modifier.alpha(0.75f))
        Text(
            value ?: "Unknown",
            fontStyle = valueStyle,
            textAlign = valueTextAlign
        )
        units?.let { Text(units, modifier=Modifier.alpha(0.75f)) }
    }
}

@Composable
fun LabelContentRow(
    label: String,
    valueContent: @Composable () -> Unit,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.SpaceBetween
) {
    Row (
        horizontalArrangement=horizontalArrangement,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("$label: ", modifier=Modifier.alpha(0.6f))
        valueContent.invoke()
    }
}


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















@Composable
fun GenusTitleCard(
    genus: Genus,
    padding: Dp,
    scrollState: LazyListState,
    onPlayNamePronunciation: () -> Unit
) {
    val silhouetteId = convertCreatureTypeToSilhouette(genus.type)
    val genusImageUrl = genus.getImageUrl()

    var expanded by remember { mutableStateOf(true) }
    val scrollPosition by remember { derivedStateOf { scrollState.firstVisibleItemScrollOffset } }

//    Side(key1 = scrollPosition) {
//        if (scrollPosition > 1) {
//            expanded = true
//        }
//    }

    Log.d("GenusDetail", "Showing image at url: $genusImageUrl")

    ElevatedCard(
        colors = CardDefaults.elevatedCardColors(MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.elevatedCardElevation(16.dp),
        shape = RoundedCornerShape(
            topStart = 0f,
            topEnd = 0f,
            bottomEnd = 50f,
            bottomStart = 50f
        ),
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .fillMaxHeight()
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(top = 20.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp, alignment = Alignment.Bottom),
        ) {
            AnimatedVisibility(visible = expanded) {
                LoadAsyncImageOrReserveDrawable(
                    imageUrl = genusImageUrl,
                    alignment = Alignment.CenterStart,
                    contentScale = ContentScale.Fit,
                    drawableIfImageFailed = R.drawable.unkn,
                    modifier = Modifier
                        .alpha(0.4f)
                        .padding(top = 10.dp, bottom = 0.dp, start = 40.dp)
//                    .offset(x = 20.dp, y = 0.dp)
                        .fillMaxWidth()
                        .weight(1f),
                )
            }
//            Spacer(Modifier.height(4.dp))
            Column (
                modifier = Modifier
                    .fillMaxHeight(1f)
                    .padding(bottom = 20.dp),
                verticalArrangement = Arrangement.Top
            ) {
                Text(
                    genus.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp,
                    fontStyle = FontStyle.Italic,
                    modifier = Modifier
                        .padding(start = padding, end = padding)
                        .offset(y = 8.dp)
                )
                genus.getNamePronunciation()?.let { pronunciation ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .padding(start = padding)
                            .alpha(0.6f)
                    ) {
                        Text(
                            pronunciation,
                            fontStyle = FontStyle.Italic,
                            modifier = Modifier.padding(vertical=12.dp)
                        )
                        IconButton(onClick = onPlayNamePronunciation) {
                            Icon(
                                Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = "play name pronunciation"
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun GenusDetail(
    genus: Genus,
    modifier: Modifier = Modifier,
    onPlayNamePronunciation: () -> Unit
) {
    val horizontalPadding = 12.dp

    val scrollState = rememberLazyListState()

    Surface(
        color = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,
        modifier = modifier
    ) {
        LazyColumn (
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = horizontalPadding),
            state = scrollState
        ) {
            item {
                GenusTitleCard(
                    genus,
                    padding = horizontalPadding,
                    scrollState = scrollState,
                    onPlayNamePronunciation = onPlayNamePronunciation
                )
            }
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(horizontal = horizontalPadding)
                ) {
                    LabelAttributeRow(
                        label = "Meaning",
                        value = genus.getNameMeaning(),
                        valueStyle = FontStyle.Italic
                    )
                    LabelAttributeRow(label = "Creature Type", value = genus.type.toString())
                }
            }
            item {
                HorizontalDivider(
                    Modifier
                        .padding(horizontal = 16.dp)
                        .alpha(0.4f))
            }
            item{
                Column (
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier=Modifier.padding(horizontal=horizontalPadding)
                ) {
                    LabelContentRow(label = "Diet", valueContent = { DietIconThin(genus.diet) })
                    LabelAttributeRow(label = "Length", value = genus.getLength())
                    LabelAttributeRow(label = "Weight", value = genus.getWeight())
                }
            }
            item { HorizontalDivider(
                Modifier
                    .padding(horizontal = 16.dp)
                    .alpha(0.4f)) }
            item {
                Column (
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(horizontal=horizontalPadding)
                ) {
                    LabelContentRow(
                        label = "Time Period",
                        valueContent = { TimePeriodIcon(timePeriod = genus.timePeriod) },
                        horizontalArrangement = Arrangement.SpaceBetween
                    )
                    LabelAttributeRow(label = "Years Lived", value = genus.yearsLived)
                }
            }
            item { HorizontalDivider(
                Modifier
                    .padding(horizontal = 16.dp)
                    .alpha(0.4f)) }
            item {
                Column (
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(horizontal=horizontalPadding)
                ) {
                    Text("Taxonomy:", modifier = Modifier.alpha(0.6f))
                    ShowTaxonomicTree(genus = genus, modifier = Modifier.fillMaxWidth())
                }
            }

            item { Spacer(Modifier.height(40.dp)) }
        }
    }
}

@Composable
fun ShowTaxonomicTree(
    genus: Genus,
    modifier: Modifier,
    internalCardPadding: PaddingValues = PaddingValues(16.dp)
) {

    val taxonomy = genus.getListOfTaxonomy()
    var tree = taxonomy[0]
    var indent = "└"
    for (taxon in taxonomy.drop(1)) {
        tree += "\n$indent $taxon"
        indent = "  $indent"
    }

    Card (
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        LazyRow (
            modifier = Modifier
                .padding(internalCardPadding)
                .fillMaxWidth()
        ) {
            item {
                Column {
                    Text(tree)
                    Text(
                        "$indent ${genus.name}",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Preview(
    widthDp = 300,
    heightDp = 800,
    name = "Light"
)
@Composable
fun PreviewGenusDetail() {
    val acro = GenusBuilderImpl("Acrocanthosaurus")
        .setDiet("Carnivorous")
        .splitTimePeriodAndYears("Early Cretaceous, 113-110 mya")
        .setNamePronunciation("'ACK-row-CAN-tho-SORE-us'")
        .setNameMeaning("high-spined lizard")
        .setLength("11-11.5 metres")
        .setWeight("4.4 tonnes")
        .setCreatureType("large theropod")
        .setTaxonomy("Dinosauria Saurischia Theropoda Carcharodontosauridae")
        .build()

    DinoDataTheme(darkTheme = false) {
        GenusDetail(acro, onPlayNamePronunciation = {})
    }
}

@Preview(widthDp = 300, heightDp = 800, name = "Dark")
@Composable
fun PreviewGenusDetailDark() {
    val styraco = GenusBuilderImpl("Styracosaurus")
        .setDiet("Herbivorous")
        .splitTimePeriodAndYears("Early Cretaceous, 70-65.5 mya")
        .setNamePronunciation("'sty-RAK-oh-SORE-us'")
        .setNameMeaning("spiked lizard")
        .setLength("5 metres")
        .setWeight("1 tonnes")
        .setCreatureType("ceratopsian")
        .setTaxonomy("Dinosauria Saurischia Ceratopsidae Centrosaurinae")
        .addImageUrlMap(imageData = mapOf(
            "sty" to MultiImageUrlData(
                "styracosaurus",
                listOf(
                    SingleImageUrlData("5fcab3ba-54b9-484c-9458-5f559f05c240",
                        imageSizes = listOf("4895x1877", "512x196"),
                        thumbSizes = listOf("192x192", "64x64")
                    )
                )
            )
        ))
        .build()

    DinoDataTheme(darkTheme = true) {
        GenusDetail(styraco, onPlayNamePronunciation = {})
    }
}
