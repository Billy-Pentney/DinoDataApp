package com.bp.dinodata.presentation.detail_genus.card_fragments

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bp.dinodata.R
import com.bp.dinodata.data.CreatureType
import com.bp.dinodata.data.genus.IGenusWithImages
import com.bp.dinodata.presentation.convertCreatureTypeToString
import com.bp.dinodata.presentation.utils.convertCreatureTypeToSilhouette

@Composable
fun BasicCreatureInfoSection(
    genus: IGenusWithImages,
    iconModifier: Modifier
) {
    val nameMeaning = genus.getNameMeaning()

    Column (
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        DietCardRow(genus.getDiet())
        CreatureTypeRow(genus.getCreatureType())
//        Row (
//            horizontalArrangement = Arrangement.spacedBy(8.dp),
//            verticalAlignment = Alignment.Top,
//            modifier = Modifier.height(150.dp)
//        ) {
//            DietCardComposable(
//                diet = genus.getDiet(),
//                modifier = Modifier.weight(1f).fillMaxHeight()
//            )
//            CreatureTypeCard(
//                genus.getCreatureType(),
//                modifier = Modifier
//                    .weight(1.3f)
//                    .height(IntrinsicSize.Min)
//            )
//        }
        nameMeaning?.let {
            GenusNameMeaningCard(it)
        }
    }
}


@Composable
fun GenusNameMeaningCard(
    nameMeaning: String,
    modifier: Modifier = Modifier
) {
    Card (
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row (
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.alpha(0.6f)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.MenuBook,
                    contentDescription = null
                )
                Text(
                    stringResource(R.string.label_name_meaning),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            Text(
                nameMeaning,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                fontStyle = FontStyle.Italic,
                modifier = Modifier
                    .alpha(0.8f)
                    .fillMaxWidth()
            )
        }
    }
}



@Composable
fun CreatureTypeCard(
    type: CreatureType,
    modifier: Modifier = Modifier
) {
    val typeName = convertCreatureTypeToString(type)
    val drawableId = convertCreatureTypeToSilhouette(type)

    Card (
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically)
        ) {
            drawableId?.let {
                Image(
                    painterResource(id = drawableId),
                    null,
                    modifier = Modifier
                        .fillMaxHeight(0.5f)
                        .alpha(0.5f)
                        .aspectRatio(2f),
                    colorFilter = ColorFilter.tint(
                        MaterialTheme.colorScheme.onBackground,
                        BlendMode.SrcIn
                    ),
                    contentScale = ContentScale.Fit
                )
            }
            Text(
                typeName ?: "Unknown",
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(0.8f),
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp
            )
            Row (
                modifier = Modifier.alpha(0.6f),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    Icons.Filled.LocalOffer,
                    contentDescription = null
                )
                Text(
                    stringResource(id = R.string.label_creature_type),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun CreatureTypeRow(
    type: CreatureType,
    modifier: Modifier = Modifier
) {
    val typeName = convertCreatureTypeToString(type)
    val drawableId = convertCreatureTypeToSilhouette(type)

    Card (
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Filled.LocalOffer,
                contentDescription = null,
                modifier = Modifier.alpha(0.6f)
            )
            Text(
                stringResource(id = R.string.label_creature_type),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.alpha(0.6f)
            )
            Spacer(modifier=Modifier.weight(1f))
            drawableId?.let {
                Image(
                    painterResource(id = drawableId),
                    null,
                    modifier = Modifier
                        .alpha(0.5f)
                        .aspectRatio(2f)
                        .weight(1f),
                    colorFilter = ColorFilter.tint(
                        MaterialTheme.colorScheme.onBackground,
                        BlendMode.SrcIn
                    ),
                    contentScale = ContentScale.Fit
                )
            }
            Text(
                typeName ?: "Unknown",
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(0.8f),
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp
            )
        }
    }
}

