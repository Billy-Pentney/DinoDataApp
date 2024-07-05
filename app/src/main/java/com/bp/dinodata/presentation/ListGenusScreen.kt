package com.bp.dinodata.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.bp.dinodata.data.Genus
import com.bp.dinodata.data.GenusBuilderImpl
import com.bp.dinodata.presentation.icons.DietIconThin
import com.bp.dinodata.presentation.icons.TimePeriodIcon
import com.bp.dinodata.presentation.utils.ConvertCreatureTypeToSilhouette
import com.bp.dinodata.presentation.vm.ListGenusViewModel


@Composable
fun GenusListItem(
    genus: Genus,
    onClick: () -> Unit = {},
    showDietText: Boolean = true
) {
    val silhouetteId = ConvertCreatureTypeToSilhouette(genus.type)

    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color.DarkGray,
            contentColor = Color.White
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        shape = RoundedCornerShape(8.dp),
        onClick = onClick
    ) {
        Box (
            contentAlignment = Alignment.CenterStart,
            modifier=Modifier.height(IntrinsicSize.Min)
        ) {
            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .zIndex(1.0f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    genus.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    fontStyle = FontStyle.Italic
                )
                genus.diet?.let { DietIconThin(diet = it, showText = showDietText) }
                genus.timePeriod?.let { TimePeriodIcon(timePeriod = it) }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                contentAlignment = Alignment.BottomEnd
            ) {
                Image(
                    painterResource(id = silhouetteId),
                    contentDescription = null,
                    modifier = Modifier
                        .alpha(0.15f)
                        .zIndex(0f)
                        .padding(top = 5.dp)
                        .fillMaxWidth(0.6f)
                        .absoluteOffset(x = 50.dp, y = 15.dp)
                        .heightIn(max = 110.dp),
                    alignment = Alignment.TopStart,
                    contentScale = ContentScale.Crop,
//                    colorFilter = ColorFilter.tint(Color.Green, BlendMode.Overlay)
                )
            }
        }
    }
}


@Composable
fun ListGenusScreenContent(
    listGenus: List<Genus>,
    navigateToGenus: (String) -> Unit = {},
    columns: Int = 1,
    spacing: Dp = 8.dp,
    outerPadding: PaddingValues = PaddingValues(12.dp),
    showDietText: Boolean = true
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        verticalArrangement = Arrangement.spacedBy(spacing),
        horizontalArrangement = Arrangement.spacedBy(spacing),
        modifier = Modifier
            .fillMaxWidth()
            .padding(outerPadding)
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


@Composable
fun ListGenusScreen(
    listGenusViewModel: ListGenusViewModel,
    navigateToGenus: (String) -> Unit,
) {
    val genera by listGenusViewModel.getListOfGenera().collectAsState()
    ListGenusScreenContent(
        genera,
        navigateToGenus,
    )
}

@Preview(widthDp=800)
@Composable
fun PreviewListGenusScreen() {
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

    ListGenusScreenContent(
        listOf(
            acro, trike, dipl, raptor, ptero, edmon,
            ankylo, stego, spino, unkn
        ),
        columns = 2,
        showDietText = false
    )
}