package com.bp.dinodata.presentation.detail_genus.card_fragments

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bp.dinodata.data.TaxonTreeBuilder
import com.bp.dinodata.data.genus.IGenus

@Composable
fun ShowTaxonomicTree(
    genus: IGenus,
    modifier: Modifier,
    internalCardPadding: PaddingValues = PaddingValues(16.dp)
) {
    var rootToLeafPath: List<String> by remember { mutableStateOf(emptyList()) }
    var leafName: String by remember { mutableStateOf("") }

    LaunchedEffect(genus) {
        val taxonomy = genus.getListOfTaxonomy()
        val taxonBuilder = TaxonTreeBuilder(taxonomy)
        val taxonTree = taxonBuilder.getPrintableTree(genus = genus.getName())
        rootToLeafPath = taxonTree.dropLast(1)
        leafName = taxonTree.last()
    }

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        LazyRow(
            modifier = Modifier
                .padding(internalCardPadding)
                .fillMaxWidth()
        ) {
            item {
                Column {
                    Text(
                        rootToLeafPath.joinToString("\n"),
//                        lineHeight = 18.sp
                    )
                    Text(
                        leafName,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.scrim,
                    )
                }
            }
        }
    }
}

