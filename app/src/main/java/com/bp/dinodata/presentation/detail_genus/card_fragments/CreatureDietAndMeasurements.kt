package com.bp.dinodata.presentation.detail_genus.card_fragments

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Restaurant
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.bp.dinodata.R
import com.bp.dinodata.data.Diet
import com.bp.dinodata.data.quantities.IDescribesLength
import com.bp.dinodata.data.quantities.IDescribesWeight
import com.bp.dinodata.presentation.detail_genus.LabelAttributeRow
import com.bp.dinodata.presentation.detail_genus.LabelContentRow
import com.bp.dinodata.presentation.icons.DietIconThin

@Composable
fun CreatureDietAndMeasurements(
    diet: Diet?,
    length: IDescribesLength?,
    weight: IDescribesWeight?,
    iconModifier: Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        LabelContentRow(
            label = stringResource(R.string.label_diet),
            valueContent = { DietIconThin(diet) },
            leadingIcon = {
                Icon(
                    Icons.Sharp.Restaurant,
                    contentDescription = null,
                    modifier = iconModifier
                )
            }
        )
        length?.let {
            LabelAttributeRow(
                label = stringResource(R.string.label_length),
                value = length.toString(),
                leadingIcon = {
                    Image(
                        painterResource(R.drawable.icon_filled_ruler),
                        null,
                        modifier = iconModifier,
                        colorFilter = ColorFilter.tint(LocalContentColor.current)
                    )
                }
            )
        }
        weight?.let {
            LabelAttributeRow(
                label = stringResource(R.string.label_weight),
                value = weight.toString(),
                leadingIcon = {
                    Image(
                        painterResource(R.drawable.icon_filled_weight),
                        null,
                        modifier = iconModifier,
                        colorFilter = ColorFilter.tint(LocalContentColor.current)
                    )
                }
            )
        }
    }
}