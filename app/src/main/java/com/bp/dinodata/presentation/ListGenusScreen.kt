package com.bp.dinodata.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.painter.BrushPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.bp.dinodata.R
import com.bp.dinodata.data.CreatureType
import com.bp.dinodata.data.Genus
import com.bp.dinodata.data.GenusBuilderImpl
import com.bp.dinodata.presentation.vm.ListGenusViewModel

fun ConvertCreatureTypeToSilhouette(type: CreatureType): Int {
    return when (type) {
        CreatureType.Ceratopsian -> R.drawable.ceratopsian
        CreatureType.LargeTheropod -> R.drawable.acrocanth
        CreatureType.Sauropod -> R.drawable.sauropod
        CreatureType.Hadrosaur -> R.drawable.edmonto
        else -> R.drawable.unkn
    }
}


@Composable
fun GenusListItem(genus: Genus, onClick: () -> Unit = {}) {

    val silhouetteId = ConvertCreatureTypeToSilhouette(genus.type)

    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color.DarkGray,
            contentColor = Color.White
        ),
        modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
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
                genus.diet?.let { DietIconThin(diet = it) }
            }
            Box(
                modifier = Modifier.fillMaxWidth().fillMaxHeight(),
                contentAlignment = Alignment.CenterEnd
            ) {
                Image(
                    painterResource(id = silhouetteId),
                    contentDescription = null,
                    modifier = Modifier
                        .alpha(0.15f)
                        .padding(top=10.dp, bottom=0.dp)
                        .zIndex(0f)
                        .fillMaxWidth(0.55f)
                        .absoluteOffset(x=60.dp, y=5.dp)
                        .heightIn(max=90.dp),
                    alignment = Alignment.TopStart,
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}


@Composable
fun ListGenusScreenContent(
    listGenus: List<Genus>,
    navigateToGenus: (String) -> Unit = {}
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        items(listGenus) { genus ->
            GenusListItem(
                genus,
                onClick = { navigateToGenus(genus.name) }
            )
        }
    }
}


@Composable
fun ListGenusScreen(
    listGenusViewModel: ListGenusViewModel,
    navigateToGenus: (String) -> Unit
) {
    val genera by listGenusViewModel.getListOfGenera().collectAsState()
    ListGenusScreenContent(
        genera,
        navigateToGenus
    )
}

@Preview
@Composable
fun PreviewListGenusScreen() {
    val acro = GenusBuilderImpl.fromDict(
        mutableMapOf(
            "name" to "Acrocanthosaurus",
            "diet" to "Carnivorous",
            "timePeriod" to "Late Cretaceous",
            "type" to "large theropod"
        )
    )!!.build()
    val trike = GenusBuilderImpl.fromDict(
        mutableMapOf(
            "name" to "Triceratops",
            "diet" to "Herbivorous",
            "timePeriod" to "Cretaceous",
            "type" to "ceratopsian"
        )
    )!!.build()
    val dipl = GenusBuilderImpl.fromDict(
        mutableMapOf(
            "name" to "Diplodocus",
            "diet" to "Herbivorous",
            "timePeriod" to "Jurassic",
            "type" to "sauropod"
        )
    )!!.build()
    val edmon = GenusBuilderImpl.fromDict(
        mutableMapOf(
            "name" to "Edmontosaurus",
            "diet" to "Herbivorous",
            "timePeriod" to "Jurassic",
            "type" to "hadrosaur"
        )
    )!!.build()

    ListGenusScreenContent(
        listGenus = listOf(acro, trike, dipl, edmon)
    )
}