package com.bp.dinodata.presentation.taxonomy_screen

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.DrawerValue
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
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bp.dinodata.R
import com.bp.dinodata.data.genus.GenusBuilder
import com.bp.dinodata.data.genus.IGenus
import com.bp.dinodata.data.taxon.ITaxon
import com.bp.dinodata.data.taxon.Taxon
import com.bp.dinodata.presentation.DataState
import com.bp.dinodata.presentation.list_genus.GenusListItem
import com.bp.dinodata.presentation.utils.LoadingItemsPlaceholder
import com.bp.dinodata.presentation.utils.NoDataPlaceholder
import com.bp.dinodata.theme.DinoDataTheme
import dagger.Lazy
import kotlin.math.exp
import kotlin.math.roundToInt

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
    initiallyExpanded: Boolean = (depth == 0),
    depthPadding: Dp = 20.dp,
    cardHeight: Dp = 54.dp,
    minCardWidth: Dp = 200.dp,
    branchAlpha: Float = 0.35f,
    onExpand: () -> Unit = {},
    onClose: () -> Unit = {},
    showDebugBranchLines: Boolean = false,
) {

    if (depth > 10) {
        Log.e("TaxonCard", "Depth limit reached with taxon ${taxon.getName()}")
        return
    }

    val hasChildren = remember { taxon.hasChildrenTaxa() }
    val numChildren = remember { taxon.getNumChildren() }
    val childrenTaxa = remember { taxon.getChildrenTaxa() }
    var isExpanded: Boolean by remember { mutableStateOf(
        initiallyExpanded && hasChildren
    ) }

    // Load the text to indicate the number of children
    val childText = pluralStringResource(
        R.plurals.num_taxon_children,
        numChildren,
        numChildren
    )

    val branchWidth = 8.dp
    val branchThickness = 2.dp
    val paddingBeforeFirstChild = 12.dp
    val paddingBetweenChildren = 8.dp

    val expandCard = {
        onExpand()
        isExpanded = true
    }
    val closeCard = {
        onClose()
        isExpanded = false
    }
    val toggleCardState = {
        if (isExpanded) {
            closeCard()
        }
        else {
            expandCard()
        }
    }

    var barHeight by remember { mutableStateOf(0.dp) }

    val defaultCardHeightPx = convertDpToPx(cardHeight)

    // Store the height of each child card
    var childHeightsPx by remember { mutableStateOf(
        Array(numChildren) { defaultCardHeightPx }
    ) }

    LaunchedEffect(null) {
        if (isExpanded) {
            onExpand()
        }
    }

    val density = LocalDensity.current

    val childHeightsDp by remember { derivedStateOf {
        childHeightsPx
            .map { with(density) { it.toDp() } }
            .mapIndexed { i, dp ->
                if (i == childHeightsPx.size-1) {
                    dp / 2
                }
                else {
                    dp
                }
            }
        }
    }

    val preChildrenPaddingColor =
        if (showDebugBranchLines) Color.Green
        else Color.White
    
    val betweenChildrenPaddingColor =
        if (showDebugBranchLines) Color.Red
        else Color.White


    val updateChildHeight = { childIndex: Int, newHeightPx: Float  ->
        if (childIndex in 0..numChildren-2) {
            childHeightsPx[childIndex] = newHeightPx
            childHeightsPx = childHeightsPx.copyOf()
        }
    }

    Column (
        modifier = modifier
    ) {
        if (depth == 0) {
            Text(
                "root",
                fontSize = 12.sp,
                modifier = Modifier.alpha(0.5f),
                fontStyle = FontStyle.Italic
            )
        }
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .heightIn(min = cardHeight)
                .widthIn(min = minCardWidth),
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
                    VerticalDivider(
                        Modifier.height(paddingBeforeFirstChild),
                        color = preChildrenPaddingColor,
                        thickness = branchThickness
                    )
                    childHeightsDp.forEachIndexed { i, height ->
                        VerticalDivider(
                            modifier = Modifier.height(height),
                            thickness = branchThickness,
                            color = Color.White
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
//                Column {
//                    VerticalDivider(
//                        thickness = branchThickness,
//                        color = MaterialTheme.colorScheme.onBackground,
//                        modifier = Modifier
//                            .height(barHeight)
//                            .alpha(branchAlpha)
//                    )
//                }
                Column(
                    verticalArrangement = Arrangement.spacedBy(paddingBetweenChildren),
                    modifier = Modifier
                        .padding(top = paddingBeforeFirstChild)
                        .padding(end = 2.dp)
                ) {
                    childrenTaxa.forEachIndexed { i, subtaxon ->
                        Row {
                            ChildBranchSymbol(
                                branchWidth = branchWidth,
                                thickness = branchThickness,
                                alpha = branchAlpha
                            )
                            if (subtaxon is IGenus) {
                                GenusListItem(
                                    subtaxon,
                                    height = cardHeight,
                                    showImage = false
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
                                    }
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaxonomyScreenContent(
    taxaState: DataState<List<ITaxon>>,
    showDebugBranchLines: Boolean = false,
    openNavDrawer: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.screen_title_taxonomy)) },
                navigationIcon = {
                    IconButton(onClick = openNavDrawer) {
                        Icon(Icons.AutoMirrored.Filled.List, "open nav drawer")
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
                Row (
                    modifier = Modifier
                        .scrollable(
                            rememberScrollableState {
                                val consumed = it / 2
                                it - consumed
                            },
                            orientation = Orientation.Horizontal
                        )
                        .fillMaxHeight()
                        .padding(pad)
                ) {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(
                            top=16.dp,
                            bottom=16.dp,
                            start=16.dp
                        )
                    ) {
                        items(taxaState.data) {
                            TaxonCard(
                                taxon = it,
                                depth = 0,
//                                minCardWidth = 1000.dp,
                                showDebugBranchLines = showDebugBranchLines
                            )
                            HorizontalDivider(
                                Modifier
                                    .height(2.dp)
                                    .padding(horizontal = 16.dp, vertical = 24.dp)
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
    openNavDrawer: () -> Unit
) {
    val taxaState by remember { viewModel.getTaxonomyList() }
    TaxonomyScreenContent(
        taxaState = taxaState,
        openNavDrawer = openNavDrawer,
//        showDebugBranchLines = true
    )
}



@Preview
@Composable
fun PreviewTaxonomyScreen() {

    val acro = GenusBuilder("Acrocanthosaurus")
        .setDiet("carnivore")
        .setCreatureType("carcharodontosaurid")
        .build()

    val carcharo = GenusBuilder("Carcharo")
        .setDiet("carnivore")
        .setCreatureType("carcharodontosaurid")
        .build()

    val veloci = GenusBuilder("Velociraptor")
        .setDiet("carnivore")
        .setCreatureType("dromaeosaurid")
        .build()

    val taxa = listOf(
        Taxon(
            "Dinosauria",
            listOf(
                Taxon("XBYZ"),
                Taxon(
                    "Theropoda",
                    listOf(
                        Taxon(
                            "Carcharodontosauridae",
                            listOf(acro, carcharo)
                        ),
                        Taxon(
                            "Dromaeosauridae",
                            listOf(veloci)
                        )
                    )
                ),
//                Taxon("Ornithischia")
            )
        ),
        Taxon(
            "Plesiosauria",
            listOf(
                Taxon("Anningasauroidea"),
//                Taxon("Anningasauroidea"),
//                Taxon("Anningasauroidea"),
//                Taxon("Anningasauroidea")
            )
        )
    )

    DinoDataTheme (darkTheme = true) {
        TaxonomyScreenContent(
            DataState.Success(taxa),
            showDebugBranchLines = true,
            openNavDrawer = {}
        )
    }
}