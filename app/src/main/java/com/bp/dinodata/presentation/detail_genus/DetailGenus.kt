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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
import com.bp.dinodata.data.genus.GenusBuilder
import com.bp.dinodata.data.MultiImageUrlData
import com.bp.dinodata.data.SingleImageUrlData
import com.bp.dinodata.data.genus.DetailedGenus
import com.bp.dinodata.data.genus.GenusWithImages
import com.bp.dinodata.data.genus.IDetailedGenus
import com.bp.dinodata.data.genus.IHasFormationInfo
import com.bp.dinodata.data.genus.LocalPrefs
import com.bp.dinodata.presentation.DataState
import com.bp.dinodata.presentation.detail_genus.card_fragments.CreatureDietAndMeasurements
import com.bp.dinodata.presentation.detail_genus.card_fragments.CreatureFormations
import com.bp.dinodata.presentation.detail_genus.card_fragments.CreatureLocations
import com.bp.dinodata.presentation.detail_genus.card_fragments.CreatureNameMeaningAndType
import com.bp.dinodata.presentation.detail_genus.card_fragments.CreatureTimePeriod
import com.bp.dinodata.presentation.detail_genus.card_fragments.ShowCreatureSpeciesCards
import com.bp.dinodata.presentation.detail_genus.card_fragments.ShowTaxonomicTree
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
        Spacer(modifier = Modifier.weight(1f).widthIn(min=50.dp))
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
    val visibleDialogState by remember { mutableStateOf(uiState.dialogState) }

    Crossfade(genus, label="crossfade_genus_null") {
        if (it == null) {
            LoadingItemsPlaceholder()
        }
        else {
            ShowGenusDetail(
                uiState = uiState,
                genus = it,
                onEvent = onEvent,
                visibleDialogState = visibleDialogState,
                modifier = modifier
            )
        }
    }
}

@Composable
fun ShowGenusDetail(
    uiState: DetailScreenUiState,
    visibleDialogState: DetailScreenDialogState,
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
    var visibleDialog by remember { mutableStateOf(visibleDialogState) }
    var preferencesControlsExpanded by remember { mutableStateOf(uiState.preferencesCardExpanded) }

    // Color-Picker Dialog Pop-up
    AnimatedVisibility (
        visibleDialog == DetailScreenDialogState.ColorPickerDialog,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        ColorPickerDialog(
            initiallySelectedColor = selectedColor,
            onColorPicked = { onEvent(DetailGenusUiEvent.SelectColor(it)) },
            onClose = {
                visibleDialog = DetailScreenDialogState.NoDialog
                onEvent(DetailGenusUiEvent.ShowColorSelectDialog(false))
            }
        )
    }

    // Image-View Dialog Pop-up
    AnimatedVisibility (
        visibleDialog == DetailScreenDialogState.ImageView,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        EnlargedImageDialog(
            genus,
            onHide = {
                visibleDialog = DetailScreenDialogState.NoDialog
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
                showLargeImageDialog = {
                    visibleDialog = DetailScreenDialogState.ImageView
                    onEvent(DetailGenusUiEvent.ShowLargeImageDialog(true))
                },
                innerPadding = innerPadding,
                canPlayPronunciation = uiState.canPlayPronunciationAudio,
                isFavourite = genus.isUserFavourite(),
                colorScheme = colorScheme ?: MaterialTheme.colorScheme,
                setColorPickerDialogVisibility = {
                    visibleDialog = if (it) {
                        DetailScreenDialogState.ColorPickerDialog
                    } else {
                        DetailScreenDialogState.NoDialog
                    }
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
                    item = genus,
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

                val formations = genus.getFormationNames()
                if (formations.isNotEmpty()) {
                    Spacer(Modifier.height(8.dp))
                    CreatureFormations(formations, iconModifier)
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


//@Preview(
//    widthDp = 300,
//    heightDp = 500,
//    name = "Light"
//)
//@Composable
//fun PreviewGenusDetail() {
//    val acro = GenusBuilder("Acrocanthosaurus")
//        .setDiet("Carnivorous")
//        .splitTimePeriodAndYears("Early Cretaceous, 113-110 mya")
////        .setNamePronunciation("'ACK-row-CAN-tho-SORE-us'")
//        .setNameMeaning("high-spined lizard")
//        .setLength("11-11.5 metres")
//        .setWeight("4.4 tonnes")
//        .setCreatureType("large theropod")
//        .setTaxonomy(listOf("Dinosauria", "Saurischia", "Theropoda", "Carcharodontosauridae"))
//        .build()
//
//    val acroWithImages = GenusWithImages(acro)
//
//    val uiState = DetailScreenUiState(
//        genusName = acro.getName(),
//        genusData = DataState.Success(DetailedGenus(acroWithImages))
//    )
//
//    DinoDataTheme(darkTheme = false) {
//        Surface (color = MaterialTheme.colorScheme.background) {
//            GenusDetailScreenContent(
//                uiState = uiState,
//                onEvent = {}
//            )
//        }
//    }
//}

@Preview(widthDp = 400, heightDp = 2000, name = "Dark")
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
        .setFormations(listOf("Alberta Woodland Formation"))
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
                SingleImageUrlData(
                    "Styracosaurus-right",
                    "5fcab3ba-54b9-484c-9458-5f559f05c240",
                    imageSizes = listOf("4895x1877", "512x196"),
                    thumbSizes = listOf("192x192", "64x64")
                )
            )
        ),
        "sty2" to MultiImageUrlData(
            "styracosaurus",
            listOf(
                SingleImageUrlData(
                    "Styracosaurus-left",
                    "5fcab3ba-54b9-484c-9458-5f559f05c240",
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
        dialogState = DetailScreenDialogState.NoDialog,
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
