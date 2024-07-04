package com.bp.dinodata.presentation

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bp.dinodata.data.Genus
import com.bp.dinodata.data.GenusBuilderImpl
import com.bp.dinodata.presentation.vm.ListGenusViewModel

@Composable
fun GenusListItem(genus: Genus, onClick: () -> Unit = {}) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color.DarkGray,
            contentColor = Color.White
        ),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
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