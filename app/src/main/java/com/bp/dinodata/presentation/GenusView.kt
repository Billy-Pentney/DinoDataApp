package com.bp.dinodata.presentation

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.mutableFloatStateOf
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import com.bp.dinodata.R
import com.bp.dinodata.data.Genus
import com.bp.dinodata.data.GenusBuilderImpl
import com.bp.dinodata.data.MultiImageUrlData
import com.bp.dinodata.data.SingleImageUrlData
import com.bp.dinodata.data.TaxonTreeBuilder
import com.bp.dinodata.presentation.icons.DietIconThin
import com.bp.dinodata.presentation.icons.TimePeriodIcon
import com.bp.dinodata.presentation.utils.convertCreatureTypeToSilhouette
import com.bp.dinodata.theme.DinoDataTheme
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import kotlin.math.min


@Composable
fun LabelAttributeRow(
    label: String,
    value: String?,
    units: String? = null,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.SpaceBetween,
    valueStyle: FontStyle = FontStyle.Normal,
    valueTextAlign: TextAlign = TextAlign.End,
    labelAlpha: Float = 0.6f,
    labelFontWeight: FontWeight = FontWeight.Bold
) {
    Row (
        horizontalArrangement = horizontalArrangement,
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "$label: ",
            modifier = Modifier.alpha(labelAlpha),
            fontWeight = labelFontWeight
        )
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
    horizontalArrangement: Arrangement.Horizontal = Arrangement.SpaceBetween,
    labelAlpha: Float = 0.6f,
    labelFontWeight: FontWeight = FontWeight.Bold
) {
    Row (
        horizontalArrangement=horizontalArrangement,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            "$label: ", modifier=Modifier.alpha(labelAlpha),
            fontWeight = labelFontWeight
        )
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
    scrollState: LazyListState,
    onPlayNamePronunciation: () -> Unit,
    modifier: Modifier = Modifier,
    innerPadding: Dp = 8.dp,
    paddingValues: PaddingValues = PaddingValues(),
    collapseSpeed: Float = 20f
) {
    val silhouetteId = convertCreatureTypeToSilhouette(genus.type)
    val genusImageUrl = genus.mainImageUrl

    val scaleDueToScroll = remember {
        mutableFloatStateOf(
            1f - min(1f,scrollState.firstVisibleItemScrollOffset / collapseSpeed)
        )
    }

    Log.d("GenusDetail", "Showing image at url: $genusImageUrl")

    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp,
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
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .padding(top = 30.dp),
            contentAlignment = Alignment.BottomStart
        ) {
            LoadAsyncImageOrReserveDrawable(
                imageUrl = genusImageUrl,
                alignment = Alignment.CenterEnd,
                contentScale = ContentScale.Fit,
                drawableIfImageFailed = R.drawable.ornith,
                modifier = Modifier
                    .fillMaxHeight(
                        0.2f + scaleDueToScroll.floatValue
                    )
                    .padding(bottom = 70.dp,
                        start = 40.dp, end = 20.dp)
                    .offset(x = 10.dp, y = 0.dp)
                    .fillMaxWidth()
                    .alpha(
                        0.1f * scaleDueToScroll.floatValue
                    )
                    .animateContentSize()
            )

            Spacer(Modifier.height(8.dp))

            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = innerPadding),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column (
                    modifier = Modifier,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val pronunciation = genus.getNamePronunciation()

                    val titleOffset = if (pronunciation != null) 10.dp else 0.dp
                    val titleBottomPadding = innerPadding - titleOffset

                    Text(
                        genus.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 26.sp,
                        fontStyle = FontStyle.Italic,
                        modifier = Modifier
                            .padding(bottom = titleBottomPadding)
                            .offset(y = titleOffset)
                    )

                    pronunciation?.let {
                        Text(
                            pronunciation,
                            fontStyle = FontStyle.Italic,
                            modifier = Modifier
                                .alpha(0.6f)
                                .padding(bottom = innerPadding)
                        )
                    }
                }
                IconButton(
                    onClick = onPlayNamePronunciation,
                    modifier = Modifier.padding(bottom=1.dp)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "play name pronunciation"
                    )
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
    val outerPadding = 8.dp
    val innerPadding = 12.dp

    val scrollState = rememberLazyListState()
    val cardExpansion = remember {
        derivedStateOf { 0f }
            //scrollState.firstVisibleItemScrollOffset }
    }

    val cardHeight = max(150.dp, 220.dp - (cardExpansion.value/2).dp)

    Surface(
        color = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            state = scrollState,
            contentPadding = PaddingValues(horizontal=outerPadding/2),
            modifier = modifier
        ) {
            item {
                GenusTitleCard(
                    genus,
                    innerPadding = innerPadding,
                    scrollState = scrollState,
                    onPlayNamePronunciation = onPlayNamePronunciation,
                    modifier = Modifier.height(cardHeight),
                    paddingValues = PaddingValues(
                        start = outerPadding,
                        end = outerPadding,
                        bottom = 10.dp
                    )
                )
            }
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(
                        horizontal = innerPadding + outerPadding/2
                    )
                ) {
                    LabelAttributeRow(
                        label = stringResource(R.string.label_meaning),
                        value = genus.getNameMeaning(),
                        valueStyle = FontStyle.Italic
                    )
                    LabelAttributeRow(
                        label = stringResource(R.string.label_creature_type),
                        value = convertCreatureTypeToString(genus.type)
                    )
                    HorizontalDivider(
                        Modifier
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .alpha(0.5f)
                    )
                    LabelContentRow(label = stringResource(R.string.label_diet), valueContent = { DietIconThin(genus.diet) })
                    LabelAttributeRow(label = stringResource(R.string.label_length), value = genus.getLength())
                    LabelAttributeRow(label = stringResource(R.string.label_weight), value = genus.getWeight())

                    HorizontalDivider(
                        Modifier
                            .padding(horizontal = 16.dp)
                            .alpha(0.4f)
                    )

                    LabelContentRow(
                        label = stringResource(R.string.label_time_period),
                        valueContent = { TimePeriodIcon(timePeriod = genus.timePeriod) },
                        horizontalArrangement = Arrangement.SpaceBetween
                    )
                    LabelAttributeRow(label = stringResource(R.string.label_years_lived), value = genus.yearsLived)
                    HorizontalDivider(
                        Modifier
                            .padding(horizontal = 16.dp)
                            .alpha(0.4f)
                    )

                    LabelContentRow(
                        label = stringResource(R.string.label_taxonomy),
                        valueContent = {}
                    )
                    ShowTaxonomicTree(genus = genus, modifier = Modifier.fillMaxWidth())
                }
                Spacer(Modifier.height(100.dp))
            }
        }
    }
}

@Composable
fun ShowTaxonomicTree(
    genus: Genus,
    modifier: Modifier,
    internalCardPadding: PaddingValues = PaddingValues(16.dp)
) {
    var taxonTreeUptoLast: List<String> by remember { mutableStateOf(emptyList()) }
    var finalChild: String by remember { mutableStateOf("") }

    LaunchedEffect(genus) {
        val taxonomy = genus.getListOfTaxonomy()
        val taxonBuilder = TaxonTreeBuilder(taxonomy)
        val taxonTree = taxonBuilder.getPrintableTree(genus = genus.name)
        taxonTreeUptoLast = taxonTree.dropLast(1)
        finalChild = taxonTree.last()
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
                    Text(
                        taxonTreeUptoLast.joinToString("\n"),
//                        lineHeight = 18.sp
                    )
                    Text(
                        finalChild,
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
    heightDp = 500,
    name = "Light"
)
@Composable
fun PreviewGenusDetail() {
    val acro = GenusBuilderImpl("Acrocanthosaurus")
        .setDiet("Carnivorous")
        .splitTimePeriodAndYears("Early Cretaceous, 113-110 mya")
//        .setNamePronunciation("'ACK-row-CAN-tho-SORE-us'")
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
