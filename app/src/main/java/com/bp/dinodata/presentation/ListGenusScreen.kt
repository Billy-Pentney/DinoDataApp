package com.bp.dinodata.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bp.dinodata.data.Genus
import com.bp.dinodata.data.GenusBuilderImpl
import com.bp.dinodata.presentation.vm.ListGenusViewModel
import com.bp.dinodata.theme.SurfaceGrey

@Composable
fun GenusListItem(genus: Genus) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = SurfaceGrey,
            contentColor = Color.White
        ),
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(2.dp, Color.White),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                genus.name,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                fontStyle = FontStyle.Italic
            )
            genus.diet?.let { Text(it.toString(), modifier = Modifier.alpha(0.6f)) }
        }
    }
}


@Composable
fun ListGenusScreenContent(listGenus: List<Genus>) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(listGenus) { genus ->
            GenusListItem(genus)
        }
    }
}


@Composable
fun ListGenusScreen(
    listGenusViewModel: ListGenusViewModel
) {
    val genera by remember { listGenusViewModel.getListOfGenera() }
    ListGenusScreenContent(genera)
}

@Preview
@Composable
fun PreviewListGenusScreen() {
    val acro = GenusBuilderImpl.fromDict(
        mutableMapOf(
            "name" to "Acrocanthosaurus",
            "diet" to "Carnivorous",
            "timePeriod" to "Late Cretaceous"
        )
    )!!.build()
    val styr = GenusBuilderImpl.fromDict(
        mutableMapOf(
            "name" to "Styracosaurus",
            "diet" to "Herbivorous",
            "timePeriod" to "Cretaceous"
        )
    )!!.build()
    ListGenusScreenContent(
        listGenus = listOf(acro, styr)
    )
}