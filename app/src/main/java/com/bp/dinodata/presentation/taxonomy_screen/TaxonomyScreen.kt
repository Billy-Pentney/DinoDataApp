package com.bp.dinodata.presentation.taxonomy_screen

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.CloseFullscreen
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bp.dinodata.R
import com.bp.dinodata.data.genus.GenusBuilder
import com.bp.dinodata.data.genus.IGenus
import com.bp.dinodata.data.taxon.IDisplayableTaxon
import com.bp.dinodata.data.taxon.ITaxon
import com.bp.dinodata.data.taxon.ITaxonCollection
import com.bp.dinodata.data.taxon.TaxonCollectionBuilder
import com.bp.dinodata.presentation.DataState
import com.bp.dinodata.presentation.list_genus.GenusListItem
import com.bp.dinodata.presentation.utils.LoadingItemsPlaceholder
import com.bp.dinodata.presentation.utils.NoDataPlaceholder
import com.bp.dinodata.presentation.utils.ZoomableBox
import com.bp.dinodata.theme.DinoDataTheme

@Composable
fun ChildBranchSymbol(
    thickness: Dp = 4.dp,
    height: Dp = 50.dp,
    branchWidth: Dp = 4.dp,
    color: Color = MaterialTheme.colorScheme.onBackground,
    alpha: Float = 1f
) {
    HorizontalDivider(
        modifier = Modifier
            .width(branchWidth)
            .padding(top = height / 2)
            .alpha(alpha),
        thickness = thickness,
        color = color
    )
}


@Composable
fun convertPxToDp(px: Float): Dp {
    return with (LocalDensity.current) { px.toDp() }
}

@Composable
fun convertDpToPx(dp: Dp): Float {
    return with (LocalDensity.current) { dp.toPx() }
}


@Composable
fun TaxonCard(
    taxon: ITaxon,
    modifier: Modifier = Modifier,
    depth: Int = 0,
    isTaxonExpanded: (ITaxon) -> Boolean,
    updateExpansion: (ITaxon, Boolean) -> Unit,
//    initiallyExpanded: Boolean = (depth == 0),
    depthPadding: Dp = 20.dp,
    cardHeight: Dp = 54.dp,
    minCardWidth: Dp = 200.dp,
    branchAlpha: Float = 0.35f,
    showDebugBranchLines: Boolean = false,
    gotoGenus: (IGenus) -> Unit = {},
) {

    if (depth > 10) {
        Log.e("TaxonCard", "Depth limit reached with taxon ${taxon.getName()}")
        return
    }

    val hasChildren = remember { taxon.hasChildrenTaxa() }
    val numChildren = remember { taxon.getNumChildren() }
    val childrenTaxa = remember { taxon.getChildrenTaxa() }

    var isExpanded: Boolean by rememberSaveable { mutableStateOf(
        isTaxonExpanded(taxon)
    ) }

    isExpanded = isTaxonExpanded(taxon)

    // Load the text to indicate the number of children
    val childText = pluralStringResource(
        R.plurals.num_taxon_children,
        numChildren,
        numChildren
    )

    val childBranchIndent = 8.dp
    val branchThickness = 2.dp
    
    val paddingBetweenChildren = 8.dp
    val paddingBeforeFirstChild = paddingBetweenChildren
    val paddingAfterChildren = 20.dp

    val expandCard = {
        isExpanded = true
        updateExpansion(taxon, true)
    }
    val closeCard = {
        isExpanded = false
        updateExpansion(taxon, false)
    }
    val toggleCardState = {
        if (isExpanded) {
            closeCard()
        }
        else {
            expandCard()
        }
    }

    val defaultCardHeightPx = convertDpToPx(cardHeight)

    // Store the height of each child card
    val childHeightsPx by remember { mutableStateOf(
        Array(numChildren) { defaultCardHeightPx }
    ) }

    val density = LocalDensity.current
    
    var totalChildrenHeight by remember { mutableStateOf(0.dp) }

    // if debugging, the color given to the divider before the children
    val preChildrenPaddingColor =
        if (showDebugBranchLines) Color.Green
        else Color.White

    // If debugging, the color given to the divider in the padding between the children
    val betweenChildrenPaddingColor =
        if (showDebugBranchLines) Color.Red
        else Color.White


    val recalculateBranchHeight = {
        // The last child branch finishes at halfway through the card
        val lastChildTrimmed = childHeightsPx
            .lastOrNull()?.plus(1)?.div(2) ?: 0f

        val totalChildrenPx = childHeightsPx.dropLast(1).sum() + lastChildTrimmed
        val totalChildrenDp = with(density) { totalChildrenPx.toDp() }

        totalChildrenHeight =
            paddingBeforeFirstChild +
            totalChildrenDp +
            paddingBetweenChildren * (numChildren-1)
    }

    LaunchedEffect(null) {
        recalculateBranchHeight()
    }

    val updateChildHeight = { childIndex: Int, newHeightPx: Float  ->
        if (childIndex in 0..numChildren-2) {
            val oldHeightPx = childHeightsPx[childIndex]
            childHeightsPx[childIndex] = newHeightPx
            val heightDiffDp = with(density) {
                (newHeightPx - oldHeightPx).toDp()
            }
            totalChildrenHeight += heightDiffDp
        }
    }

    val surfaceColor = if (isExpanded) {
        MaterialTheme.colorScheme.surfaceVariant
    }
    else {
        MaterialTheme.colorScheme.surface
    }

    val branchColor = MaterialTheme.colorScheme.onBackground

    Column (modifier = modifier) {
        if (depth == 0) {
            Text(
                stringResource(R.string.label_taxon_root),
                fontSize = 12.sp,
                modifier = Modifier.alpha(0.7f),
                fontStyle = FontStyle.Italic,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        Surface(
            color = surfaceColor,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .heightIn(min = cardHeight)
                .widthIn(min = minCardWidth)
                .width(IntrinsicSize.Min),
            onClick = toggleCardState
        ) {
//
//        Card(
//            modifier = Modifier.fillMaxWidth()
//        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(2.dp)
            ) {
                Column (
                    modifier = Modifier
                        .padding(all = 8.dp)
                        .padding(start = 4.dp, end = 8.dp)
                ) {
                    Text(
                        taxon.getName(),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                    )
                    if (hasChildren) {
                        Text(
                            childText,
                            fontSize = 12.sp,
                            lineHeight = 12.sp,
                            modifier = Modifier
                                .alpha(0.75f)
                                .height(IntrinsicSize.Min)
                        )
                    }
                }

                if (hasChildren) {
                    Spacer(Modifier.weight(1f))
                    IconButton(onClick = toggleCardState) {
                        Crossfade(isExpanded, label="expand_card_crossfade") {
                            if (!it) {
                                Icon(
                                    Icons.Filled.ArrowDropDown,
                                    "expand taxon"
                                )
                            } else {
                                Icon(
                                    Icons.Filled.ArrowDropUp,
                                    "hide taxon"
                                )
                            }
                        }
                    }
                }
            }
        }

        Log.d("TaxonCard", "Recomposed for ${taxon.getName()}")

        AnimatedVisibility(visible = isExpanded) {
            Row (modifier = Modifier.padding(start = 8.dp)) {
                Column (modifier = Modifier.alpha(branchAlpha)) {
                    if (showDebugBranchLines) {
                        VerticalDivider(
                            Modifier.height(paddingBeforeFirstChild),
                            color = preChildrenPaddingColor,
                            thickness = branchThickness
                        )
                        childHeightsPx.forEachIndexed { i, height ->
                            val heightDp = convertPxToDp(px = height)
                            VerticalDivider(
                                modifier = Modifier.height(heightDp),
                                thickness = branchThickness,
                                color = branchColor
                            )
                            if (i < numChildren-1) {
                                VerticalDivider(
                                    Modifier.height(paddingBetweenChildren),
                                    color = betweenChildrenPaddingColor,
                                    thickness = branchThickness
                                )
                            }
                        }
                    }
                    else {
                        VerticalDivider(
                            Modifier.height(totalChildrenHeight),
                            color = branchColor,
                            thickness = branchThickness
                        )
                    }
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(paddingBetweenChildren),
                    modifier = Modifier
                        .padding(top = paddingBeforeFirstChild)
                ) {
                    childrenTaxa.forEachIndexed { i, subtaxon ->
                        Row {
                            ChildBranchSymbol(
                                branchWidth = childBranchIndent,
                                thickness = branchThickness,
                                alpha = branchAlpha
                            )
                            if (subtaxon is IGenus) {
                                GenusListItem(
                                    subtaxon,
                                    height = cardHeight,
                                    showImage = false,
                                    onClick = { gotoGenus(subtaxon) },
                                    modifier = Modifier.width(270.dp)
                                )
                            } else {
                                TaxonCard(
                                    taxon = subtaxon,
                                    depthPadding = depthPadding,
                                    cardHeight = cardHeight,
                                    branchAlpha = branchAlpha,
                                    depth = depth+1,
                                    showDebugBranchLines = showDebugBranchLines,
                                    modifier = Modifier.onSizeChanged {
                                        Log.d("TaxonCard", "Update child $i height")
                                        updateChildHeight(i, it.height.toFloat())
                                    },
                                    gotoGenus = gotoGenus,
                                    updateExpansion = updateExpansion,
                                    isTaxonExpanded = isTaxonExpanded
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(paddingAfterChildren))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaxonomyScreenContent(
    taxaState: DataState<out ITaxonCollection>,
    showDebugBranchLines: Boolean = false,
    openNavDrawer: () -> Unit,
    gotoGenus: (IGenus) -> Unit,
    updateExpansion: (ITaxon, Boolean) -> Unit,
    closeAllTaxa: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.screen_title_taxonomy)) },
                navigationIcon = {
                    IconButton(onClick = openNavDrawer) {
                        Icon(Icons.AutoMirrored.Filled.List, "open nav drawer")
                    }
                },
                actions = {
                    IconButton(onClick = closeAllTaxa) {
                        Icon(Icons.Filled.CloseFullscreen, "close all taxa")
                    }
                }
            )
        },
        contentWindowInsets = WindowInsets(top=80.dp)
    ) {
        pad ->
        when (taxaState) {
            is DataState.Failed -> {
                NoDataPlaceholder()
            }

            is DataState.Idle -> {
                Text("Idle", color = MaterialTheme.colorScheme.onBackground)
            }

            is DataState.LoadInProgress -> LoadingItemsPlaceholder()
            is DataState.Success -> {
                val collection = taxaState.data
                // Callback to check if the taxon is currently expanded
                val isTaxonExpanded =  { taxon: ITaxon -> collection.isExpanded(taxon) }

                Box (
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(pad)
                        .horizontalScroll(rememberScrollState())
                ) {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(
                            top=16.dp,
                            bottom=16.dp,
                        ),
                        modifier = Modifier.width(700.dp)
                    ) {
                        items(collection.getRoots()) {
                            TaxonCard(
                                taxon = it,
                                showDebugBranchLines = showDebugBranchLines,
                                gotoGenus = gotoGenus,
                                updateExpansion = updateExpansion,
                                isTaxonExpanded = isTaxonExpanded,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                            )
                            HorizontalDivider(
                                Modifier
                                    .height(2.dp)
                                    .padding(vertical = 24.dp)
                            )
                            Spacer(Modifier.height(25.dp))
                        }
                        item {
                            Spacer(Modifier.height(50.dp))
                        }
                    }
                    if (taxaState.data.isEmpty()) {
                        NoDataPlaceholder()
                    }
                }
            }
        }
    }
}



@Composable
fun TaxonomyScreen(
    viewModel: ITaxonomyScreenViewModel,
    openNavDrawer: () -> Unit,
    gotoGenusByName: (String) -> Unit
) {
    val taxaState by remember { viewModel.getTaxonomyList() }

    val toastFlow = remember { viewModel.getToastFlow() }

    val context = LocalContext.current

    LaunchedEffect(null) {
        toastFlow.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    TaxonomyScreenContent(
        taxaState = taxaState,
        openNavDrawer = openNavDrawer,
        gotoGenus = { genus ->
            val genusName = genus.getName()
            Log.d("TaxonomyScreen", "Attempt to navigate to genus $genusName")
            gotoGenusByName(genusName)
        },
        updateExpansion = { taxon, expanded ->
            viewModel.onEvent(
                TaxonomyScreenUiEvent.UpdateTaxonExpansion(taxon, expanded)
            )
        },
        closeAllTaxa = {
            viewModel.onEvent(
                TaxonomyScreenUiEvent.CloseAllExpandedTaxa
            )
        }
//        showDebugBranchLines = true
    )
}



@Preview
@Composable
fun PreviewTaxonomyScreen() {

    val acro = GenusBuilder("Acrocanthosaurus")
        .setDiet("carnivore")
        .setCreatureType("carcharodontosaurid")
        .setTaxonomy(listOf("Dinosauria", "Carcharodontosauridae"))
        .build()

    val carcharo = GenusBuilder("Carcharo")
        .setDiet("carnivore")
        .setCreatureType("carcharodontosaurid")
        .setTaxonomy(listOf("Dinosauria", "Carcharodontosauridae"))
        .build()

    val veloci = GenusBuilder("Velociraptor")
        .setDiet("carnivore")
        .setCreatureType("dromaeosaurid")
        .setTaxonomy(listOf("Dinosauria", "Dromaeosauridae"))
        .build()

    val taxaParents = mapOf(
        "XBYZ" to "Dinosauria",
        "Theropoda" to "Dinosauria",
        "Carcharodontosauridae" to "Theropoda",
        "Dromaeosauridae" to "Theropoda",
        "Anningasauroidea" to "Plesiosauria"
    )

    val taxaCollection = TaxonCollectionBuilder(taxaParents)
        .addGenera(listOf(acro, carcharo, veloci))
        .build()
    
    taxaCollection.markAsExpanded(
        "Dinosauria", "Theropoda", "Plesiosauria", "Carcharodontosauridae"
    )

    DinoDataTheme (darkTheme = true) {
        TaxonomyScreenContent(
            DataState.Success(taxaCollection),
            showDebugBranchLines = false,
            openNavDrawer = {},
            gotoGenus = {},
            updateExpansion = { _,_ -> },
            closeAllTaxa = {}
        )
    }
}