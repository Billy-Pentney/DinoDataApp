package com.bp.dinodata.presentation.detail_genus

import android.widget.Toast
import androidx.compose.animation.Crossfade
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
import androidx.compose.runtime.State
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
import com.bp.dinodata.data.Formation
import com.bp.dinodata.presentation.utils.ThemeConverter
import com.bp.dinodata.data.MultiImageUrlData
import com.bp.dinodata.data.SingleImageUrlData
import com.bp.dinodata.data.genus.DebugGenusFactory
import com.bp.dinodata.data.genus.DetailedGenus
import com.bp.dinodata.data.genus.GenusWithImages
import com.bp.dinodata.data.genus.IDetailedGenus
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
        Spacer(modifier = Modifier
            .weight(1f)
            .widthIn(min = 50.dp))
        valueContent.invoke()
    }
}


@Composable
fun GenusDetailScreenContent(
    uiState: DetailScreenUiState,
    dialogState: State<DetailScreenDialogState>,
    modifier: Modifier = Modifier,
    onEvent: (DetailGenusUiEvent) -> Unit
) {
    val genus = uiState.getGenusData()

    Crossfade(genus, label="crossfade_genus_null") {
        if (it == null) {
            LoadingItemsPlaceholder()
        }
        else {
            ShowGenusDetail(
                genus = it,
                onEvent = onEvent,
                visibleDialogState = dialogState,
                isPreferencesCardExpanded = uiState.preferencesCardExpanded,
                canPlayPronunciationAudio = uiState.canPlayPronunciationAudio,
                modifier = modifier
            )
        }
    }
}

@Composable
fun ShowGenusDetail(
    isPreferencesCardExpanded: Boolean,
    visibleDialogState: State<DetailScreenDialogState>,
    canPlayPronunciationAudio: Boolean,
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

    val initiallySelectedColor = genus.getSelectedColorName()
    var selectedColor by remember { mutableStateOf(initiallySelectedColor) }
    val colorScheme by remember { derivedStateOf { ThemeConverter.getTheme(selectedColor) } }
    var preferencesControlsExpanded by remember { mutableStateOf(isPreferencesCardExpanded) }

    val currentDialogState by remember { visibleDialogState }
    val colorPickerDialogVisible = remember {
        derivedStateOf { currentDialogState == DetailScreenDialogState.ColorPickerDialog }
    }

    Crossfade(targetState = currentDialogState, label="crossfade_detail_dialog") {
        when (it) {
            DetailScreenDialogState.ColorPickerDialog -> {
                ColorPickerDialog(
                    isVisibleState = colorPickerDialogVisible,
                    initiallySelectedColor = selectedColor,
                    onColorPicked = { color ->
                        // Record the new colour in the UI
                        selectedColor = color
                    },
                    onClose = {
                        // Save the new colour and close the dialog
                        onEvent(DetailGenusUiEvent.SelectColor(selectedColor))
                        onEvent(DetailGenusUiEvent.ShowColorSelectDialog(false))
                    }
                )
            }
            DetailScreenDialogState.ImageView -> {
                EnlargedImageDialog(
                    genus,
                    onHide = {
                        onEvent(DetailGenusUiEvent.ShowColorSelectDialog(false))
                    }
                )
            }
            else -> {}
        }

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
                    onEvent(DetailGenusUiEvent.ShowLargeImageDialog(true))
                },
                innerPadding = innerPadding,
                canPlayPronunciation = canPlayPronunciationAudio,
                isFavourite = genus.isUserFavourite(),
                colorScheme = colorScheme ?: MaterialTheme.colorScheme,
                setColorPickerDialogVisibility = {
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

                val formations = genus.getFormations()
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


@Preview(widthDp = 400, heightDp = 2000, name = "Dark")
@Composable
fun PreviewGenusDetailDark() {
    val styraco = DebugGenusFactory.getGenus(DebugGenusFactory.GenusFactoryKey.Styracosaurus)

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

    val genusData = DetailedGenus(
        GenusWithImages(styraco, imageMap),
        LocalPrefs(_isFavourite = false),
        formations = listOf(
            Formation(
                "Alberta Mountain Formation",
                "alberta_mountain",
                continent = "North America",
                location = "Canada, US",
                countries = listOf("Canada", "US")
            ),
            Formation("Test formation")
        )
    )

    val uiState = DetailScreenUiState(
        genusName = styraco.getName(),
        genusData = DataState.Success(genusData),
        listOfColors = ThemeConverter.listOfColors,
        dialogState = DetailScreenDialogState.NoDialog,
        preferencesCardExpanded = true
    )
    val dialogState = remember { mutableStateOf(DetailScreenDialogState.NoDialog) }

    DinoDataTheme(darkTheme = true) {
        Surface (color = MaterialTheme.colorScheme.background) {
            GenusDetailScreenContent(
                uiState = uiState,
                dialogState = dialogState,
                onEvent = {}
            )
        }
    }
}
