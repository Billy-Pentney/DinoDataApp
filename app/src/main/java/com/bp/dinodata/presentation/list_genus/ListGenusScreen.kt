package com.bp.dinodata.presentation.list_genus

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bp.dinodata.R
import com.bp.dinodata.data.Diet
import com.bp.dinodata.data.IResultsByLetter
import com.bp.dinodata.data.ResultsByLetter
import com.bp.dinodata.data.genus.DebugGenusFactory
import com.bp.dinodata.data.genus.GenusBuilder
import com.bp.dinodata.data.genus.GenusWithPrefs
import com.bp.dinodata.data.genus.IGenus
import com.bp.dinodata.data.genus.IGenusWithPrefs
import com.bp.dinodata.data.genus.LocalPrefs
import com.bp.dinodata.data.search.GenusSearchBuilder
import com.bp.dinodata.data.search.terms.ISearchTerm
import com.bp.dinodata.presentation.DataState
import com.bp.dinodata.presentation.utils.DividerTextRow
import com.bp.dinodata.presentation.utils.LoadingItemsPlaceholder
import com.bp.dinodata.presentation.utils.MissingDataPlaceholder
import com.bp.dinodata.presentation.utils.NoDataPlaceholder
import com.bp.dinodata.theme.DinoDataTheme
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.max


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListGenusScreenContent(
    uiState: IListGenusUiState,
    navigateToGenus: (String) -> Unit = {},
    spacing: Dp = 8.dp,
    outerPadding: Dp = 12.dp,
    switchToPageByIndex: (Int) -> Unit = {},
    updateSearchQuery: (TextFieldValue) -> Unit,
    clearSearchQuery: () -> Unit,
    updateScrollState: (LazyListState) -> Unit,
    toggleSearchBarVisibility: (Boolean) -> Unit,
    refreshFeed: () -> Unit,
    prefillSearchSuggestion: () -> Unit,
    runSearch: () -> Unit,
    removeSearchTerm: (ISearchTerm<in IGenus>) -> Unit,
    openNavDrawer: () -> Unit,
    onSearchBarFocusChanged: (Boolean) -> Unit
) {
    val searchBarVisible = uiState.isSearchBarVisible()

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
                    // Button to open the navigation-drawer
                    IconButton(
                        onClick = {
                            openNavDrawer()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.List,
                            contentDescription = null,
//                            modifier = Modifier
//                                .padding(bottom = 4.dp)
//                                .alpha(0.6f)
                        )
                    }
                },
                actions = {
                    // Show Search Bar
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
                                    Icons.Filled.KeyboardArrowUp,
                                    contentDescription = stringResource(R.string.desc_close_search)
                                )
                            }
                        }
                    }

                    // Refresh feed button
                    IconButton(
                        onClick = refreshFeed,
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Icon(
                            Icons.Filled.Refresh,
                            "refresh the feed"
                        )
                    }
                }
            )
        }
    ) { pad ->
        Crossfade (
            uiState.getDataState(),
            label="listCrossfade",
            modifier = Modifier.padding(pad)
        ) {
            when (it) {
                is DataState.Success -> {
                    ShowSearchOrGeneraPager(
                        uiState = uiState,
                        spacing = spacing,
                        outerPadding = outerPadding,
                        navigateToGenus = navigateToGenus,
                        switchToPageByIndex = switchToPageByIndex,
                        runSearch = runSearch,
                        clearSearchQuery = clearSearchQuery,
                        updateSearchQuery = updateSearchQuery,
                        updateScrollState = updateScrollState,
                        prefillSearchSuggestion = prefillSearchSuggestion,
                        removeSearchTerm = removeSearchTerm,
                        onSearchBarFocusChanged = onSearchBarFocusChanged
                    )
                }

                is DataState.LoadInProgress -> {
                    LoadingItemsPlaceholder()
                }

                is DataState.Failed -> {
                    Text("Sorry! An error occurred. Reason: ${it.reason}")
                    MissingDataPlaceholder()
                }

                else -> {}
            }
        }
    }
}

@Composable
fun ShowSearchOrGeneraPager(
    uiState: IListGenusUiState,
    spacing: Dp,
    outerPadding: Dp,
    navigateToGenus: (String) -> Unit,
    switchToPageByIndex: (Int) -> Unit,
    runSearch: () -> Unit,
    clearSearchQuery: () -> Unit,
    updateSearchQuery: (TextFieldValue) -> Unit,
    updateScrollState: (LazyListState) -> Unit,
    prefillSearchSuggestion: () -> Unit,
    removeSearchTerm: (ISearchTerm<in IGenus>) -> Unit,
    onSearchBarFocusChanged: (Boolean) -> Unit
) {
    Crossfade(
        targetState = uiState.getContentMode(),
        label = "search_or_page_select"
    ) {
        when (it) {
            ListGenusContentMode.Search -> {
                SearchPage(
                    uiState = uiState,
                    updateSearchQuery = updateSearchQuery,
                    runSearch = runSearch,
                    clearSearchQuery = clearSearchQuery,
                    navigateToGenus = navigateToGenus,
                    outerPadding = outerPadding,
                    prefillSearchSuggestion = prefillSearchSuggestion,
                    updateScrollState = updateScrollState,
                    removeSearchTerm = removeSearchTerm,
                    modifier = Modifier.fillMaxSize(),
                    onSearchBarFocusChanged = onSearchBarFocusChanged
                )
            }
            ListGenusContentMode.Pager -> {
                HorizontalPagerOfGenera(
                    currentPageIndex = uiState.getCurrentPagerIndex(),
                    pageKeys = uiState.getPagerKeys(),
                    getPageByIndex = { page -> uiState.getPageByIndex(page) ?: emptyList() },
                    switchToPageByIndex = switchToPageByIndex,
                    outerPadding = outerPadding,
                    spacing = spacing,
                    navigateToGenus = navigateToGenus,
                    modifier = Modifier.fillMaxSize(),
                    updateScrollState = updateScrollState
                )
            }
        }
    }
}

@Composable
fun SearchPage(
    uiState: IListGenusSearchUiState,
    updateSearchQuery: (TextFieldValue) -> Unit,
    runSearch: () -> Unit,
    clearSearchQuery: () -> Unit,
    navigateToGenus: (String) -> Unit,
    outerPadding: Dp,
    prefillSearchSuggestion: () -> Unit,
    updateScrollState: (LazyListState) -> Unit,
    removeSearchTerm: (ISearchTerm<in IGenus>) -> Unit,
    onSearchBarFocusChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val searchResults = uiState.getSearchResultsAsList()

    val scrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(uiState) {
        // On open/recompose, animate to the correct position
        coroutineScope.launch {
            scrollState.animateScrollToItem(
                uiState.getFirstVisibleItemIndex(),
                uiState.getFirstVisibleItemOffset()
            )
        }
    }

    DisposableEffect(key1 = null) {
        onDispose {
            updateScrollState(scrollState)
        }
    }

    Column (
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface (shadowElevation = 4.dp) {
            Column {
                ListGenusSearchBar(
                    updateSearchQuery = updateSearchQuery,
                    clearSearchQuery = clearSearchQuery,
                    tryAcceptSearchSuggestion = prefillSearchSuggestion,
                    modifier = Modifier.padding(horizontal = outerPadding),
                    onSearchTermTap = removeSearchTerm,
                    searchTextFieldState = uiState.getSearchTextFieldState(),
                    completedSearchTerms = uiState.getCompletedSearchTerms(),
                    runSearch = runSearch,
                    onSearchBarFocusChanged = onSearchBarFocusChanged
                )
                DividerTextRow(
                    text = stringResource(R.string.text_showing_X_creatures, searchResults.size),
                    modifier = Modifier.padding(vertical = 12.dp, horizontal = outerPadding),
                    dividerPadding = PaddingValues(horizontal = 8.dp)
                )
            }
        }
        LazyListOfGenera(
            searchResults,
            contentPadding = PaddingValues(outerPadding),
            navigateToGenus = navigateToGenus,
            showCreatureCountAtBottom = false,
            scrollState = scrollState
        )
    }
}


@Composable
fun PageSelectorRow(
    pageKeys: List<String>,
    outerPadding: Dp,
    selectedPageIndex: Int,
    switchToPageByIndex: (Int) -> Unit
) {
    val rowScrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // When the page index updates, scroll the indicator automatically
    LaunchedEffect(selectedPageIndex) {
        coroutineScope.launch {
            val firstPosition = max(0, selectedPageIndex - 4)
            rowScrollState.animateScrollToItem(firstPosition)
        }
    }

    val keyIndices = remember { pageKeys.indices.toList() }

    val onSurface = MaterialTheme.colorScheme.onSurface
    val onBackground = MaterialTheme.colorScheme.onBackground

    val selectedTextStyle = remember {
        TextStyle.Default.copy(
            color = onSurface,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
    }
    val unselectedTextStyle = remember {
        TextStyle.Default.copy(
            color = onBackground,
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal
        )
    }

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
            val key = pageKeys[index]
            val isSelected = (selectedPageIndex == index)

            val boxBackground: Color
            val boxPadding: PaddingValues

            if (isSelected) {
                boxBackground = MaterialTheme.colorScheme.surface
                boxPadding = PaddingValues(horizontal=10.dp, vertical=8.dp)
            } else {
                boxBackground = MaterialTheme.colorScheme.background
                boxPadding = PaddingValues(horizontal=4.dp, vertical=6.dp)
            }

            val textStyle =
                if (isSelected) selectedTextStyle
                else unselectedTextStyle

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .height(IntrinsicSize.Min)
                    .clip(RoundedCornerShape(8.dp))
                    .animateContentSize()
                    .background(boxBackground)
                    .clickable(onClick = { switchToPageByIndex(index) })
            ) {
                Text(
                    key,
                    style = textStyle,
                    modifier = Modifier
                        .width(IntrinsicSize.Min)
                        .padding(boxPadding)
                        .alpha(if (isSelected) 1f else 0.75f),
                    textAlign = TextAlign.Center,
                )
            }
        }
    }

    // Horizontal Scroll Indicator
    if (pageKeys.size > 3) {
        val startPosition = selectedPageIndex.toFloat() / pageKeys.size
        val endPosition = selectedPageIndex.toFloat() / pageKeys.size
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
}

@Composable
fun HorizontalPagerOfGenera(
    currentPageIndex: Int,
    getPageByIndex: (Int) -> List<IGenus>,
    pageKeys: List<String>,
    switchToPageByIndex: (Int) -> Unit,
    outerPadding: Dp,
    spacing: Dp,
    navigateToGenus: (String) -> Unit,
    updateScrollState: (LazyListState) -> Unit,
    modifier: Modifier
) {
    var selectedPageIndex by remember { mutableIntStateOf(0) }

    if (pageKeys.isEmpty()) {
        NoDataPlaceholder()
        return
    }

    val coroutineScope = rememberCoroutineScope()
    val horizontalPagerState = rememberPagerState { pageKeys.size }

    LaunchedEffect(currentPageIndex) {
        selectedPageIndex = currentPageIndex
        if (selectedPageIndex != horizontalPagerState.currentPage) {
            coroutineScope.launch {
                horizontalPagerState.scrollToPage(selectedPageIndex)
            }
        }
    }

    LaunchedEffect(null) {
        // Collect from the a snapshotFlow reading the currentPage
        snapshotFlow { horizontalPagerState.currentPage }.collectLatest { page ->
            Log.d("ListGenusScreenPager", "Page changed to $page")
            switchToPageByIndex(page)
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

    Column (
        modifier = modifier.padding(top=outerPadding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PageSelectorRow(
            pageKeys = pageKeys,
            outerPadding = outerPadding,
            switchToPageByIndex = switchToPageByIndex,
            selectedPageIndex = selectedPageIndex
        )

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
            LazyListOfGenera(
                getPageByIndex(pageNum),
                verticalSpacing = spacing,
                navigateToGenus = navigateToGenus,
                modifier = Modifier.nestedScroll(nestedScrollConnection),
                scrollState = rememberLazyListState(),
                updateScrollState = updateScrollState
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
    scrollState: ScrollState = rememberScrollState(),
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
    updateScrollState: ((LazyListState) -> Unit)? = null,
    showCreatureCountAtBottom: Boolean = true
) {
    if (updateScrollState != null) {
        DisposableEffect(key1 = null) {
            onDispose {
                updateScrollState(scrollState)
            }
        }
    }

    val numElements = generaList?.size ?: 0


    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(verticalSpacing),
        state = scrollState,
        contentPadding = contentPadding,
        modifier = modifier.fillMaxWidth()
    ) {
        items(generaList ?: emptyList(), key = { it.getName() }) { genus ->
            GenusListItem(
                genus = genus,
                onClick = { navigateToGenus(genus.getName()) },
                modifier = Modifier
                    .animateItem(
                        fadeInSpec = spring(),
                        placementSpec = spring(
                            Spring.DampingRatioMediumBouncy,
                            Spring.StiffnessLow
                        )
                    )
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
    listGenusViewModel: IListGenusViewModel,
    navigateToGenus: (String) -> Unit,
    openNavDrawer: () -> Unit
) {
    val uiState by remember { listGenusViewModel.getUiState() }
    val contentMode by remember { derivedStateOf { uiState.getContentMode() } }
    val toastFlow = remember { listGenusViewModel.getToastFlow() }

    val context = LocalContext.current
    LaunchedEffect(null) {
        // Show the toast messages received from the view model
        toastFlow.collect { message: String ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    // Intercept back-press events if the search-bar is visible
    BackHandler (contentMode == ListGenusContentMode.Search) {
        listGenusViewModel.onUiEvent(ListGenusPageUiEvent.NavigateUp)
    }

    ListGenusScreenContent(
        uiState = uiState,
        navigateToGenus = navigateToGenus,
        switchToPageByIndex = { index ->
            listGenusViewModel.onUiEvent(ListGenusPageUiEvent.SwitchToPage(index))
        },
        updateSearchQuery = {
            listGenusViewModel.onUiEvent(ListGenusPageUiEvent.UpdateSearchQuery(it))
        },
        clearSearchQuery = {
            listGenusViewModel.onUiEvent(ListGenusPageUiEvent.ClearSearchQueryOrHideBar)
        },
        updateScrollState = {
            listGenusViewModel.onUiEvent(ListGenusPageUiEvent.UpdateScrollState(it))
        },
        toggleSearchBarVisibility = {
            listGenusViewModel.onUiEvent(ListGenusPageUiEvent.ToggleSearchBar(visible=it))
        },
        refreshFeed = {
            listGenusViewModel.onUiEvent(ListGenusPageUiEvent.RefreshFeed)
        },
        prefillSearchSuggestion = {
            listGenusViewModel.onUiEvent(ListGenusPageUiEvent.AcceptSearchSuggestion)
        },
        runSearch = {
            listGenusViewModel.onUiEvent(ListGenusPageUiEvent.RunSearch)
        },
        removeSearchTerm = { term ->
            listGenusViewModel.onUiEvent(ListGenusPageUiEvent.RemoveSearchTerm(term))
        },
        openNavDrawer = openNavDrawer,
        onSearchBarFocusChanged = {
            listGenusViewModel.onUiEvent(ListGenusPageUiEvent.FocusSearchBar(it))
        }
    )
}

@SuppressLint("UnrememberedMutableState")
@Preview(widthDp=400)
@Composable
fun PreviewListGenus() {

    val genera = DebugGenusFactory.getAll()
    val generaPrefs = genera.map {
        val color = when (it.getDiet()) {
            Diet.Carnivore -> "BURGUNDY"
            Diet.Herbivore -> "BLACK"
            else -> null
        }
        val prefs = LocalPrefs(
            _color = color,
            _isFavourite = it.getDiet() == Diet.Herbivore
        )
        GenusWithPrefs(it,prefs)
    }
    val generaGrouped: IResultsByLetter<IGenusWithPrefs> = ResultsByLetter(generaPrefs)

    DinoDataTheme (darkTheme = true) {
        ListGenusScreenContent(
            uiState = ListGenusUiState(
                allPageData = DataState.Success(generaGrouped),
                searchResults = DataState.Success(genera),
                selectedPageIndex = 0,
                contentMode = ListGenusContentMode.Pager,
                search = GenusSearchBuilder(
                    query = "taxon:ab",
                    locations = listOf("USA", "canada"),
                    taxa = listOf("abelisauridae", "brachiosauridae")
                ).build(),
            ),
            updateSearchQuery = {},
            clearSearchQuery = {},
            updateScrollState = {},
            toggleSearchBarVisibility = {},
            refreshFeed = {},
            prefillSearchSuggestion = {},
            runSearch = {},
            removeSearchTerm = {},
            openNavDrawer = {},
            onSearchBarFocusChanged = {}
        )
    }
}