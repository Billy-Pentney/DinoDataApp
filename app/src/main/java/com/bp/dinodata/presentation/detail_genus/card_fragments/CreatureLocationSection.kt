package com.bp.dinodata.presentation.detail_genus.card_fragments

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.bp.dinodata.R
import com.bp.dinodata.data.genus.IHasListOfFormations
import com.bp.dinodata.data.genus.IHasLocationInfo
import com.bp.dinodata.presentation.detail_genus.SectionLabelRow
import com.bp.dinodata.presentation.detail_genus.location_map.LocationAtlas

@Composable
fun CreatureLocationSection(
    creature: IHasLocationInfo,
    iconModifier: Modifier
) {
    val locations = creature.getLocations()

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (locations.isNotEmpty()) {
            SectionLabelRow(
                labelText = stringResource(R.string.label_locations),
                leadingIcon = {
                    Icon(
                        Icons.Filled.LocationOn, null,
                        modifier = iconModifier
                    )
                }
            )
            LocationAtlas(locations, modifier = Modifier.shadow(4.dp))
        }

        // Show the formations below the location list
        if (creature is IHasListOfFormations){
            val formations = creature.getFormations()
            if (formations.isNotEmpty()) {
                if (locations.isNotEmpty()) {
                    Spacer(Modifier.height(8.dp))
                }
                CreatureFormations(formations, iconModifier)
            }
        }
    }
}