package com.bp.dinodata.presentation.detail_creature_type

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
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
import com.bp.dinodata.data.attributions.IResourceAttribution
import com.bp.dinodata.data.attributions.ResourceAttribution
import com.bp.dinodata.presentation.convertCreatureTypeToString
import com.bp.dinodata.presentation.utils.convertCreatureTypeToSilhouette
import com.bp.dinodata.theme.DinoDataTheme

data class TextWithAttribution(
    val text: String,
    val attribution: IResourceAttribution? = null
) {
    fun getTextAttribution(): String? {
        return attribution?.let {
            "Text provided by ${attribution.describe()}"
        }
    }
}


@Composable
fun convertCreatureTypeToDescription(creatureType: CreatureType): TextWithAttribution {
    val id = when (creatureType) {
        CreatureType.LargeTheropod -> R.string.todo
        CreatureType.SmallTheropod -> R.string.todo
        CreatureType.MediumTheropod -> R.string.todo
        CreatureType.Ceratopsian -> R.string.class_ceratopsian_description
        CreatureType.Ankylosaur -> R.string.class_ankylosaur_description
        CreatureType.Stegosaur -> R.string.class_stegosaurid_description
        CreatureType.Sauropod -> R.string.class_sauropod_description
        CreatureType.Hadrosaur -> R.string.class_hadrosaur_description
        CreatureType.Pachycephalosaur -> R.string.class_pachycephalosaurid_description
        CreatureType.Ornithomimid -> R.string.class_ornithomimid_description
        CreatureType.Pterosaur -> R.string.class_pterosaur_description
        CreatureType.Spinosaur -> R.string.class_spinosaurid_description
        CreatureType.Plesiosaur -> R.string.class_plesiosaur_description
        CreatureType.Synapsid -> R.string.class_synapsid_description
        CreatureType.SynapsidWithSail -> R.string.class_sailed_synapsid_description
        CreatureType.Dromaeosaurid -> R.string.class_dromaeosaurid_description
        CreatureType.Carcharodontosaurid -> R.string.class_carcharodontosaurid_description
        CreatureType.Abelisaurid -> R.string.class_abelisaurid_description
        CreatureType.Tyrannosaurid -> R.string.class_tyrannosaurid_description
        CreatureType.Therizinosaurid -> R.string.class_therizinosaurid_description
        CreatureType.Iguanodontian -> R.string.class_iguanodontian_description
        CreatureType.Ichthyosaur -> R.string.class_ichthyosaur_description
        CreatureType.Mosasaur -> R.string.class_mosasaur_description
        CreatureType.Crocodilian -> R.string.class_crocodilian_description
        CreatureType.Other -> R.string.todo
        CreatureType.Aquatic -> R.string.todo
        CreatureType.Serpent -> R.string.todo
    }

    if (id != R.string.todo) {
        return TextWithAttribution(
            stringResource(id),
            ResourceAttribution.ChatGPT
        )
    }
    return TextWithAttribution(stringResource(id))
}


@Composable
fun DetailCreatureTypeDialog(
    creatureType: CreatureType,
    attribution: ResourceAttribution = ResourceAttribution.ChatGPT,
    onClose: () -> Unit
) {
    val typeName = convertCreatureTypeToString(creatureType)
    val typeImage = convertCreatureTypeToSilhouette(creatureType)
    val descriptionWithAttribution = convertCreatureTypeToDescription(creatureType)

    val description = descriptionWithAttribution.text
    val attribution = descriptionWithAttribution.getTextAttribution()

    AlertDialog(
        onDismissRequest = { onClose() },
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
                    description,
                    textAlign = TextAlign.Justify,
                    modifier = Modifier.alpha(0.75f)
                )
                Spacer(Modifier.height(12.dp))

                attribution?.let {
                    Text(
                        attribution,
                        modifier = Modifier
                            .alpha(0.4f)
                            .align(Alignment.End)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onClose,
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
    val type = CreatureType.Ceratopsian

    DinoDataTheme (darkTheme = true) {
        DetailCreatureTypeDialog(creatureType = type, onClose = {})
    }
}