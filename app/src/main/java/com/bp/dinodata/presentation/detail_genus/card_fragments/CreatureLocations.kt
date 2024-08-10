package com.bp.dinodata.presentation.detail_genus.card_fragments

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.bp.dinodata.R
import com.bp.dinodata.presentation.detail_genus.LabelContentRow
import com.bp.dinodata.presentation.detail_genus.location_map.LocationAtlas

@Composable
fun CreatureLocations(locations: List<String>, iconModifier: Modifier) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        LabelContentRow(
            label = stringResource(R.string.label_locations),
            valueContent = {
//                Text(locations.joinToString())
            },
            leadingIcon = {
                Icon(
                    Icons.Filled.LocationOn, null,
                    modifier = iconModifier
                )
            }
        )
        LocationAtlas(locations, modifier = Modifier.shadow(4.dp))
    }
}