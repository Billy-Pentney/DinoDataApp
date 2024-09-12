package com.bp.dinodata.presentation.detail_genus.card_fragments

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.filled.Book
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.bp.dinodata.R
import com.bp.dinodata.data.genus.IGenusWithImages
import com.bp.dinodata.presentation.convertCreatureTypeToString
import com.bp.dinodata.presentation.detail_genus.LabelAttributeRow
import com.bp.dinodata.presentation.detail_genus.LabelContentRow
import com.bp.dinodata.presentation.utils.convertCreatureTypeToSilhouette

@Composable
fun CreatureNameMeaningAndType(
    genus: IGenusWithImages,
    iconModifier: Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        LabelAttributeRow(
            label = stringResource(R.string.label_meaning),
            value = genus.getNameMeaning(),
            valueStyle = FontStyle.Italic,
            leadingIcon = {
                Icon(Icons.Filled.Book, null, modifier = iconModifier)
            }
        )
        LabelContentRow(
            label = "Type", //stringResource(R.string.label_creature_type),
            valueContent = {
                val creatureType = genus.getCreatureType()
                val typeName = convertCreatureTypeToString(creatureType)
                val drawableId = convertCreatureTypeToSilhouette(creatureType)
                typeName?.let {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        drawableId?.let {
                            Image(
                                painterResource(id = drawableId),
                                null,
                                modifier = Modifier
                                    .height(18.dp)
                                    .alpha(0.5f),
                                colorFilter = ColorFilter.tint(
                                    MaterialTheme.colorScheme.onBackground,
                                    BlendMode.SrcIn
                                )
                            )
                        }
                        Text(typeName)
                    }
                }
            },
            leadingIcon = {
                Icon(
                    Icons.AutoMirrored.Filled.Label,
                    null,
                    modifier = iconModifier
                )
            }
        )
    }
}