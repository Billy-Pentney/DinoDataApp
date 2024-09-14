package com.bp.dinodata.presentation.detail_creature_type

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.bp.dinodata.R
import com.bp.dinodata.data.CreatureType
import com.bp.dinodata.data.attributions.ResourceAttribution
import com.bp.dinodata.presentation.convertCreatureTypeToString
import com.bp.dinodata.presentation.utils.convertCreatureTypeToSilhouette
import com.bp.dinodata.theme.DinoDataTheme

@Composable
fun convertCreatureTypeToDescription(creatureType: CreatureType): String {
    val id = when (creatureType) {
        CreatureType.LargeTheropod -> TODO()
        CreatureType.SmallTheropod -> TODO()
        CreatureType.MediumTheropod -> TODO()
        CreatureType.Ceratopsian -> R.string.class_ceratopsian_description
        CreatureType.Ankylosaur -> TODO()
        CreatureType.Stegosaur -> R.string.class_stegosaurid_description
        CreatureType.Sauropod -> TODO()
        CreatureType.Hadrosaur -> TODO()
        CreatureType.Pachycephalosaur -> TODO()
        CreatureType.Ornithomimid -> TODO()
        CreatureType.Pterosaur -> TODO()
        CreatureType.Spinosaur -> TODO()
        CreatureType.Aquatic -> TODO()
        CreatureType.Plesiosaur -> TODO()
        CreatureType.Cenezoic -> TODO()
        CreatureType.Synapsid -> R.string.class_synapsid_description
        CreatureType.SynapsidWithSail -> R.string.class_sailed_synapsid_description
        CreatureType.Other -> TODO()
        CreatureType.Dromaeosaurid -> TODO()
        CreatureType.Carcharodontosaurid -> TODO()
        CreatureType.Abelisaurid -> R.string.class_abelisaurid_description
        CreatureType.Euornithopod -> TODO()
        CreatureType.Tyrannosaurid -> TODO()
        CreatureType.Therizinosaurid -> TODO()
        CreatureType.Iguanodontid -> TODO()
        CreatureType.Crocodilian -> TODO()
        CreatureType.Serpent -> TODO()
        CreatureType.Ichthyosaur -> R.string.class_ichthyosaur_description
        CreatureType.Mosasaur -> TODO()
    }
    return stringResource(id = id)
}


@Composable
fun DetailCreatureTypeDialog(
    creatureType: CreatureType,
    attribution: ResourceAttribution = ResourceAttribution.ChatGPT,
) {
    val typeName = convertCreatureTypeToString(creatureType)
    val typeImage = convertCreatureTypeToSilhouette(creatureType)
    val typeDescription = convertCreatureTypeToDescription(creatureType)

    var dialogVisible by remember { mutableStateOf(true) }

    if (!dialogVisible) {
        return
    }

    AlertDialog(
        onDismissRequest = { dialogVisible = false },
        properties = DialogProperties(dismissOnClickOutside = true),
        title = {
            Column(
                modifier = Modifier.padding(8.dp),
                horizontalAlignment = Alignment.Start,
            ) {
                Spacer(Modifier.height(16.dp))
                Image(
                    painter = painterResource(id = typeImage),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
                    modifier = Modifier.alpha(0.7f)
                )
            }
        },
        text = {
            Column (
                modifier = Modifier.padding(8.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    stringResource(id = R.string.label_creature_type),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.alpha(0.5f)
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    typeName,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(16.dp))
                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth(0.4f)
                        .alpha(0.5f),
                    thickness = 2.dp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(24.dp))
                Text(
                    typeDescription,
                    textAlign = TextAlign.Justify,
                    modifier = Modifier.alpha(0.75f)
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    "Text provided by ${attribution.describe()}",
                    modifier = Modifier.alpha(0.4f).align(Alignment.End)
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {},
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(stringResource(id = R.string.action_done))
            }
        }
    )
}


@Composable
@Preview
fun Preview_CreatureTypeDetail() {
    val types = listOf(
        CreatureType.Ichthyosaur,
        CreatureType.Abelisaurid,
        CreatureType.SynapsidWithSail
    )
    
    DinoDataTheme (darkTheme = true) {
        DetailCreatureTypeDialog(creatureType = types[0])
    }
}