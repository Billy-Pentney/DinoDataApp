package com.bp.dinodata.presentation.detail_genus.card_fragments

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Interests
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.bp.dinodata.R
import com.bp.dinodata.data.genus.IGenus
import com.bp.dinodata.presentation.detail_genus.LabelContentRow
import com.bp.dinodata.presentation.detail_genus.SpeciesListItem

@Composable
fun ShowCreatureSpeciesCards(
    genus: IGenus,
    iconModifier: Modifier = Modifier
) {

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        LabelContentRow(
            label = stringResource(R.string.label_known_species),
            valueContent = {},
            leadingIcon = {
                Icon(
                    Icons.Filled.Interests,
                    null,
                    modifier = iconModifier
                )
            }
        )
        Spacer(modifier = Modifier.height(4.dp))
        genus.getSpeciesList().forEach {
            SpeciesListItem(it, genus.getName())
        }
    }
}