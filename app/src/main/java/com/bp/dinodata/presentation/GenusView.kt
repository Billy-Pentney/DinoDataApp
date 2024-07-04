package com.bp.dinodata.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bp.dinodata.data.Genus
import com.bp.dinodata.data.GenusBuilderImpl
import com.bp.dinodata.theme.SurfaceGrey


@Composable
fun LabelAttributeRow(
    label: String,
    value: String?
) {
    Row {
        Text("$label: ", modifier=Modifier.alpha(0.6f))
        Text(value ?: "Unknown")
    }
}

@Composable
fun GenusDetail(genus: Genus) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = SurfaceGrey,
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column (
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                genus.name,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                fontStyle = FontStyle.Italic
            )
            genus.namePronunciation?.let {
                Text(it, modifier=Modifier.alpha(0.6f))
            }
            LabelAttributeRow(label = "Meaning", value = genus.nameMeaning)
            HorizontalDivider(Modifier.padding(8.dp).alpha(0.4f))
            LabelAttributeRow(label = "Diet", value = genus.diet.toString())
            LabelAttributeRow(label = "Length", value = genus.length)
            LabelAttributeRow(label = "Weight", value = genus.weight)
            HorizontalDivider(Modifier.padding(8.dp).alpha(0.4f))
            LabelAttributeRow(label = "Time Period", value = genus.timePeriod)
            LabelAttributeRow(label = "Years Lived", value = genus.yearsLived)
        }
    }
}

@Preview(widthDp = 300, heightDp = 1000)
@Composable
fun PreviewGenusDetail() {
    val acro = GenusBuilderImpl("Acrocanthosaurus")
        .setDiet("Carnivorous")
        .splitTimePeriodAndYears("Early Cretaceous, 113-110 mya")
        .setNamePronunciation("ACK-row-CAN-thow-SORE-us")
        .setNameMeaning("high-spined lizard")
        .setLength("11-11.5 metres")
        .setWeight("4.4-6.6 metric tons")
        .build()

    GenusDetail(acro)
}
