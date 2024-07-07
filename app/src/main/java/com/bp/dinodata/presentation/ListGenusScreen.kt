package com.bp.dinodata.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.bp.dinodata.R
import com.bp.dinodata.data.Genus
import com.bp.dinodata.data.GenusBuilderImpl
import com.bp.dinodata.presentation.icons.DietIconThin
import com.bp.dinodata.presentation.utils.convertCreatureTypeToSilhouette
import com.bp.dinodata.presentation.vm.ListGenusViewModel
import com.bp.dinodata.theme.DinoDataTheme


@Composable
fun GenusListItem(
    genus: Genus,
    onClick: () -> Unit = {},
    showDietText: Boolean = true
) {
    val silhouetteId = convertCreatureTypeToSilhouette(genus.type)

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
            contentAlignment = Alignment.CenterStart,
            modifier=Modifier.fillMaxHeight()
        ) {
            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .zIndex(1.0f),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row (
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    genus.diet?.let { DietIconThin(diet = it, showText = showDietText) }
                    Text(
                        genus.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        fontStyle = FontStyle.Italic
                    )
                }

//                genus.timePeriod?.let { TimePeriodIcon(timePeriod = it) }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .absoluteOffset(y=5.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                Image(
                    painterResource(id = silhouetteId),
                    contentDescription = null,
                    modifier = Modifier
                        .alpha(0.5f)
                        .zIndex(0f)
//                        .padding(top = 5.dp)
                        .fillMaxWidth(0.33f)
                        .absoluteOffset(x = 25.dp, y = 0.dp)
                        .fillMaxHeight(),
                    alignment = Alignment.BottomStart,
                    contentScale = ContentScale.Crop,
//                    colorFilter = ColorFilter.tint(Color.Green, BlendMode.Overlay)
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListGenusScreenContent(
    listGenus: List<Genus>,
    navigateToGenus: (String) -> Unit = {},
    columns: Int = 1,
    spacing: Dp = 8.dp,
    outerPadding: PaddingValues = PaddingValues(12.dp),
    showDietText: Boolean = true
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.title_creature_list)) }) }
    ) { pad ->

        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            verticalArrangement = Arrangement.spacedBy(spacing),
            horizontalArrangement = Arrangement.spacedBy(spacing),
            modifier = Modifier
                .fillMaxWidth()
                .padding(outerPadding)
                .padding(pad)
        ) {
            items(listGenus) { genus ->
                GenusListItem(
                    genus = genus,
                    onClick = { navigateToGenus(genus.name) },
                    showDietText = showDietText
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
    val genera by listGenusViewModel.getListOfGenera().collectAsState()
    ListGenusScreenContent(
        genera,
        navigateToGenus,
        showDietText = false
    )
}

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
        .setCreatureType("avian")
        .build()
    val raptor = GenusBuilderImpl("Velociraptor")
        .setDiet("Carnivorous")
        .setTimePeriod("Cretaceous")
        .setCreatureType("small theropod")
        .build()
    val ankylo = GenusBuilderImpl("Ankylosaurus")
        .setDiet("Herbivorous")
        .setTimePeriod("Cretaceous")
        .setCreatureType("armoured")
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
            listOf(
                acro, trike, dipl, raptor, ptero, edmon,
                ankylo, stego, spino, unkn
            ),
            columns = 1,
            showDietText = false
        )
    }
}