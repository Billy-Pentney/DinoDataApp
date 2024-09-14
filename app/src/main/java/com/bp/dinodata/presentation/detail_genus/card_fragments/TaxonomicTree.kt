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
    var genusTaxonomyList by remember { mutableStateOf(emptyList<String>()) }
    genusTaxonomyList = genus.getListOfTaxonomy()

    // Store an ordered list of all non-leaf taxa to which this genus belongs.
    // These values should be from least-to-most specific.
    var nonTerminalTaxa: List<String> by remember { mutableStateOf(emptyList()) }
    // Store the name of the leaf (i.e. the genus name)
    var leafName: String by remember { mutableStateOf("") }

    LaunchedEffect(genusTaxonomyList) {
        val taxonBuilder = TaxonTreeBuilder(genusTaxonomyList)
        val taxonTree = taxonBuilder.getPrintableTree(genus = genus.getName())
        nonTerminalTaxa = taxonTree.dropLast(1)
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
                    // Show the non-terminal taxa
                    if (nonTerminalTaxa.isNotEmpty()) {
                        Text(nonTerminalTaxa.joinToString("\n"))
                    }
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

