package com.bp.dinodata.presentation.detail_genus.card_fragments

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bp.dinodata.R
import com.bp.dinodata.data.IFormation
import com.bp.dinodata.presentation.detail_genus.LabelContentRow

@Composable
fun CreatureFormations(formations: List<IFormation>, iconModifier: Modifier) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        LabelContentRow(
            label = stringResource(R.string.label_formations),
            valueContent = {
            },
            leadingIcon = {
                Icon(
                    painterResource(R.drawable.icon_filled_spade),
                    null,
                    modifier = iconModifier
                )
            }
        )
        formations.forEach {
            Card (
                shape = RoundedCornerShape(8.dp),
                modifier=Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column (
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(12.dp).fillMaxWidth()
                ) {
                    Text(
                        it.getName(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    // Display a comma-separated list of countries containing this country
                    if (it.hasCountries()) {
                        val countries = it.getLocations().joinToString()
                        Text(
                            countries,
                            Modifier.alpha(0.5f),
                            fontSize = 14.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}