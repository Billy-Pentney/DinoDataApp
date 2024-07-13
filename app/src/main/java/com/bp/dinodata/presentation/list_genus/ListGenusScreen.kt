package com.bp.dinodata.presentation.list_genus

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Cyan
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bp.dinodata.R
import com.bp.dinodata.data.Genus
import com.bp.dinodata.data.GenusBuilderImpl
import com.bp.dinodata.presentation.LoadState
import com.bp.dinodata.theme.DinoDataTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListGenusScreenContent(
    loadState: LoadState,
    searchQueryState: State<String>,
    searchBarVisibility: State<Boolean>,
    genusList: List<Genus>,
    navigateToGenus: (String) -> Unit = {},
    columns: Int = 1,
    spacing: Dp = 8.dp,
    outerPadding: Dp = 12.dp,
    requestNextPage: () -> Unit = {},
    updateSearchQuery: (String) -> Unit,
    clearSearchQuery: () -> Unit,
    toggleSearchBarVisibility: (Boolean) -> Unit
) {
    val scrollState = rememberLazyListState()
//    val scrollState = rememberLazyGridState()
    val scrollPosition by remember { derivedStateOf { scrollState.firstVisibleItemIndex } }
    var loadTriggered by remember { mutableStateOf(false) }

//    LaunchedEffect(scrollPosition) {
//        if (genusList.isNotEmpty()
//            && genusList.size-15 <= scrollPosition
//            && !loadTriggered)
//        {
//            Log.d("ListGenusScreen","Trigger load")
//            requestNextPage()
//            loadTriggered = true
//        }
//    }
//
//    SideEffect {
//        if (loadState is LoadState.IsLoaded) {
//            loadTriggered = false
//        }
//    }

    val searchQuery = remember { searchQueryState }

    val searchBarVisible by remember { searchBarVisibility }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.title_creature_list),
                        fontWeight = FontWeight.SemiBold
                    )
                },
                actions = {
                    IconButton(
                        onClick = { toggleSearchBarVisibility(!searchBarVisible) },
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
        when (loadState) {
            is LoadState.IsLoaded,
            is LoadState.LoadingPage -> {

                LazyColumn(
//                    columns = GridCells.Fixed(columns),
                    verticalArrangement = Arrangement.spacedBy(spacing),
//                    horizontalArrangement = Arrangement.spacedBy(spacing),
                    state = scrollState,
                    contentPadding = PaddingValues(outerPadding),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(pad)
                ) {
                    item {
                        ListGenusSearchBar(
                            searchBarQuery = searchQuery,
                            searchBarVisibility = searchBarVisibility,
                            toggleVisibility = toggleSearchBarVisibility,
                            updateSearchQuery = updateSearchQuery,
                            clearSearchQuery = clearSearchQuery,
                            numCreaturesVisible = genusList.size
                        )
                    }

                    items(genusList, key = { it.name }) {
                        genus ->
                        GenusListItem(
                            genus = genus,
                            onClick = { navigateToGenus(genus.name) },
                            showDietText = false
                        )
                    }
                }
            }
            is LoadState.LoadingFirstPage -> {
                // Loading items Placeholder
                Column (
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(color=Cyan)
                    Spacer(modifier=Modifier.height(8.dp))
                    Text("loading...")
                }
            }
            is LoadState.Error -> {
                Text("Sorry! An error occurred. Reason: ${loadState.reason}")
            }
            else -> { }
        }
    }
}


@Composable
fun ListGenusScreen(
    listGenusViewModel: ListGenusViewModel,
    navigateToGenus: (String) -> Unit,
) {
    val genera by listGenusViewModel.getListOfGenera().collectAsState()
    val hasLoaded by remember { listGenusViewModel.getIsLoadedState() }
    val searchQuery = remember { listGenusViewModel.getSearchQueryState() }
    val searchBarVisibility = remember { listGenusViewModel.getSearchBarVisibility() }

    val context = LocalContext.current

//    BackHandler (searchQuery.value.isNotBlank()) {
//        // Clear the Search bar when navigating up
//        listGenusViewModel.onEvent(ListGenusPageUiEvent.ClearSearchQuery)
//    }
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
        loadState = hasLoaded,
        searchQueryState = searchQuery,
        genusList = genera,
        navigateToGenus = navigateToGenus,
        searchBarVisibility = searchBarVisibility,
        requestNextPage = {
            Toast.makeText(context, "Loading new page...", Toast.LENGTH_SHORT).show()
            listGenusViewModel.onEvent(ListGenusPageUiEvent.InitiateNextPageLoad)
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
        .setCreatureType("ankylosaur")
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

    val searchQuery = remember { mutableStateOf("") }
    val searchBarVisibility = remember { mutableStateOf(true) }

    DinoDataTheme (darkTheme = true) {
        ListGenusScreenContent(
            loadState = LoadState.IsLoaded(0),
            searchQueryState = searchQuery,
            searchBarVisibility = searchBarVisibility,
            genusList = listOf(
                acro, trike, dipl, raptor, ptero, edmon,
                ankylo, stego, spino, unkn
            ),
            columns = 1,
            //            triggerNextPageLoad = {  },
            updateSearchQuery = {},
            clearSearchQuery = {},
            toggleSearchBarVisibility = {}
        )
    }
}