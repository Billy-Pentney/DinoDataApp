package com.bp.dinodata.presentation.detail_genus

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AccessTimeFilled
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.sharp.Restaurant
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import com.bp.dinodata.R
import com.bp.dinodata.data.ThemeConverter
import com.bp.dinodata.data.Diet
import com.bp.dinodata.data.genus.GenusBuilderImpl
import com.bp.dinodata.data.MultiImageUrlData
import com.bp.dinodata.data.SingleImageUrlData
import com.bp.dinodata.data.TaxonTreeBuilder
import com.bp.dinodata.data.TimePeriod
import com.bp.dinodata.data.genus.DetailedGenus
import com.bp.dinodata.data.genus.GenusWithImages
import com.bp.dinodata.data.genus.IGenus
import com.bp.dinodata.data.genus.IGenusWithImages
import com.bp.dinodata.data.quantities.IDescribesLength
import com.bp.dinodata.data.quantities.IDescribesWeight
import com.bp.dinodata.presentation.convertCreatureTypeToString
import com.bp.dinodata.presentation.icons.DietIconThin
import com.bp.dinodata.presentation.icons.TimePeriodIcon
import com.bp.dinodata.presentation.utils.NoDataPlaceholder
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
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(6.dp),
    valueStyle: FontStyle = FontStyle.Normal,
    valueTextAlign: TextAlign = TextAlign.End,
    labelAlpha: Float = 0.6f,
    labelFontSize: TextUnit = 18.sp,
    labelFontWeight: FontWeight = FontWeight.Bold,
    leadingIcon: @Composable () -> Unit = {}
) {
    Row (
        horizontalArrangement = horizontalArrangement,
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        leadingIcon.invoke()
        Text(
            "$label: ",
            modifier = Modifier.alpha(labelAlpha),
            fontWeight = labelFontWeight,
            fontSize = labelFontSize
        )
        Spacer(modifier = Modifier.weight(1f))
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
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(6.dp),
    labelAlpha: Float = 0.6f,
    labelFontWeight: FontWeight = FontWeight.Bold,
    labelFontSize: TextUnit = 18.sp,
    leadingIcon: @Composable () -> Unit = {}
) {
    Row (
        horizontalArrangement=horizontalArrangement,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        leadingIcon.invoke()
        Text(
            "$label: ", modifier=Modifier.alpha(labelAlpha),
            fontWeight = labelFontWeight,
            fontSize = labelFontSize
        )
        Spacer(modifier = Modifier.weight(1f))
        valueContent.invoke()
    }
}


@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun GenusTitleCard(
    genus: IGenusWithImages,
    scrollState: LazyListState,
    onPlayNamePronunciation: () -> Unit,
    modifier: Modifier = Modifier,
    innerPadding: Dp = 8.dp,
    paddingValues: PaddingValues = PaddingValues(),
    collapseSpeed: Float = 20f,
    colorScheme: ColorScheme? = null
) {
//    val silhouetteId = convertCreatureTypeToSilhouette(genus.type)

    var imageIndex by remember { mutableIntStateOf(0) }
    val genusImageUrl = genus.getImageUrl(imageIndex)
    val totalImages = genus.getNumDistinctImages()

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
        Column (
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier.fillMaxHeight()
        ) {
            Box (
                contentAlignment = Alignment.BottomEnd
            ) {
                GlideImage(
                    model = genusImageUrl,
                    alignment = Alignment.CenterEnd,
                    contentScale = ContentScale.Fit,
                    failure = placeholder(R.drawable.unkn),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxHeight(0.2f + 0.4f * scaleDueToScroll.floatValue)
                        .padding(start = 40.dp, end = 20.dp, bottom = 30.dp)
                        .offset(x = 10.dp, y = 0.dp)
                        .fillMaxWidth()
                        .alpha(
                            0.3f * scaleDueToScroll.floatValue
                        )
                        .animateContentSize()
                )
                if (totalImages > 1) {
//                    Text("Showing $genusImageUrl")
                    Row {
                        IconButton(
                            onClick = { imageIndex-- },
                            enabled = imageIndex > 0
                        ) {
                            Icon(
                                Icons.Filled.ChevronLeft,
                                contentDescription = "switch to previous image"
                            )
                        }
                        IconButton(
                            onClick = { imageIndex++ },
                            enabled = imageIndex < totalImages-1
                        ) {
                            Icon(
                                Icons.Filled.ChevronRight,
                                contentDescription = "switch to next image"
                            )
                        }
                    }
                }
            }

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
                        genus.getName(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 26.sp,
                        fontStyle = FontStyle.Italic,
                        modifier = Modifier
                            .padding(bottom = titleBottomPadding)
                            .offset(y = titleOffset)
                    )

                    pronunciation?.let {
                        Row (
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .alpha(0.6f)
                                .padding(bottom = innerPadding, top = 2.dp)
                        ) {
                            Icon(
                                Icons.Filled.RecordVoiceOver,
                                null,
                                modifier=Modifier.height(20.dp),
                            )
                            Text(
                                pronunciation,
                                fontStyle = FontStyle.Italic
                            )
                        }
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
fun GenusDetailScreenContent(
    uiState: DetailScreenUiState,
    modifier: Modifier = Modifier,
    onPlayNamePronunciation: () -> Unit,
    setColorPickerDialogVisibility: (Boolean) -> Unit,
    onColorSelect: (String) -> Unit,
) {
    val outerPadding = 8.dp
    val innerPadding = 12.dp

    val scrollState = rememberLazyListState()
    val cardExpansion = remember { derivedStateOf { 0f } }

    val cardHeight = max(150.dp, 250.dp - (cardExpansion.value/2).dp)
    
    val iconModifier = Modifier
        .height(20.dp)
        .alpha(0.75f)

    val sectionDivider: @Composable () -> Unit = {
        HorizontalDivider(
            Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .alpha(0.5f)
        )
    }

    val genus = uiState.genusData
    val colorScheme = ThemeConverter.getTheme(uiState.selectedColorName)

    if (genus == null) {
        NoDataPlaceholder()
    }
    else {
        if (uiState.colorSelectDialogVisibility) {
            ColorPickerDialog(
                selectedColor = uiState.selectedColorName,
                colorNames = uiState.listOfColors,
                onSelect = onColorSelect,
                onClose = {
                    setColorPickerDialogVisibility(false)
                }
            )
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            state = scrollState,
            contentPadding = PaddingValues(horizontal = outerPadding / 2),
            modifier = modifier.background(MaterialTheme.colorScheme.background)
        ) {
            item {
                MaterialTheme(
                    colorScheme = colorScheme ?: MaterialTheme.colorScheme
                ) {
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
                        ),
                        colorScheme = colorScheme
                    )
                }
            }
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(
                        horizontal = innerPadding + outerPadding / 2
                    )
                ) {
                    CreatureNameMeaningAndType(genus, iconModifier = iconModifier)
                    sectionDivider()
                    CreatureDietAndMeasurements(
                        diet = genus.getDiet(),
                        length = genus.getLength(),
                        weight = genus.getWeight(),
                        iconModifier = iconModifier
                    )
                    sectionDivider()
                    CreatureTimePeriod(
                        timePeriod = genus.getTimePeriod(),
                        yearsLived = genus.getYearsLived(),
                        iconModifier = iconModifier
                    )
                    sectionDivider()
                    LabelContentRow(
                        label = stringResource(R.string.label_taxonomy),
                        valueContent = {},
                        leadingIcon = {
                            Image(
                                painterResource(id = R.drawable.icon_filled_taxon_tree),
                                null,
                                modifier = iconModifier,
                                colorFilter = ColorFilter.tint(LocalContentColor.current)
                            )
                        }
                    )
                    ShowTaxonomicTree(genus = genus, modifier = Modifier.fillMaxWidth())

                    if (genus.getLocations().isNotEmpty()) {
                        sectionDivider()
                        CreatureLocations(
                            locations = genus.getLocations(),
                            iconModifier = iconModifier
                        )
                    }

                    Spacer(modifier=Modifier.height(30.dp))

                    Row (
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(
                            onClick = { setColorPickerDialogVisibility(true) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                                contentColor = MaterialTheme.colorScheme.onSurface
                            )
                        ) {
                            Text("PICK COLOR")
                        }
                    }
                }
                Spacer(Modifier.height(100.dp))
            }
        }
    }
//    }
}

@Composable
fun CreatureLocations(locations: List<String>, iconModifier: Modifier) {
    LabelContentRow(
        label = stringResource(R.string.label_locations),
        valueContent = { Text(locations.joinToString()) },
        leadingIcon = {
            Icon(
                Icons.Filled.LocationOn, null,
                modifier = iconModifier
            )
        }
    )
}

@Composable
fun CreatureTimePeriod(
    timePeriod: TimePeriod?,
    yearsLived: String?,
    iconModifier: Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier=Modifier.fillMaxWidth()
    ) {
        LabelContentRow(
            label = stringResource(R.string.label_time_period),
            valueContent = { TimePeriodIcon(timePeriod) },
            leadingIcon = {
                Icon(Icons.Filled.CalendarMonth, null, modifier = iconModifier)
            }
        )
        LabelAttributeRow(
            label = stringResource(R.string.label_years_lived),
            value = yearsLived,
            leadingIcon = {
                Icon(Icons.Filled.AccessTimeFilled, null, modifier = iconModifier)
            }
        )
    }
}

@Composable
fun CreatureDietAndMeasurements(
    diet: Diet?,
    length: IDescribesLength?,
    weight: IDescribesWeight?,
    iconModifier: Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier=Modifier.fillMaxWidth()
    ) {
        LabelContentRow(
            label = stringResource(R.string.label_diet),
            valueContent = { DietIconThin(diet) },
            leadingIcon = {
                Icon(
                    Icons.Sharp.Restaurant,
                    contentDescription = null,
                    modifier = iconModifier
                )
            }
        )
        length?.let {
            LabelAttributeRow(
                label = stringResource(R.string.label_length),
                value = length.toString(),
                leadingIcon = {
                    Image(
                        painterResource(R.drawable.icon_filled_ruler),
                        null,
                        modifier = iconModifier,
                        colorFilter = ColorFilter.tint(LocalContentColor.current)
                    )
                }
            )
        }
        weight?.let {
            LabelAttributeRow(
                label = stringResource(R.string.label_weight),
                value = weight.toString(),
                leadingIcon = {
                    Image(
                        painterResource(R.drawable.icon_filled_weight),
                        null,
                        modifier = iconModifier,
                        colorFilter = ColorFilter.tint(LocalContentColor.current)
                    )
                }
            )
        }
    }
}

@Composable
fun CreatureNameMeaningAndType(
    genus: IGenusWithImages,
    iconModifier: Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier=Modifier.fillMaxWidth()
    ) {
        LabelAttributeRow(
            label = stringResource(R.string.label_meaning),
            value = genus.getNameMeaning(),
            valueStyle = FontStyle.Italic,
            leadingIcon = {
                Icon(Icons.Filled.Book, null, modifier = iconModifier)
            }
        )
        LabelAttributeRow(
            label = stringResource(R.string.label_creature_type),
            value = convertCreatureTypeToString(genus.getCreatureType()),
            leadingIcon = {
                Icon(
                    Icons.AutoMirrored.Filled.Label,
                    null,
                    modifier = iconModifier
                )
            }
        )
    }
}

@Composable
fun ShowTaxonomicTree(
    genus: IGenus,
    modifier: Modifier,
    internalCardPadding: PaddingValues = PaddingValues(16.dp)
) {
    var taxonTreeUptoLast: List<String> by remember { mutableStateOf(emptyList()) }
    var finalChild: String by remember { mutableStateOf("") }

    LaunchedEffect(genus) {
        val taxonomy = genus.getListOfTaxonomy()
        val taxonBuilder = TaxonTreeBuilder(taxonomy)
        val taxonTree = taxonBuilder.getPrintableTree(genus = genus.getName())
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
                        color = MaterialTheme.colorScheme.scrim,
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
        .setTaxonomy(listOf("Dinosauria", "Saurischia", "Theropoda", "Carcharodontosauridae"))
        .build()

    val acroWithImages = GenusWithImages(acro)

    val uiState = DetailScreenUiState(
        genusName = acro.getName(),
        genusData = DetailedGenus(acroWithImages),
        selectedColorName = "RED"
    )

    DinoDataTheme(darkTheme = false) {
        Surface (color = MaterialTheme.colorScheme.background) {
            GenusDetailScreenContent(
                uiState = uiState,
                onPlayNamePronunciation = {},
                setColorPickerDialogVisibility = {},
                onColorSelect = {}
            )
        }
    }
}

@Preview(widthDp = 300, heightDp = 1200, name = "Dark")
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
        .setTaxonomy(listOf("Dinosauria", "Saurischia", "Ceratopsidae", "Centrosaurinae"))
        .setLocations(listOf("Canada", "USA"))
        .build()

    val imageMap = mapOf(
        "sty" to MultiImageUrlData(
            "styracosaurus",
            listOf(
                SingleImageUrlData("5fcab3ba-54b9-484c-9458-5f559f05c240",
                    imageSizes = listOf("4895x1877", "512x196"),
                    thumbSizes = listOf("192x192", "64x64")
                )
            )
        ),
        "sty2" to MultiImageUrlData(
            "styracosaurus",
            listOf(
                SingleImageUrlData("5fcab3ba-54b9-484c-9458-5f559f05c240",
                    imageSizes = listOf("4895x1877", "512x196"),
                    thumbSizes = listOf("192x192", "64x64")
                )
            )
        )
    )

    val uiState = DetailScreenUiState(
        genusName = styraco.getName(),
        genusData = DetailedGenus(GenusWithImages(styraco, imageMap)),
        selectedColorName = "YELLOW",
        listOfColors = ThemeConverter.listOfColors,
        colorSelectDialogVisibility = true
    )

    DinoDataTheme(darkTheme = true) {
        Surface (color = MaterialTheme.colorScheme.background) {
            GenusDetailScreenContent(
                uiState = uiState,
                onPlayNamePronunciation = {},
                setColorPickerDialogVisibility = {},
                onColorSelect = {}
            )
        }
    }
}
