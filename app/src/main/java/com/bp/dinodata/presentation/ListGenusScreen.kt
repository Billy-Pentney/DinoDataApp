package com.bp.dinodata.presentation

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Cyan
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.bp.dinodata.R
import com.bp.dinodata.data.Genus
import com.bp.dinodata.data.GenusBuilderImpl
import com.bp.dinodata.presentation.icons.DietIconSquare
import com.bp.dinodata.presentation.utils.convertCreatureTypeToSilhouette
import com.bp.dinodata.presentation.vm.ListGenusPageUiEvent
import com.bp.dinodata.presentation.vm.ListGenusViewModel
import com.bp.dinodata.theme.DinoDataTheme
import com.bp.dinodata.theme.MyGrey600


@Composable
fun GenusListItem(
    genus: Genus,
    onClick: () -> Unit = {},
    showDietText: Boolean = true
) {
    val silhouetteId = remember {
        convertCreatureTypeToSilhouette(genus.type)
    }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color.DarkGray, //MaterialTheme.colorScheme.surface,
            contentColor = Color.White//MaterialTheme.colorScheme.onSurface
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
//            .height(100.dp),
        shape = RoundedCornerShape(8.dp),
        onClick = onClick
    ) {
        Box (
            contentAlignment = Alignment.BottomEnd,
            modifier = Modifier.fillMaxHeight()
        ) {
            Row (
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(8.dp)
                    .zIndex(1.0f)
            ) {
                DietIconSquare(diet = genus.diet)
                Text(
                    genus.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    fontStyle = FontStyle.Italic,
                    modifier = Modifier.padding(vertical=2.dp)
                )
            }
            LoadAsyncImageOrReserveDrawable(
                imageUrl = genus.mainThumbnailUrl,
                drawableIfImageFailed = silhouetteId,
                contentDescription = null,
                modifier = Modifier
                    .alpha(0.5f)
                    .zIndex(0f)
//                        .padding(top = 5.dp)
                    .fillMaxWidth(0.33f)
                    .absoluteOffset(x = 25.dp, y = 5.dp)
                    .fillMaxHeight(),
                alignment = Alignment.BottomStart,
                contentScale = ContentScale.Crop,
//              colorFilter = ColorFilter.tint(Color.Green, BlendMode.Overlay)
            )
        }
    }
}

@Composable
fun TotalCreaturesCard(
    numCreatures: Int
) {
    Card (
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal=20.dp, vertical=25.dp),
            verticalAlignment = Alignment.Top
        ) {
            Text(
                "Showing:",
                color = Color.White
            )
            Spacer(Modifier.weight(1f))
            Text(
                "$numCreatures ",
                fontSize = 42.sp,
                fontStyle = FontStyle.Italic,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.End,
                modifier = Modifier
                    .offset(y = 0.dp)
                    .padding(end = 4.dp)
            )
            Text(
                "creatures",
                color = Color.White,
                modifier = Modifier
                    .alpha(0.5f)
                    .fillMaxHeight(),
                fontSize = 22.sp
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListGenusSearchBar(
    searchBarQuery: State<String>,
    toggleVisibility: (Boolean) -> Unit,
    updateSearchQuery: (String) -> Unit,
    clearSearchQuery: () -> Unit
) {
    SearchBar(
        query = searchBarQuery.value,
        onQueryChange = updateSearchQuery,
        onSearch = {
            updateSearchQuery(it)
            // TODO - Close keyboard
        },
        active = false,
        onActiveChange = {},
        leadingIcon = {
            Icon(Icons.Outlined.Search, "search")
        },
        trailingIcon = {
            IconButton(onClick = {
                clearSearchQuery()
                toggleVisibility(false)
            }) {
                Icon(
                    Icons.Filled.Close,
                    "clear search",
                    tint = Color.White
                )
            }
        },
        colors = SearchBarDefaults.colors(
            containerColor = MyGrey600
        ),
        modifier = Modifier
            .padding(bottom = 8.dp)
            .fillMaxWidth()
    ) {

    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListGenusScreenContent(
    loadState: LoadState,
    searchQuery: State<String>,
    genusList: List<Genus>,
    navigateToGenus: (String) -> Unit = {},
    columns: Int = 1,
    spacing: Dp = 8.dp,
    outerPadding: Dp = 12.dp,
    showDietText: Boolean = true,
    searchBarVisible: Boolean = false,
    requestNextPage: () -> Unit = {},
    updateSearchQuery: (String) -> Unit,
    clearSearchQuery: () -> Unit,
    toggleSearchBarVisibility: (Boolean) -> Unit
) {
    val scrollState = rememberLazyGridState()
    val scrollPosition by remember { derivedStateOf { scrollState.firstVisibleItemIndex } }
    var loadTriggered by remember { mutableStateOf(false) }

    LaunchedEffect(scrollPosition) {
        if (genusList.isNotEmpty()
            && genusList.size-15 <= scrollPosition
            && !loadTriggered)
        {
            Log.d("ListGenusScreen","Trigger load")
            requestNextPage()
            loadTriggered = true
        }
    }

    SideEffect {
        if (loadState is LoadState.IsLoaded) {
            loadTriggered = false
        }
    }

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
                    IconButton(onClick = { toggleSearchBarVisibility(!searchBarVisible) } ) {
                        Icon(
                            Icons.Filled.Search,
                            contentDescription = "show search bar",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        }
    ) { pad ->
        when (loadState) {
            is LoadState.IsLoaded,
            is LoadState.LoadingPage -> {

                LazyVerticalGrid(
                    columns = GridCells.Fixed(columns),
                    verticalArrangement = Arrangement.spacedBy(spacing),
                    horizontalArrangement = Arrangement.spacedBy(spacing),
                    state = scrollState,
                    contentPadding = PaddingValues(outerPadding),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(pad)
                ) {
                    item {
                        Crossfade(
                            searchBarVisible, label = "searchBar",
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (it) {
                                ListGenusSearchBar(
                                    searchBarQuery = searchQuery,
                                    toggleVisibility = toggleSearchBarVisibility,
                                    updateSearchQuery = updateSearchQuery,
                                    clearSearchQuery = clearSearchQuery
                                )
                            }
                            else {
                                TotalCreaturesCard(genusList.size)
                            }
                        }
                    }

                    items(
                        genusList,
                        key = { it.name }
                    ) { genus ->
                        GenusListItem(
                            genus = genus,
                            onClick = { navigateToGenus(genus.name) },
                            showDietText = showDietText
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
        listGenusViewModel.onEvent(ListGenusPageUiEvent.ClearSearchQuery)
        // Close the Search Bar when navigating up
        listGenusViewModel.onEvent(ListGenusPageUiEvent.ToggleSearchBar(false))
    }

    ListGenusScreenContent(
        loadState = hasLoaded,
        searchQuery = searchQuery,
        genusList = genera,
        navigateToGenus = navigateToGenus,
        showDietText = false,
        searchBarVisible = searchBarVisibility.value,
        requestNextPage = {
            Toast.makeText(context, "Loading new page...", Toast.LENGTH_SHORT).show()
            listGenusViewModel.onEvent(ListGenusPageUiEvent.InitiateNextPageLoad)
        },
        updateSearchQuery = {
            listGenusViewModel.onEvent(ListGenusPageUiEvent.UpdateSearchQuery(it))
        },
        clearSearchQuery = {
            listGenusViewModel.onEvent(ListGenusPageUiEvent.ClearSearchQuery)
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

    DinoDataTheme (darkTheme = true) {
        ListGenusScreenContent(
            loadState = LoadState.IsLoaded(0),
            searchQuery = mutableStateOf("Test string"),
            searchBarVisible = false,
            genusList = listOf(
                acro, trike, dipl, raptor, ptero, edmon,
                ankylo, stego, spino, unkn
            ),
            columns = 1,
            showDietText = false,
//            triggerNextPageLoad = {  },
            updateSearchQuery = {},
            clearSearchQuery = {},
            toggleSearchBarVisibility = {}
        )
    }
}