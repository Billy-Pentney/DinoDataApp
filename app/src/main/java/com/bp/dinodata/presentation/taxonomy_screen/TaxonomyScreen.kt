package com.bp.dinodata.presentation.taxonomy_screen

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloseFullscreen
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults.Container
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bp.dinodata.R
import com.bp.dinodata.data.genus.GenusBuilder
import com.bp.dinodata.data.genus.IGenus
import com.bp.dinodata.data.taxon.ITaxon
import com.bp.dinodata.data.taxon.ITaxonCollection
import com.bp.dinodata.data.taxon.TaxonCollectionBuilder
import com.bp.dinodata.presentation.DataState
import com.bp.dinodata.presentation.list_genus.GenusListItem
import com.bp.dinodata.presentation.list_genus.TextFieldState
import com.bp.dinodata.presentation.utils.LoadingItemsPlaceholder
import com.bp.dinodata.presentation.utils.NoDataPlaceholder
import com.bp.dinodata.theme.DinoDataTheme
import com.bp.dinodata.theme.HighlightYellow
import com.bp.dinodata.theme.MyGrey600
import kotlinx.coroutines.flow.MutableSharedFlow

@Composable
fun ChildBranchSymbol(
    modifier: Modifier = Modifier,
    thickness: Dp = 4.dp,
    height: Dp = 50.dp,
    branchWidth: Dp = 4.dp,
    color: Color = MaterialTheme.colorScheme.onBackground,
    alpha: Float = 1f,
) {
    HorizontalDivider(
        modifier = modifier
            .width(branchWidth)
            .padding(top = height / 2)
            .alpha(alpha),
        thickness = thickness,
        color = color
    )
}


data class ChildExpandedEvent(
    val childIndex: Int,
    val newSize: IntSize
)


@Composable
fun convertPxToDp(px: Float): Dp {
    return with (LocalDensity.current) { px.toDp() }
}

@Composable
fun convertDpToPx(dp: Dp): Float {
    return with (LocalDensity.current) { dp.toPx() }
}

object TaxonomyScreenUtils {
    const val MAX_TAXONOMY_DEPTH = 20
    val HIGHLIGHT_COLOR = HighlightYellow
}




@Composable
fun TaxonCard(
    taxon: ITaxon,
    expandedTaxaNames: Set<String>,
    highlightedTaxaNames: Set<String>,
    modifier: Modifier = Modifier,
    depth: Int = 0,
    updateExpansion: (ITaxon, Boolean) -> Unit,
    depthPadding: Dp = 20.dp,
    cardHeight: Dp = 54.dp,
    minCardWidth: Dp = 200.dp,
    branchAlpha: Float = 0.35f,
    showDebugBranchLines: Boolean = false,
    gotoGenus: (IGenus) -> Unit = {},
    onSizeChanged: (IntSize) -> Unit = {}
) {

    val hasChildren = remember { taxon.hasChildrenTaxa() }
    val numChildren = remember { taxon.getNumChildren() }
    val childrenTaxa = remember { taxon.getChildrenTaxa() }

    var canExpand by remember { mutableStateOf(hasChildren) }

    if (depth > TaxonomyScreenUtils.MAX_TAXONOMY_DEPTH) {
        Log.e("TaxonCard", "Depth limit reached with taxon ${taxon.getName()}")
        canExpand = false
    }

    var isExpanded: Boolean by rememberSaveable { mutableStateOf(
        taxon.getName().lowercase() in expandedTaxaNames
    )}
    var isHighlighted: Boolean by rememberSaveable { mutableStateOf(
        taxon.getName().lowercase() in highlightedTaxaNames
    ) }

    isExpanded = taxon.getName().lowercase() in expandedTaxaNames
    isHighlighted = taxon.getName().lowercase() in highlightedTaxaNames

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

    val defaultCardHeightPx = convertDpToPx(cardHeight)

    var cardSize by remember { mutableStateOf(IntSize(0, defaultCardHeightPx.toInt())) }

    // Store the height of each child card
    val childHeightsPx by rememberSaveable { mutableStateOf(Array(numChildren) { defaultCardHeightPx }) }

    val density = LocalDensity.current

    val calculateBranchHeight = {
        val lastChildTrimmed = childHeightsPx.lastOrNull()?.plus(1)?.div(2) ?: 0f
        val totalChildrenHeightPx = childHeightsPx.dropLast(1).sum() + lastChildTrimmed
        val totalChildrenDp = with(density) { totalChildrenHeightPx.toDp() }
        paddingBeforeFirstChild +
            totalChildrenDp +
            paddingBetweenChildren * (numChildren-1)
        
        // Calculate by subtracting the final child's height
//        val totalHeight = with(density) {
//            cardSize.height.toDp() }
//        val finalChildHeightDp = with(density) {
//            childHeightsPx.lastOrNull()?.toDp() ?: 0.dp
//        }
//        totalHeight - finalChildHeightDp - paddingAfterChildren + (cardHeight+1.dp)/2
    }

    val childUpdatedFlow: MutableSharedFlow<Int> = remember { MutableSharedFlow() }
    val coroutineScope = rememberCoroutineScope()

    var totalChildrenHeight by remember { mutableStateOf(0.dp) }

    val setCardExpansion = { newState: Boolean ->
        updateExpansion(taxon, newState)
        onSizeChanged(cardSize)
    }
    val toggleCardState = {
        // When the card changes state, we inform its parent of the new size
        setCardExpansion(!isExpanded)
    }

    // if debugging, the color given to the divider before the children
    val preChildrenPaddingColor =
        if (showDebugBranchLines) Color.Green
        else Color.White

    // If debugging, the color given to the divider in the padding between the children
    val betweenChildrenPaddingColor =
        if (showDebugBranchLines) Color.Red
        else Color.White

    val updateChildHeight = { childIndex: Int, newHeightPx: Float  ->
        if (childIndex in 0..<numChildren-1) {
            childHeightsPx[childIndex] = newHeightPx
        }
        totalChildrenHeight = calculateBranchHeight()
    }

    totalChildrenHeight = calculateBranchHeight()

    val surfaceColor = if (isExpanded) {
        MaterialTheme.colorScheme.surfaceVariant
    }
    else {
        MaterialTheme.colorScheme.surface
    }

    val borderStroke =
        if (isHighlighted)
            BorderStroke(2.dp, Color.White)
        else
            null

    val branchColor = MaterialTheme.colorScheme.onBackground

    Column (
        modifier = modifier
            .onSizeChanged { cardSize = it }
            .animateContentSize(
                animationSpec = tween(durationMillis = 100),
                finishedListener = { startSize, endSize ->
                    totalChildrenHeight = calculateBranchHeight()
                    onSizeChanged(endSize)
                }
            )
    ) {
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
            border = borderStroke,
            onClick = toggleCardState,
            modifier = Modifier
                .heightIn(min = cardHeight)
                .widthIn(min = minCardWidth)
                .width(IntrinsicSize.Min)
        ) {
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

                if (canExpand) {
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

        AnimatedVisibility(
            visible = isExpanded && canExpand,
            enter = fadeIn(tween(delayMillis = 150))
                    + expandVertically(tween(durationMillis = 250)) { 0 },
            exit = fadeOut(tween(durationMillis = 350))
                    + shrinkVertically(tween(delayMillis = 50, durationMillis = 300)) { 0 }
        ) {
            Row (modifier = Modifier.padding(start = 8.dp)) {
                Column (
                    modifier = Modifier.alpha(branchAlpha),
                    verticalArrangement = Arrangement.Top
                ) {
//                    if (showDebugBranchLines) {
//                        VerticalDivider(
//                            Modifier.height(paddingBeforeFirstChild),
//                            color = preChildrenPaddingColor,
//                            thickness = branchThickness
//                        )
//                        childHeightsPx.forEachIndexed { i, height ->
//                            val heightDp = convertPxToDp(px = height).let {
//                                if (i == numChildren-1) {
//                                    (it - 1.dp) / 2
//                                }
//                                else {
//                                    it
//                                }
//                            }
//                            VerticalDivider(
//                                modifier = Modifier.height(heightDp),
//                                thickness = branchThickness,
//                                color = branchColor
//                            )
//                            if (i < numChildren-1) {
//                                VerticalDivider(
//                                    Modifier.height(paddingBetweenChildren),
//                                    color = betweenChildrenPaddingColor,
//                                    thickness = branchThickness
//                                )
//                            }
//                        }
//                    }
//                    else {
                        VerticalDivider(
                            Modifier.animateContentSize().height(totalChildrenHeight),
                            color = branchColor,
                            thickness = branchThickness
                        )
                //                    }
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(paddingBetweenChildren),
                    modifier = Modifier.padding(top = paddingBeforeFirstChild)
                ) {
                    childrenTaxa.forEachIndexed { i, subtaxon ->
                        Row {
                            ChildBranchSymbol(
                                branchWidth = childBranchIndent,
                                thickness = branchThickness,
                                alpha = branchAlpha,
                                modifier = Modifier.animateContentSize(
                                    animationSpec = tween(delayMillis = 200)
                                )
                            )
                            if (subtaxon is IGenus) {
                                val isGenusHighlighted
                                    = (subtaxon.getName().lowercase() in highlightedTaxaNames)

                                GenusListItem(
                                    subtaxon,
                                    height = cardHeight,
                                    showImage = false,
                                    onClick = { gotoGenus(subtaxon) },
                                    modifier = Modifier
                                        .width(310.dp)
                                        .then(
                                            if (isGenusHighlighted) {
                                                Modifier.border(
                                                    2.dp,
                                                    TaxonomyScreenUtils.HIGHLIGHT_COLOR,
                                                    shape = RoundedCornerShape(8.dp)
                                                )
                                            } else {
                                                Modifier
                                            }
                                        )
                                )
                            } else {
                                TaxonCard(
                                    taxon = subtaxon,
                                    depthPadding = depthPadding,
                                    cardHeight = cardHeight,
                                    branchAlpha = branchAlpha,
                                    depth = depth+1,
                                    showDebugBranchLines = showDebugBranchLines,
                                    gotoGenus = gotoGenus,
                                    updateExpansion = { taxon, expanded ->
                                        totalChildrenHeight = calculateBranchHeight()
                                        updateExpansion(taxon, expanded)
                                    },
                                    onSizeChanged = {
                                        updateChildHeight(i, it.height.toFloat())
                                    },
                                    expandedTaxaNames = expandedTaxaNames,
                                    highlightedTaxaNames = highlightedTaxaNames
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

//    SideEffect {
//        totalChildrenHeight = calculateBranchHeight()
//        onSizeChanged(cardSize)
//    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaxonomyScreenContent(
    uiState: TaxonomyScreenUiState,
    taxaState: DataState<out ITaxonCollection>,
    showDebugBranchLines: Boolean = false,
    openNavDrawer: () -> Unit,
    gotoGenus: (IGenus) -> Unit,
    updateExpansion: (ITaxon, Boolean) -> Unit,
    closeAllTaxa: () -> Unit,
    updateSearchVisibility: () -> Unit,
    updateSearchBarContent: (String) -> Unit,
    submitSearch: () -> Unit
) {
    val searchBarVisible = uiState.isSearchBarVisible

    val textFieldColors = TextFieldDefaults.colors(
        unfocusedContainerColor = MaterialTheme.colorScheme.background,
        focusedContainerColor = MaterialTheme.colorScheme.background,
        focusedTextColor = MaterialTheme.colorScheme.onSurface,
        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
        disabledTextColor = MaterialTheme.colorScheme.onSurface,
        cursorColor = MaterialTheme.colorScheme.onSurface,
        focusedIndicatorColor = MaterialTheme.colorScheme.onSurface,
        unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface,
    )

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
                    IconButton(onClick = updateSearchVisibility) {
                        Crossfade(searchBarVisible, label="search_bar_button_crossfade") {
                            if (!it) {
                                Icon(Icons.Filled.Search, "show search")
                            }
                            else {
                                Icon(Icons.Filled.SearchOff, "hide search")
                            }
                        }
                    }
                    IconButton(onClick = closeAllTaxa) {
                        Icon(Icons.Filled.CloseFullscreen, "collapse all taxa")
                    }
                }
            )
        },
        contentWindowInsets = WindowInsets(top=80.dp)
    ) {
        pad ->

        Column (
            modifier = Modifier.padding(pad)
        ) {
            AnimatedVisibility(
                visible = searchBarVisible,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                TaxonomyScreenSearchBar(
                    text = uiState.searchBoxTextState.getTextContent(),
                    updateSearchBarContent = updateSearchBarContent,
                    submitSearch = submitSearch
                )
            }

            when (taxaState) {
                is DataState.Failed -> {
                    NoDataPlaceholder(Modifier)
                }

                is DataState.Idle -> {
                    Text("Idle", color = MaterialTheme.colorScheme.onBackground)
                }

                is DataState.LoadInProgress -> LoadingItemsPlaceholder()
                is DataState.Success -> {
                    val collection = taxaState.data

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                    ) {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(
                                top = 16.dp,
                                bottom = 16.dp,
                            ),
                            modifier = Modifier
                                .width(700.dp)
                                .fillMaxWidth()
                        ) {
//                            item {
//                                Text(
//                                    collection.getRoots().size.toString(),
//                                    color = MaterialTheme.colorScheme.onBackground
//                                )
//                            }
                            items(collection.getRoots()) {
                                TaxonCard(
                                    taxon = it,
                                    showDebugBranchLines = showDebugBranchLines,
                                    gotoGenus = gotoGenus,
                                    updateExpansion = updateExpansion,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp),
                                    expandedTaxaNames = collection.getAllExpandedNames(),
                                    highlightedTaxaNames = collection.getAllHighlightedNames()
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
                            NoDataPlaceholder(modifier=Modifier.fillMaxWidth())
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaxonomyScreenSearchBar(
    text: String,
    updateSearchBarContent: (String) -> Unit,
    submitSearch: () -> Unit
) {
    val clearSearchQuery = {
        updateSearchBarContent("")
    }

    val textStyle = TextStyle(
        fontSize = 18.sp,
        color = MaterialTheme.colorScheme.onSurface
    )

    val textFieldColors = TextFieldDefaults.colors(
        unfocusedContainerColor = MaterialTheme.colorScheme.background,
        focusedContainerColor = MaterialTheme.colorScheme.background,
        focusedTextColor = MaterialTheme.colorScheme.onSurface,
        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
        disabledTextColor = MaterialTheme.colorScheme.onSurface,
        cursorColor = MaterialTheme.colorScheme.onSurface,
        selectionColors = TextSelectionColors(
            handleColor = MaterialTheme.colorScheme.onSurface,
            backgroundColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        focusedIndicatorColor = MaterialTheme.colorScheme.onSurface,
        unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface,
    )

    val interactionSource = remember { MutableInteractionSource() }

    SearchBar(
        inputField = {
            BasicTextField(
                value = text,
                onValueChange = updateSearchBarContent,
                textStyle = textStyle,
                maxLines = 1,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(onSearch = {
                    submitSearch()
                }),
                modifier = Modifier.padding(8.dp),
                decorationBox = { innerTextField ->
                    TextFieldDefaults.DecorationBox(
                        value = text,
                        innerTextField = {
                            innerTextField()
                        },
                        colors = textFieldColors,
                        leadingIcon = {
                            Icon(
                                Icons.Outlined.Search,
                                "search",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .padding(start = 8.dp)
                            )
                        },
                        trailingIcon = {
                            if (text.isNotEmpty()) {
                                IconButton(
                                    onClick = { clearSearchQuery() },
                                    modifier = Modifier.padding(end=8.dp)
                                ) {
                                    Icon(
                                        Icons.Filled.Close,
                                        "clear search text",
                                        tint = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        },
                        enabled = true,
                        singleLine = true,
                        visualTransformation = VisualTransformation.None,
                        interactionSource = interactionSource,
                        container = {
                            Container(
                                enabled = true,
                                isError = false,
                                colors = textFieldColors,
                                shape = RoundedCornerShape(24.dp),
                                interactionSource = interactionSource
                            )
                        }
                    )
                }
            )
        },
        expanded = false,
        onExpandedChange = {},
        shape = SearchBarDefaults.inputFieldShape,
        colors = SearchBarDefaults.colors(
            containerColor = MyGrey600
        ),
        tonalElevation = SearchBarDefaults.TonalElevation,
        shadowElevation = SearchBarDefaults.ShadowElevation,
        windowInsets = SearchBarDefaults.windowInsets
    ) {

    }
}


@Composable
fun TaxonomyScreen(
    viewModel: ITaxonomyScreenViewModel,
    openNavDrawer: () -> Unit,
    gotoGenusByName: (String) -> Unit
) {
    val taxaState by remember { viewModel.getTaxonomyList() }
    val uiState by remember { viewModel.getUiState() }

    val toastFlow = remember { viewModel.getToastFlow() }

    val context = LocalContext.current

    LaunchedEffect(null) {
        toastFlow.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    TaxonomyScreenContent(
        uiState = uiState,
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
        },
        showDebugBranchLines = false,
        updateSearchVisibility = {
            viewModel.onEvent(
                TaxonomyScreenUiEvent.ToggleSearchBarVisibility
            )
        },
        updateSearchBarContent = {
            viewModel.onEvent(
                TaxonomyScreenUiEvent.UpdateSearchBoxText(it)
            )
        },
        submitSearch = {
            viewModel.onEvent(
                TaxonomyScreenUiEvent.SubmitSearch
            )
        }
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
    taxaCollection.markAsHighlighted(
        "Carcharo"
    )

    val uiState = TaxonomyScreenUiState(
        isSearchBarVisible = true,
        searchBoxTextState = TextFieldState(
            text = "diplodocidae"
        )
    )

    DinoDataTheme (darkTheme = true) {
        TaxonomyScreenContent(
            uiState = uiState,
            taxaState = DataState.Success(taxaCollection),
            showDebugBranchLines = false,
            openNavDrawer = {},
            gotoGenus = {},
            updateExpansion = { _,_ -> },
            closeAllTaxa = {},
            updateSearchVisibility = {},
            updateSearchBarContent = {},
            submitSearch = {}
        )
    }
}