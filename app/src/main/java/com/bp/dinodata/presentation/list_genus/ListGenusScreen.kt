package com.bp.dinodata.presentation.list_genus

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color.Companion.Cyan
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bp.dinodata.R
import com.bp.dinodata.data.GenusBuilderImpl
import com.bp.dinodata.data.ResultsByLetter
import com.bp.dinodata.presentation.LoadState
import com.bp.dinodata.presentation.utils.DividerTextRow
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
    updateSearchQuery: (String) -> Unit,
    clearSearchQuery: () -> Unit,
    toggleSearchBarVisibility: (Boolean) -> Unit,
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
                        modifier = Modifier.padding(bottom=4.dp).alpha(0.6f)
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
                        updateSearchQuery = updateSearchQuery
                    )
                }

                LoadState.InProgress -> {
                    // Loading items Placeholder
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(color = Cyan)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("loading...")
                    }
                }

                is LoadState.Error -> {
                    Text("Sorry! An error occurred. Reason: ${loadState?.reason}")
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
    updateSearchQuery: (String) -> Unit
) {

    val keys = uiState.pageKeys
    val selectedKeyIndex = uiState.selectedPageIndex
//    val numGroups = keys.size

    if (keys.isEmpty()) {
        Text("No data to display!")
        return
    }

    val keyIndices = remember { keys.indices.toList() }

    val generaList = uiState.getVisibleGenera() ?: emptyList()
    val scrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    Column (
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Crossfade(
            targetState = uiState.searchBarVisible,
            label="search_or_page_select",
            modifier = Modifier.padding(horizontal=outerPadding).padding(top=outerPadding)
        ) {
            if (it) {
                ListGenusSearchBar(
                    searchBarQuery = uiState.searchBarQuery,
                    toggleVisibility = toggleSearchBarVisibility,
                    updateSearchQuery = updateSearchQuery,
                    clearSearchQuery = clearSearchQuery,
                )
            }
            else {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally)
                ) {
                    items(keyIndices) { index ->
                        val key = keys[index]
                        val isSelected = (selectedKeyIndex == index)
                        val color =
                            if (isSelected) MaterialTheme.colorScheme.surface
                            else MaterialTheme.colorScheme.background

                        Surface(
                            color = color,
                            shape = RoundedCornerShape(8.dp),
                            onClick = {
                                switchToPageByIndex(index)
                                coroutineScope.launch {
                                    scrollState.animateScrollToItem(0, 0)
                                }
                            },
                            modifier = Modifier
                                .height(IntrinsicSize.Min)
                                .aspectRatio(if (isSelected) 1f else 0.6f)
                        ) {
                            Text(
                                key,
                                fontSize = if (isSelected) 20.sp else 18.sp,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(vertical = 12.dp)
                                    .alpha(if (isSelected) 1f else 0.75f),
                                textAlign = TextAlign.Center,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }

        DividerTextRow(
            text = stringResource(R.string.text_showing_X_creatures, generaList.size),
            modifier = Modifier.padding(horizontal = 4.dp + outerPadding).padding(top=8.dp),
            dividerPadding = PaddingValues(horizontal = 8.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(spacing),
            state = scrollState,
            contentPadding = PaddingValues(
                bottom = 60.dp,
                top = outerPadding,
                start = outerPadding,
                end = outerPadding
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(generaList, key = { it.getName() }) { genus ->
                GenusListItem(
                    genus = genus,
                    onClick = { navigateToGenus(genus.getName()) }
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
    val searchQuery = remember { derivedStateOf { uiState.searchBarQuery } }
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
//            Toast.makeText(context, "Loading new page $index", Toast.LENGTH_SHORT).show()
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
        .setCreatureType("Stegosaur").build()
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
    val generaGrouped = ResultsByLetter(genera)
    val keys = generaGrouped.getKeys()

    DinoDataTheme (darkTheme = true) {
        ListGenusScreenContent(
            uiState = ListGenusUiState(
                visiblePage = genera,//generaGrouped.getGroupByIndex(0),
                searchBarQuery = "",
                searchBarVisible = false,
                loadState = LoadState.Loaded,
                pageKeys = keys.map { it.toString() }
            ),
            updateSearchQuery = {},
            clearSearchQuery = {},
            toggleSearchBarVisibility = {}
        )
    }
}