package com.bp.dinodata.presentation.detail_genus.card_fragments

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.bp.dinodata.R
import com.bp.dinodata.data.genus.IDetailedGenus
import com.bp.dinodata.presentation.detail_genus.LabelContentRow

@Composable
fun CreatureTaxonomySection(genus: IDetailedGenus, iconModifier: Modifier) {
    LabelContentRow(
        label = stringResource(R.string.label_taxonomy),
        valueContent = {},
        leadingIcon = {
            Image(
                painterResource(id = R.drawable.icon_filled_taxon_tree),
                null,
                modifier = iconModifier,
                colorFilter = ColorFilter.tint(LocalContentColor.current)
            )
        }
    )
    ShowTaxonomicTree(genus = genus, modifier = Modifier.fillMaxWidth())
}