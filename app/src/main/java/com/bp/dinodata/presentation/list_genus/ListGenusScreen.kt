package com.bp.dinodata.presentation.list_genus

import android.annotation.SuppressLint
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bp.dinodata.R
import com.bp.dinodata.data.Diet
import com.bp.dinodata.data.search.GenusSearch
import com.bp.dinodata.data.genus.GenusBuilderImpl
import com.bp.dinodata.data.genus.IGenus
import com.bp.dinodata.data.IResultsByLetter
import com.bp.dinodata.data.ResultsByLetter
import com.bp.dinodata.data.genus.LocalPrefs
import com.bp.dinodata.data.genus.GenusWithPrefs
import com.bp.dinodata.data.genus.IGenusWithPrefs
import com.bp.dinodata.presentation.LoadState
import com.bp.dinodata.presentation.utils.DividerTextRow
import com.bp.dinodata.presentation.utils.NoDataPlaceholder
import com.bp.dinodata.theme.DinoDataTheme
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListGenusScreenContent(
    uiState: ListGenusUiState,
    navigateToGenus: (String) -> Unit = {},
    spacing: Dp = 8.dp,
    outerPadding: Dp = 12.dp,
    switchToPageByIndex: (Int) -> Unit = {},
    updateSearchQuery: (TextFieldValue) -> Unit,
    clearSearchQuery: () -> Unit,
    toggleSearchBarVisibility: (Boolean) -> Unit,
    prefillSearchSuggestion: () -> Unit
) {
    val loadState = uiState.loadState
    val searchBarVisible = uiState.searchBarVisible

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.title_creature_list),
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    Icon(
                        painterResource(id = R.mipmap.ic_launcher_v1_foreground),
                        contentDescription = "app_logo",
                        modifier = Modifier
                            .padding(bottom = 4.dp)
                            .alpha(0.6f)
                    )
                },
                actions = {
                    IconButton(
                        onClick = {
                            toggleSearchBarVisibility(!searchBarVisible)
                        },
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Crossfade(searchBarVisible, label="searchBarIcon") {
                            if (!it) {
                                Icon(
                                    Icons.Filled.Search,
                                    contentDescription = stringResource(R.string.desc_show_search),
                                )
                            }
                            else {
                                Icon(
                                    Icons.Filled.SearchOff,
                                    contentDescription = stringResource(R.string.desc_close_search)
                                )
                            }
                        }
                    }
                }
            )
        }
    ) { pad ->

        Column (
            modifier = Modifier.padding(pad)
        ) {
            when (loadState) {
                LoadState.Loaded -> {
                    ShowHorizontalPagerOfGeneraByLetter(
                        uiState = uiState,
                        spacing = spacing,
                        outerPadding = outerPadding,
                        navigateToGenus = navigateToGenus,
                        switchToPageByIndex = switchToPageByIndex,
                        toggleSearchBarVisibility = toggleSearchBarVisibility,
                        clearSearchQuery = clearSearchQuery,
                        updateSearchQuery = updateSearchQuery,
                        prefillSearchSuggestion = prefillSearchSuggestion
                    )
                }

                LoadState.InProgress -> {
                    // Loading items Placeholder
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .alpha(0.5f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically)
                    ) {
                        CircularProgressIndicator(color = White)
                        Text(stringResource(R.string.info_loading_placeholder))
                    }
                }

                is LoadState.Error -> {
                    Text("Sorry! An error occurred. Reason: ${loadState.reason}")
                }

                else -> {}
            }
        }
    }
}

@Composable
fun ShowHorizontalPagerOfGeneraByLetter(
    uiState: ListGenusUiState,
    spacing: Dp,
    outerPadding: Dp,
    navigateToGenus: (String) -> Unit,
    switchToPageByIndex: (Int) -> Unit,
    toggleSearchBarVisibility: (Boolean) -> Unit,
    clearSearchQuery: () -> Unit,
    updateSearchQuery: (TextFieldValue) -> Unit,
    prefillSearchSuggestion: () -> Unit
) {
    Column (
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Crossfade(
            targetState = uiState.searchBarVisible,
            label="search_or_page_select",
            modifier = Modifier.padding(top=outerPadding)
        ) {
            if (it) {
                SearchPage(
                    uiState,
                    toggleSearchBarVisibility,
                    updateSearchQuery,
                    clearSearchQuery,
                    navigateToGenus,
                    outerPadding,
                    prefillSearchSuggestion
                )
            }
            else {
                HorizontalPagerOfGenera(
                    uiState,
                    switchToPageByIndex,
                    outerPadding,
                    spacing,
                    navigateToGenus
                )
            }
        }
    }
}

@Composable
fun SearchPage(
    uiState: ListGenusUiState,
    toggleSearchBarVisibility: (Boolean) -> Unit,
    updateSearchQuery: (TextFieldValue) -> Unit,
    clearSearchQuery: () -> Unit,
    navigateToGenus: (String) -> Unit,
    outerPadding: Dp,
    prefillSearchSuggestion: () -> Unit
) {
    val results = uiState.searchResults ?: emptyList()

    Column (verticalArrangement = Arrangement.spacedBy(8.dp)) {
        ListGenusSearchBar(
            toggleVisibility = toggleSearchBarVisibility,
            updateSearchQuery = updateSearchQuery,
            clearSearchQuery = clearSearchQuery,
            modifier = Modifier.padding(horizontal = outerPadding),
            searchSuggestions = uiState.search.getSuggestedSuffixes(),
            prefillSearchSuggestion = prefillSearchSuggestion,
            uiState = uiState
        )
        DividerTextRow(
            text = stringResource(R.string.text_showing_X_creatures, results.size),
            modifier = Modifier.padding(vertical = 8.dp, horizontal=outerPadding),
            dividerPadding = PaddingValues(horizontal = 8.dp)
        )
        LazyListOfGenera(
            results,
            contentPadding = PaddingValues(start = outerPadding, end = outerPadding, bottom=outerPadding),
            navigateToGenus = navigateToGenus,
            modifier = Modifier.padding(top=outerPadding),
            showCreatureCountAtBottom = false
        )
    }
}

@Composable
fun HorizontalPagerOfGenera(
    uiState: ListGenusUiState,
    switchToPageByIndex: (Int) -> Unit,
    outerPadding: Dp,
    spacing: Dp,
    navigateToGenus: (String) -> Unit
) {

    val keys = uiState.keys
    val selectedPageIndex = uiState.selectedPageIndex

    if (keys.isEmpty()) {
        NoDataPlaceholder()
        return
    }

    val keyIndices = remember { keys.indices.toList() }

    val coroutineScope = rememberCoroutineScope()

    val horizontalPagerState = rememberPagerState { keys.size }

    LaunchedEffect(horizontalPagerState) {
        // Collect from the a snapshotFlow reading the currentPage
        snapshotFlow { horizontalPagerState.currentPage }.collect { page ->
            // Do something with each page change, for example:
            // viewModel.sendPageSelectedEvent(page)
            Log.d("ListGenusScreenPager", "Page changed to $page")
            switchToPageByIndex(page)
        }
    }

    LaunchedEffect(selectedPageIndex) {
        if (selectedPageIndex != horizontalPagerState.currentPage) {
            coroutineScope.launch {
                horizontalPagerState.scrollToPage(selectedPageIndex)
            }
        }
    }

    // Monitoring nested scroll within the page
    var persistScrollY by remember { mutableFloatStateOf(0f) }
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
                val oldState = persistScrollY
                val newState = (persistScrollY + available.y).coerceIn(-100f, 100f)
                persistScrollY = newState
                return Offset(x = 0f, y = newState - oldState)
            }
        }
    }

    Column {
        val rowScrollState = rememberLazyListState()

        // Show the keys as a scrollable row.
        // Each letter can be tapped to jump to that page specifically.
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = outerPadding),
            horizontalArrangement = Arrangement.spacedBy(
                4.dp, Alignment.CenterHorizontally
            ),
            verticalAlignment = Alignment.CenterVertically,
            state = rowScrollState
        ) {
            items(keyIndices) { index ->
                val key = keys[index].toString()
                val isSelected = (selectedPageIndex == index)
                val color =
                    if (isSelected) MaterialTheme.colorScheme.surface
                    else MaterialTheme.colorScheme.background

                val textColor = 
                    if (isSelected) MaterialTheme.colorScheme.onSurface
                    else MaterialTheme.colorScheme.onBackground

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .height(IntrinsicSize.Min)
                        .aspectRatio(if (isSelected) 0.8f else 0.5f)
                        .clickable(onClick = { switchToPageByIndex(index) })
                        .clip(RoundedCornerShape(8.dp))
                        .background(color = color)
                ) {
                    Text(
                        key,
                        fontSize = if (isSelected) 24.sp else 16.sp,
                        color = textColor,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(vertical = 10.dp)
                            .alpha(if (isSelected) 1f else 0.75f),
                        textAlign = TextAlign.Center,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
        // Horizontal Scroll Indicator
        if (keys.size > 3) {
            val startPosition = selectedPageIndex.toFloat() / keys.size
            val endPosition = selectedPageIndex.toFloat() / keys.size
            Row (
                modifier= Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 8.dp),
                verticalAlignment = Alignment.Top
            ) {
                if (startPosition > 0){
                    Spacer(Modifier.weight(startPosition))
                }
                Box(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .animateContentSize()
                        .alpha(0.3f)
                        .padding(horizontal = outerPadding)
                        .background(MaterialTheme.colorScheme.onBackground)
                        .clip(RoundedCornerShape(4.dp))
                        .weight(0.2f)
                        .height(2.dp)
                )
                if (endPosition < 1){
                    Spacer(Modifier.weight(1-endPosition))
                }
            }
        }

        HorizontalPager(
            state = horizontalPagerState,
            contentPadding = PaddingValues(
                top = outerPadding,
                start = outerPadding,
                end = outerPadding
            ),
            pageSpacing = outerPadding,
            verticalAlignment = Alignment.Top,
            pageNestedScrollConnection = nestedScrollConnection,
            modifier = Modifier.fillMaxHeight()
        ) { pageNum ->
            val generaList = uiState.getPageByIndex(pageNum) ?: emptyList()
            ListOfGenera(
                generaList,
                verticalSpacing = spacing,
                outerPadding = outerPadding,
                navigateToGenus = navigateToGenus,
                modifier = Modifier.nestedScroll(nestedScrollConnection)
            )
        }
    }
}

@Composable
fun ListOfGenera(
    generaList: List<IGenus>?,
    outerPadding: Dp,
    navigateToGenus: (String) -> Unit,
    modifier: Modifier = Modifier,
    verticalSpacing: Dp = 8.dp,
    scrollState: ScrollState = rememberScrollState()
) {
    val numElements = generaList?.size ?: 0
    Column(
        verticalArrangement = Arrangement.spacedBy(verticalSpacing),
        modifier = modifier
            .verticalScroll(scrollState)
            .fillMaxWidth()
    ) {
        generaList?.forEach { genus ->
            GenusListItem(
                genus = genus,
                onClick = { navigateToGenus(genus.getName()) }
            )
        }
        DividerTextRow(
            text = stringResource(R.string.text_showing_X_creatures, numElements),
            modifier = Modifier
                .padding(horizontal = 4.dp + outerPadding)
                .padding(top = 8.dp),
            dividerPadding = PaddingValues(horizontal = 8.dp)
        )
        Spacer(Modifier.height(40.dp))
    }
}

@Composable
fun LazyListOfGenera(
    generaList: List<IGenus>?,
    navigateToGenus: (String) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    verticalSpacing: Dp = 8.dp,
    scrollState: LazyListState = rememberLazyListState(),
    showCreatureCountAtBottom: Boolean = true
) {
    val numElements = generaList?.size ?: 0
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(verticalSpacing),
        state = scrollState,
        contentPadding = contentPadding,
        modifier = modifier.fillMaxWidth()
    ) {
        items(generaList ?: emptyList()) { genus ->
            GenusListItem(
                genus = genus,
                onClick = { navigateToGenus(genus.getName()) }
            )
        }
        if (showCreatureCountAtBottom) {
            item {
                DividerTextRow(
                    text = stringResource(R.string.text_showing_X_creatures, numElements),
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .padding(bottom = 40.dp),
                    dividerPadding = PaddingValues(horizontal = 8.dp)
                )
            }
        }
    }
}


@Composable
fun ListGenusScreen(
    listGenusViewModel: ListGenusViewModel,
    navigateToGenus: (String) -> Unit,
) {
    val uiState by remember { listGenusViewModel.getUiState() }
    val searchQuery = remember { derivedStateOf { uiState.search.getQuery() } }
    val searchBarVisibility = remember { derivedStateOf { uiState.searchBarVisible } }

    BackHandler (searchBarVisibility.value) {
        if (searchQuery.value.isNotEmpty()) {
            listGenusViewModel.onEvent(ListGenusPageUiEvent.ClearSearchQueryOrHideBar)
        }
        else {
            // Close the Search Bar when navigating up
            listGenusViewModel.onEvent(ListGenusPageUiEvent.ToggleSearchBar(false))
        }
    }

    ListGenusScreenContent(
        uiState = uiState,
        navigateToGenus = navigateToGenus,
        switchToPageByIndex = { index ->
            listGenusViewModel.onEvent(ListGenusPageUiEvent.SwitchToPage(index))
        },
        updateSearchQuery = {
            listGenusViewModel.onEvent(ListGenusPageUiEvent.UpdateSearchQuery(it))
        },
        clearSearchQuery = {
            listGenusViewModel.onEvent(ListGenusPageUiEvent.ClearSearchQueryOrHideBar)
        },
        toggleSearchBarVisibility = {
            listGenusViewModel.onEvent(ListGenusPageUiEvent.ToggleSearchBar(visible=it))
        },
        prefillSearchSuggestion = {
            listGenusViewModel.onEvent(ListGenusPageUiEvent.AcceptSearchSuggestion)
        }
    )
}

@SuppressLint("UnrememberedMutableState")
@Preview(widthDp=400)
@Composable
fun PreviewListGenus() {
    val acro = GenusBuilderImpl("Acrocanthosaurus")
        .setDiet("Carnivorous")
        .setTimePeriod("Late Cretaceous")
        .setCreatureType("large theropod")
        .build()
    val trike = GenusBuilderImpl("Triceratops")
        .setDiet("Herbivorous")
        .setTimePeriod("Cretaceous")
        .setCreatureType("ceratopsian")
        .build()
    val dipl = GenusBuilderImpl("Diplodocus")
        .setDiet("Herbivorous")
        .setTimePeriod("Jurassic")
        .setCreatureType("sauropod")
        .build()
    val edmon = GenusBuilderImpl("Edmontosaurus")
        .setDiet("Herbivorous")
        .setTimePeriod("Jurassic")
        .setCreatureType("hadrosaur")
        .build()
    val ptero = GenusBuilderImpl("Pteranodon")
        .setDiet("Piscivorous")
        .setTimePeriod("Jurassic")
        .setCreatureType("pterosaur")
        .build()
    val raptor = GenusBuilderImpl("Velociraptor")
        .setDiet("Carnivorous")
        .setTimePeriod("Cretaceous")
        .setCreatureType("small theropod")
        .build()
    val ankylo = GenusBuilderImpl("Ankylosaurus")
        .setDiet("Herbivorous")
        .setTimePeriod("Cretaceous")
        .setCreatureType("ankylosaurid")
        .build()
    val stego = GenusBuilderImpl("Stegosaurus")
        .setDiet("Herbivorous")
        .setTimePeriod("Jurassic")
        .setCreatureType("Stegosaurid").build()
    val spino = GenusBuilderImpl("Spinosaurus").setDiet("Piscivorous")
        .setTimePeriod("Cretaceous")
        .setCreatureType("spinosaur").build()
    val unkn = GenusBuilderImpl("Othersaurus").setDiet("Nuts")
        .setTimePeriod("Other")
        .setCreatureType("other").build()

    val genera = listOf(
        acro, trike, dipl, raptor, ptero, edmon,
        ankylo, stego, spino, unkn
    )
    val generaPrefs = genera.map {
        val color = when (it.getDiet()) {
            Diet.Carnivore -> "BURGUNDY"
            Diet.Herbivore -> "BLACK"
            else -> null
        }
        GenusWithPrefs(it, LocalPrefs(_color = color))
    }
    val generaGrouped: IResultsByLetter<IGenusWithPrefs> = ResultsByLetter(generaPrefs)

    DinoDataTheme (darkTheme = true) {
        ListGenusScreenContent(
            uiState = ListGenusUiState(
                allPageData = generaGrouped,
                searchResults = genera,
                selectedPageIndex = 0,
                searchBarVisible = false,
                search = GenusSearch(
                    query = "taxon:ab",
                    taxa = listOf("abelisauridae", "brachiosauridae"),
                    locations = listOf("USA", "canada")
                ),
                loadState = LoadState.Loaded,
            ),
            updateSearchQuery = {},
            clearSearchQuery = {},
            toggleSearchBarVisibility = {},
            prefillSearchSuggestion = {}
        )
    }
}