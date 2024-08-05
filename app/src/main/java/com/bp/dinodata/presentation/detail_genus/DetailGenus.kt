package com.bp.dinodata.presentation.detail_genus

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.filled.AccessTimeFilled
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Interests
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.sharp.Restaurant
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bp.dinodata.R
import com.bp.dinodata.presentation.utils.ThemeConverter
import com.bp.dinodata.data.Diet
import com.bp.dinodata.data.genus.GenusBuilder
import com.bp.dinodata.data.MultiImageUrlData
import com.bp.dinodata.data.SingleImageUrlData
import com.bp.dinodata.data.TaxonTreeBuilder
import com.bp.dinodata.data.TimePeriod
import com.bp.dinodata.data.genus.DetailedGenus
import com.bp.dinodata.data.genus.GenusWithImages
import com.bp.dinodata.data.genus.IDetailedGenus
import com.bp.dinodata.data.genus.IGenus
import com.bp.dinodata.data.genus.IGenusWithImages
import com.bp.dinodata.data.genus.LocalPrefs
import com.bp.dinodata.data.quantities.IDescribesLength
import com.bp.dinodata.data.quantities.IDescribesWeight
import com.bp.dinodata.presentation.DataState
import com.bp.dinodata.presentation.convertCreatureTypeToString
import com.bp.dinodata.presentation.detail_genus.location_map.LocationAtlas
import com.bp.dinodata.presentation.icons.DietIconThin
import com.bp.dinodata.presentation.icons.chronology.MesozoicChronologyBar
import com.bp.dinodata.presentation.icons.chronology.TimeInterval
import com.bp.dinodata.presentation.icons.TimePeriodIcon
import com.bp.dinodata.presentation.icons.chronology.ITimeInterval
import com.bp.dinodata.presentation.icons.chronology.TimeChronologyBar
import com.bp.dinodata.presentation.utils.LoadingItemsPlaceholder
import com.bp.dinodata.presentation.utils.dialog.ColorPickerDialog
import com.bp.dinodata.theme.DinoDataTheme


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


@Composable
fun GenusDetailScreenContent(
    uiState: DetailScreenUiState,
    modifier: Modifier = Modifier,
    onEvent: (DetailGenusUiEvent) -> Unit
) {
    val genus = uiState.getGenusData()
    val colorPickerDialogVisible by remember { mutableStateOf(uiState.colorSelectDialogVisible) }

    Crossfade(genus, label="crossfade_genus_null") {
        if (it == null) {
            LoadingItemsPlaceholder()
        }
        else {
            ShowGenusDetail(
                uiState = uiState,
                genus = it,
                onEvent = onEvent,
                colorDialogVisible = colorPickerDialogVisible,
                modifier = modifier
            )
        }
    }
}

@Composable
fun ShowGenusDetail(
    uiState: DetailScreenUiState,
    colorDialogVisible: Boolean,
    genus: IDetailedGenus,
    onEvent: (DetailGenusUiEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val outerPadding = remember { 8.dp }
    val innerPadding = remember { 12.dp }

    val scrollState = rememberLazyListState()

    val cardHeight = remember { 240.dp }

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

    val selectedColor by remember { mutableStateOf(genus.getSelectedColorName()) }
    val colorScheme by remember { derivedStateOf { ThemeConverter.getTheme(selectedColor) } }
    var colorPickerDialogVisible by remember { mutableStateOf(colorDialogVisible) }
    var preferencesControlsExpanded by remember { mutableStateOf(uiState.preferencesCardExpanded) }

    AnimatedVisibility (
        colorPickerDialogVisible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        ColorPickerDialog(
            initiallySelectedColor = selectedColor,
            onColorPicked = { onEvent(DetailGenusUiEvent.SelectColor(it)) },
            onClose = {
                colorPickerDialogVisible = false
                onEvent(DetailGenusUiEvent.ShowColorSelectDialog(false))
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
            GenusTitleCardAndControls(
                genus,
                onPlayNamePronunciation = { onEvent(DetailGenusUiEvent.PlayNamePronunciation) },
                modifier = Modifier.height(cardHeight),
                visibleImageIndex = genus.getPreferredImageIndex(),
                innerPadding = innerPadding,
                canPlayPronunciation = uiState.canPlayPronunciationAudio,
                isFavourite = genus.isUserFavourite(),
                colorScheme = colorScheme ?: MaterialTheme.colorScheme,
                setColorPickerDialogVisibility = {
                    colorPickerDialogVisible = it
                    onEvent(DetailGenusUiEvent.ShowColorSelectDialog(visible = it))
                },
                toggleItemAsFavourite = {
                    onEvent(DetailGenusUiEvent.ToggleItemFavouriteStatus(it))
                },
                controlsExpanded = preferencesControlsExpanded,
                showControls = {
                    preferencesControlsExpanded = it
                    onEvent(DetailGenusUiEvent.SetPreferencesCardExpansion(it))
                },
                updateVisibleImageIndex = { increment ->
                    if (increment != 0) {
                        onEvent(DetailGenusUiEvent.UpdateVisibleImageIndex(increment))
                    }
                }
            )
        }
        item {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .padding(horizontal = innerPadding + outerPadding / 2)
                    .padding(top = 16.dp)
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
                    timeInterval = genus.getTimeIntervalLived(),
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

                val locations = genus.getLocations()
                if (locations.isNotEmpty()) {
                    sectionDivider()
                    CreatureLocations(locations, iconModifier)
                }

                if (genus.hasSpeciesInfo()) {
                    sectionDivider()
                    ShowCreatureSpeciesCards(genus, iconModifier)
                }
            }
            Spacer(Modifier.height(100.dp))
        }
    }
}


@Composable
fun UpdateGenusLocalPreferencesButtons(
    setColorPickerDialogVisibility: (Boolean) -> Unit,
    toggleItemAsFavourite: (Boolean) -> Unit,
    isFavourite: Boolean,
    modifier: Modifier = Modifier,
    hideButtons: (() -> Unit)? = null,
) {
    val context = LocalContext.current

    Row (
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
    ) {
        Spacer(modifier=Modifier.weight(1f))
        Button(
            onClick = {
                val nowIsFavourite = !isFavourite
                toggleItemAsFavourite(nowIsFavourite)

                if (nowIsFavourite) {
                    Toast.makeText(context,
                        context.getString(R.string.toast_added_favourite), Toast.LENGTH_SHORT).show()
                }
                else {
                    Toast.makeText(context,
                        context.getString(R.string.toast_removed_favourite), Toast.LENGTH_SHORT).show()
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Crossfade (isFavourite, label="favouriteButtonFade") {
                Row (
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (it) {
                        Icon(
                            Icons.Filled.Remove,
                            contentDescription = stringResource(R.string.description_remove_favourite)
                        )
                        Text(
                            stringResource(id = R.string.action_remove_favourite)
                        )
                    } else {
                        Icon(
                            Icons.Filled.Add,
                            contentDescription = stringResource(R.string.description_add_to_favourite)
                        )
                        Text(
                            stringResource(id = R.string.action_set_favourite)
                        )
                    }
                }
            }
        }
        IconButton(
            onClick = { setColorPickerDialogVisibility(true) },
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Icon(
                Icons.Filled.ColorLens,
                contentDescription = "choose the color"
            )
        }
        Spacer(modifier=Modifier.weight(1f))
        hideButtons?.let {
            IconButton(
                onClick = hideButtons,
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
            ) {
                Icon(
                    Icons.Filled.KeyboardArrowUp,
                    contentDescription = "hide buttons"
                )
            }
        }
    }
}

@Composable
fun CreatureLocations(locations: List<String>, iconModifier: Modifier) {
    Column (
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        LabelContentRow(
            label = stringResource(R.string.label_locations),
            valueContent = {
//                Text(locations.joinToString())
            },
            leadingIcon = {
                Icon(
                    Icons.Filled.LocationOn, null,
                    modifier = iconModifier
                )
            }
        )
        LocationAtlas(locations, modifier = Modifier.shadow(4.dp))
    }
}

@Composable
fun CreatureTimePeriod(
    timePeriod: TimePeriod?,
    yearsLived: String?,
    timeInterval: ITimeInterval?,
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

        timeInterval?.let {
            Spacer(modifier = Modifier.height(4.dp))
            TimeChronologyBar(
                timeInterval,
                modifier = Modifier.padding(4.dp)
            )
        }
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


@Composable
fun ShowCreatureSpeciesCards(
    genus: IGenus,
    iconModifier: Modifier = Modifier
) {

    Column (
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        LabelContentRow(
            label = stringResource(R.string.label_known_species),
            valueContent = {},
            leadingIcon = {
                Icon(
                    Icons.Filled.Interests,
                    null,
                    modifier = iconModifier
                )
            }
        )
        Spacer(modifier=Modifier.height(4.dp))
        genus.getSpeciesList().forEach {
            SpeciesListItem(it, genus.getName())
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
    val acro = GenusBuilder("Acrocanthosaurus")
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
        genusData = DataState.Success(DetailedGenus(acroWithImages))
    )

    DinoDataTheme(darkTheme = false) {
        Surface (color = MaterialTheme.colorScheme.background) {
            GenusDetailScreenContent(
                uiState = uiState,
                onEvent = {}
            )
        }
    }
}

@Preview(widthDp = 300, heightDp = 1200, name = "Dark")
@Composable
fun PreviewGenusDetailDark() {
    val styraco = GenusBuilder("Styracosaurus")
        .setDiet("Herbivorous")
        .splitTimePeriodAndYears("Early Cretaceous, 70-65.5 mya")
        .setNamePronunciation("'sty-RAK-oh-SORE-us'")
        .setNameMeaning("spiked lizard")
        .setLength("5 metres")
        .setWeight("1 tonnes")
        .setCreatureType("ceratopsian")
        .setStartMya("75.5")
        .setEndMya("74.5")
        .setTaxonomy(listOf("Dinosauria", "Saurischia", "Ceratopsidae", "Centrosaurinae"))
        .setLocations(listOf("Canada", "USA"))
        .setSpecies(
            listOf(
                mapOf(
                    "name" to "albertensis",
                    "discovered_by" to "Marsh",
                    "discovered_year" to "1885",
                    "is_type" to "true"
                )
            )
        )
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
        genusData = DataState.Success(
            DetailedGenus(
                GenusWithImages(styraco, imageMap),
                LocalPrefs(_isFavourite = false)
            )
        ),
        listOfColors = ThemeConverter.listOfColors,
        colorSelectDialogVisible = false,
        preferencesCardExpanded = true
    )

    DinoDataTheme(darkTheme = true) {
        Surface (color = MaterialTheme.colorScheme.background) {
            GenusDetailScreenContent(
                uiState = uiState,
                onEvent = {}
            )
        }
    }
}
